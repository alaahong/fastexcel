package cn.idev.excel.test.core.excludeorinclude;

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
public class ExcludeOrIncludeData {
    @ExcelProperty(order = 1)
    private String column1;

    @ExcelProperty(order = 2)
    private String column2;

    @ExcelProperty(order = 3)
    private String column3;

    @ExcelProperty(order = 4)
    private String column4;
}
