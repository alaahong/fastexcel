package cn.idev.excel.temp.issue580;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.converters.WriteConverterContext;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.data.ImageData;
import cn.idev.excel.metadata.data.WriteCellData;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

/**
 * 将 URL 转为图片写入 Excel；下载失败则“吞异常并返回空单元格”。
 * <p>
 * 使用方式：
 * 1) 在字段上通过 @ExcelProperty(..., converter = UrlImageOrEmptyConverter.class)
 * 2) 或在 writer 上 registerConverter(new UrlImageOrEmptyConverter())
 */
public class UrlImageOrEmptyConverter implements Converter<URL> {

    // 可根据需要调整超时与最大下载大小，避免阻塞或拉过大的文件
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 2000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 3000;
    // 防御性限制，避免将异常大的图片塞入内存（比如 10MB）
    private static final int MAX_BYTES = 10 * 1024 * 1024;

    @Override
    public Class<URL> supportJavaTypeKey() {
        return URL.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<URL> context) {
        URL url = context.getValue();
        if (url == null) {
            // 空值 -> 空单元格
            return emptyCell();
        }

        try {
            byte[] imageBytes = tryDownload(url, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS, MAX_BYTES);
            if (imageBytes == null || imageBytes.length == 0) {
                return emptyCell(); // 下载失败或为空，回退为空单元格
            }

            // 识别图片类型（简单基于签名/扩展名/Content-Type）
            ImageData.ImageType imageType = detectImageType(url, imageBytes);

            // 返回图片单元格
            WriteCellData<Void> cellData = new WriteCellData<>();
            cellData.setType(CellDataTypeEnum.EMPTY); // 由 imageDataList 驱动为图片
            ImageData imageData = new ImageData();
            imageData.setImage(imageBytes);
            imageData.setImageType(imageType);
            cellData.setImageDataList(Collections.singletonList(imageData));
            return cellData;
        } catch (Exception ignored) {
            // 吞掉所有异常，返回空单元格，保证整行数据可写入
            return emptyCell();
        }
    }

    private static WriteCellData<String> emptyCell() {
        // 返回空字符串最稳妥，可确保不会触发写入异常
        return new WriteCellData<>("");
    }

    private static byte[] tryDownload(URL url, int connectTimeoutMs, int readTimeoutMs, int maxBytes) {
        try {
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(connectTimeoutMs);
            conn.setReadTimeout(readTimeoutMs);
            // 某些服务端需要 UA，按需设置
            conn.setRequestProperty("User-Agent", "FastExcel-URL-Image-Converter/1.0");
            try (InputStream is = conn.getInputStream();
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int n;
                int total = 0;
                while ((n = is.read(buf)) != -1) {
                    bos.write(buf, 0, n);
                    total += n;
                    if (total > maxBytes) {
                        // 超过大小限制，放弃
                        return null;
                    }
                }
                return bos.toByteArray();
            }
        } catch (Exception e) {
            return null; // 出错即回退
        }
    }

    private static ImageData.ImageType detectImageType(URL url, byte[] bytes) {
        // 先用魔数检测
        ImageData.ImageType byMagic = detectByMagic(bytes);
        if (byMagic != null) return byMagic;

        // 再用扩展名猜测
        String path = url.getPath().toLowerCase();
        if (path.endsWith(".png")) return ImageData.ImageType.PICTURE_TYPE_PNG;
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return ImageData.ImageType.PICTURE_TYPE_JPEG;
        // 兜底 JPEG
        return ImageData.ImageType.PICTURE_TYPE_JPEG;
    }

    private static ImageData.ImageType detectByMagic(byte[] bytes) {
        if (bytes.length >= 8) {
            // PNG: 89 50 4E 47 0D 0A 1A 0A
            if ((bytes[0] & 0xFF) == 0x89 &&
                    (bytes[1] & 0xFF) == 0x50 &&
                    (bytes[2] & 0xFF) == 0x4E &&
                    (bytes[3] & 0xFF) == 0x47 &&
                    (bytes[4] & 0xFF) == 0x0D &&
                    (bytes[5] & 0xFF) == 0x0A &&
                    (bytes[6] & 0xFF) == 0x1A &&
                    (bytes[7] & 0xFF) == 0x0A) {
                return ImageData.ImageType.PICTURE_TYPE_PNG;
            }
        }
        if (bytes.length >= 3) {
            // JPEG: FF D8, 结尾 FF D9（只检查开头）
            if ((bytes[0] & 0xFF) == 0xFF &&
                    (bytes[1] & 0xFF) == 0xD8) {
                return ImageData.ImageType.PICTURE_TYPE_JPEG;
            }
        }
        return null;
    }
}
