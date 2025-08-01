package cn.idev.excel.converters.booleanconverter;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import java.math.BigDecimal;

/**
 * Boolean and number converter
 *
 *
 */
public class BooleanNumberConverter implements Converter<Boolean> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Boolean.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public Boolean convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (BigDecimal.ONE.compareTo(cellData.getNumberValue()) == 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            Boolean value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value) {
            return new WriteCellData<>(BigDecimal.ONE);
        }
        return new WriteCellData<>(BigDecimal.ZERO);
    }
}
