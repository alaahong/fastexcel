package cn.idev.excel.test.demo.web;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 基础数据类
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class UploadData {
    private String string;
    private Date date;
    private Double doubleData;
}
