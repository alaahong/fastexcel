package cn.idev.excel.test.demo.write;

import cn.idev.excel.annotation.ExcelProperty;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 复杂头数据.这里最终效果是第一行就一个主标题，第二行分类
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class ComplexHeadData {
    @ExcelProperty({"主标题", "字符串标题"})
    private String string;

    @ExcelProperty({"主标题", "日期标题"})
    private Date date;

    @ExcelProperty({"主标题", "数字标题"})
    private Double doubleData;
}
