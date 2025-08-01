package cn.idev.excel.read.builder;

import cn.idev.excel.ExcelReader;
import cn.idev.excel.cache.ReadCache;
import cn.idev.excel.cache.selector.ReadCacheSelector;
import cn.idev.excel.cache.selector.SimpleReadCacheSelector;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.enums.CellExtraTypeEnum;
import cn.idev.excel.enums.ReadDefaultReturnEnum;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.event.SyncReadListener;
import cn.idev.excel.read.listener.ModelBuildEventListener;
import cn.idev.excel.read.metadata.ReadWorkbook;
import cn.idev.excel.support.ExcelTypeEnum;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;

/**
 * Build ExcelReader
 *
 *
 */
public class ExcelReaderBuilder extends AbstractExcelReaderParameterBuilder<ExcelReaderBuilder, ReadWorkbook> {
    /**
     * Workbook
     */
    private final ReadWorkbook readWorkbook;

    public ExcelReaderBuilder() {
        this.readWorkbook = new ReadWorkbook();
    }

    public ExcelReaderBuilder excelType(ExcelTypeEnum excelType) {
        readWorkbook.setExcelType(excelType);
        return this;
    }

    /**
     * Read InputStream
     * <p>
     * If 'inputStream' and 'file' all not empty, file first
     */
    public ExcelReaderBuilder file(InputStream inputStream) {
        readWorkbook.setInputStream(inputStream);
        return this;
    }

    /**
     * Read file
     * <p>
     * If 'inputStream' and 'file' all not empty, file first
     */
    public ExcelReaderBuilder file(File file) {
        readWorkbook.setFile(file);
        return this;
    }

    /**
     * Read file
     * <p>
     * If 'inputStream' and 'file' all not empty, file first
     */
    public ExcelReaderBuilder file(String pathName) {
        return file(new File(pathName));
    }

    /**
     * charset.
     * Only work on the CSV file
     */
    public ExcelReaderBuilder charset(Charset charset) {
        readWorkbook.setCharset(charset);
        return this;
    }

    /**
     * Mandatory use 'inputStream' .Default is false.
     * <p>
     * if false, Will transfer 'inputStream' to temporary files to improve efficiency
     */
    public ExcelReaderBuilder mandatoryUseInputStream(Boolean mandatoryUseInputStream) {
        readWorkbook.setMandatoryUseInputStream(mandatoryUseInputStream);
        return this;
    }

    /**
     * Default true
     *
     * @param autoCloseStream
     * @return
     */
    public ExcelReaderBuilder autoCloseStream(Boolean autoCloseStream) {
        readWorkbook.setAutoCloseStream(autoCloseStream);
        return this;
    }

    /**
     * Ignore empty rows.Default is true.
     *
     * @param ignoreEmptyRow
     * @return
     */
    public ExcelReaderBuilder ignoreEmptyRow(Boolean ignoreEmptyRow) {
        readWorkbook.setIgnoreEmptyRow(ignoreEmptyRow);
        return this;
    }

    /**
     * This object can be read in the Listener {@link AnalysisEventListener#invoke(Object, AnalysisContext)}
     * {@link AnalysisContext#getCustom()}
     *
     * @param customObject
     * @return
     */
    public ExcelReaderBuilder customObject(Object customObject) {
        readWorkbook.setCustomObject(customObject);
        return this;
    }

    /**
     * A cache that stores temp data to save memory.
     *
     * @param readCache
     * @return
     */
    public ExcelReaderBuilder readCache(ReadCache readCache) {
        readWorkbook.setReadCache(readCache);
        return this;
    }

    /**
     * Select the cache.Default use {@link SimpleReadCacheSelector}
     *
     * @param readCacheSelector
     * @return
     */
    public ExcelReaderBuilder readCacheSelector(ReadCacheSelector readCacheSelector) {
        readWorkbook.setReadCacheSelector(readCacheSelector);
        return this;
    }

    /**
     * Whether the encryption
     *
     * @param password
     * @return
     */
    public ExcelReaderBuilder password(String password) {
        readWorkbook.setPassword(password);
        return this;
    }

