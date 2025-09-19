package cn.idev.excel.temp.issue580;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.net.URL;

@Data
public class AlarmInfoExportVO {

    @ExcelProperty(index = 0, value = "告警内容")
    private String alarmLevel;

    // 方案 C：仍然使用 URL 类型，通过 Converter 控制写入行为
    // 1) 下载成功 -> 插入图片
    // 2) 下载失败 -> 空单元格（或回退为文本，取决于你选用的 Converter）
    @ExcelProperty(index = 1, value = "图片", converter = UrlImageOrEmptyConverter.class)
    private URL pic;

}
