package cn.idev.excel.converters.localdate;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import cn.idev.excel.util.DateUtils;
import java.text.ParseException;
import java.time.LocalDate;

/**
 * LocalDate and string converter
 *
 *
 */
public class LocalDateStringConverter implements Converter<LocalDate> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDate convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws ParseException {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return DateUtils.parseLocalDate(cellData.getStringValue(), null, globalConfiguration.getLocale());
        } else {
            return DateUtils.parseLocalDate(
                    cellData.getStringValue(),
                    contentProperty.getDateTimeFormatProperty().getFormat(),
                    globalConfiguration.getLocale());
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            LocalDate value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new WriteCellData<>(DateUtils.format(value, null, globalConfiguration.getLocale()));
        } else {
            return new WriteCellData<>(DateUtils.format(
                    value, contentProperty.getDateTimeFormatProperty().getFormat(), globalConfiguration.getLocale()));
        }
    }
}
