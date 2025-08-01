package cn.idev.excel.test.core.template;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class TemplateData {
    @ExcelProperty("字符串0")
    private String string0;

    @ExcelProperty("字符串1")
    private String string1;
}