    /**
     * SAXParserFactory used when reading xlsx.
     * <p>
     * The default will automatically find.
     * <p>
     * Please pass in the name of a class ,like : "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
     *
     * @param xlsxSAXParserFactoryName
     * @return
     * @see SAXParserFactory#newInstance()
     * @see SAXParserFactory#newInstance(String, ClassLoader)
     */
    public ExcelReaderBuilder xlsxSAXParserFactoryName(String xlsxSAXParserFactoryName) {
        readWorkbook.setXlsxSAXParserFactoryName(xlsxSAXParserFactoryName);
        return this;
    }

    /**
     * Read some extra information, not by default
     *
     * @param extraType extra information type
     * @return
     */
    public ExcelReaderBuilder extraRead(CellExtraTypeEnum extraType) {
        if (readWorkbook.getExtraReadSet() == null) {
            readWorkbook.setExtraReadSet(new HashSet<CellExtraTypeEnum>());
        }
        readWorkbook.getExtraReadSet().add(extraType);
        return this;
    }

    /**
     * Whether to use the default listener, which is used by default.
     * <p>
     * The {@link ModelBuildEventListener} is loaded by default to convert the object.
     *
     * @param useDefaultListener
     * @return
     */
    public ExcelReaderBuilder useDefaultListener(Boolean useDefaultListener) {
        readWorkbook.setUseDefaultListener(useDefaultListener);
        return this;
    }

    /**
     * Read not to {@code cn.idev.excel.metadata.BasicParameter#clazz} value, the default will return type.
     * Is only effective when set `useDefaultListener=true` or `useDefaultListener=null`.
     *
     * @see ReadDefaultReturnEnum
     */
    public ExcelReaderBuilder readDefaultReturn(ReadDefaultReturnEnum readDefaultReturn) {
        readWorkbook.setReadDefaultReturn(readDefaultReturn);
        return this;
    }

    public ExcelReaderBuilder numRows(Integer numRows) {
        readWorkbook.setNumRows(numRows);
        return this;
    }

    public ExcelReader build() {
        return new ExcelReader(readWorkbook);
    }

    public void doReadAll() {
        try (ExcelReader excelReader = build()) {
            excelReader.readAll();
        }
    }

    /**
     * Synchronous reads return results
     *
     * @return
     */
    public <T> List<T> doReadAllSync() {
        SyncReadListener syncReadListener = new SyncReadListener();
        registerReadListener(syncReadListener);
        try (ExcelReader excelReader = build()) {
            excelReader.readAll();
            excelReader.finish();
        }
        return (List<T>) syncReadListener.getList();
    }

    public ExcelReaderSheetBuilder sheet() {
        return sheet(null, null);
    }

    public ExcelReaderSheetBuilder sheet(Integer sheetNo) {
        return sheet(sheetNo, null);
    }

    public ExcelReaderSheetBuilder sheet(String sheetName) {
        return sheet(null, sheetName);
    }

    public ExcelReaderSheetBuilder sheet(Integer sheetNo, String sheetName) {
        ExcelReaderSheetBuilder excelReaderSheetBuilder = new ExcelReaderSheetBuilder(build());
        if (sheetNo != null) {
            excelReaderSheetBuilder.sheetNo(sheetNo);
        }
        if (sheetName != null) {
            excelReaderSheetBuilder.sheetName(sheetName);
        }
        return excelReaderSheetBuilder;
    }

    public CsvReaderBuilder csv() {
        excelType(ExcelTypeEnum.CSV);
        return new CsvReaderBuilder(parameter());
    }

    @Override
    protected ReadWorkbook parameter() {
        return readWorkbook;
    }

    /**
     * Ignore hiddene sheet.Default is false.
     *
     * @param ignoreHiddenSheet
     * @return
     */
    public ExcelReaderBuilder ignoreHiddenSheet(Boolean ignoreHiddenSheet) {
        readWorkbook.setIgnoreHiddenSheet(ignoreHiddenSheet);
        return this;
    }
}
