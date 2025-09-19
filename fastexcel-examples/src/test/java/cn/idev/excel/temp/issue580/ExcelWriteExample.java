package cn.idev.excel.temp.issue580;


import cn.idev.excel.write.builder.ExcelWriterBuilder;
import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.idev.excel.FastExcel.write;

public class ExcelWriteExample {

    public static void main(String[] args) throws Exception {
        File file = new File("export.xlsx");
        List<AlarmInfoExportVO> data = Arrays.asList(
                build("正常", "https://www.ianzhang.cn/images/2020-04-06_21_13_43-Window.png"),
                build("告警一", "http://172.31.228.28:59000/path/to/a.jpg"),
                build("告警二", "http://unreachable-host/path/to/b.png")
        );

        write(file, AlarmInfoExportVO.class)
                .sheet("Sheet1")
                .doWrite(data);
        System.out.println(file.getAbsolutePath());
        // 方式 2：全局注册 Converter（若模型上未指定 converter）
        // ExcelWriterBuilder builder = write(new File("export2.xlsx"), AlarmInfoExportVO.class);
        // builder.registerConverter(new UrlImageOrEmptyConverter()); // 或 new UrlImageOrTextFallbackConverter()
        // ExcelWriterSheetBuilder sheet = builder.sheet("Sheet1");
        // sheet.doWrite(data);
    }

    private static AlarmInfoExportVO build(String level, String url) {
        AlarmInfoExportVO vo = new AlarmInfoExportVO();
        vo.setAlarmLevel(level);
        try {
            vo.setPic(new URL(url));
        } catch (Exception e) {
            vo.setPic(null);
        }
        vo.setPictureUrl(url);
        return vo;
    }
}
