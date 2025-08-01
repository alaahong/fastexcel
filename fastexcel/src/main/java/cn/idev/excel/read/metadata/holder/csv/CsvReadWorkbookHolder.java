package cn.idev.excel.read.metadata.holder.csv;

import cn.idev.excel.read.metadata.ReadWorkbook;
import cn.idev.excel.read.metadata.holder.ReadWorkbookHolder;
import cn.idev.excel.support.ExcelTypeEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

/**
 * Workbook holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class CsvReadWorkbookHolder extends ReadWorkbookHolder {

    private CSVFormat csvFormat;
    private CSVParser csvParser;

    public CsvReadWorkbookHolder(ReadWorkbook readWorkbook) {
        super(readWorkbook);
        setExcelType(ExcelTypeEnum.CSV);
        this.csvFormat = readWorkbook.getCsvFormat() == null ? CSVFormat.DEFAULT : readWorkbook.getCsvFormat();
    }
}
