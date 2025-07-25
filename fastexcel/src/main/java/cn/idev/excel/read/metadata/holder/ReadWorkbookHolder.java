package cn.idev.excel.read.metadata.holder;

import cn.idev.excel.cache.ReadCache;
import cn.idev.excel.cache.selector.EternalReadCacheSelector;
import cn.idev.excel.cache.selector.ReadCacheSelector;
import cn.idev.excel.cache.selector.SimpleReadCacheSelector;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.enums.CellExtraTypeEnum;
import cn.idev.excel.enums.HolderEnum;
import cn.idev.excel.enums.ReadDefaultReturnEnum;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.exception.ExcelAnalysisException;
import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.read.metadata.ReadWorkbook;
import cn.idev.excel.support.ExcelTypeEnum;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Workbook holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ReadWorkbookHolder extends AbstractReadHolder {

    /**
     * current param
     */
    private ReadWorkbook readWorkbook;
    /**
     * Read InputStream
     * <p>
     * If 'inputStream' and 'file' all not empty, file first
     */
    private InputStream inputStream;
    /**
     * Read file
     * <p>
     * If 'inputStream' and 'file' all not empty, file first
     */
    private File file;

    /**
     * charset.
     * Only work on the CSV file
     */
    private Charset charset;
    /**
     * Mandatory use 'inputStream' .Default is false.
     * <p>
     * if false, Will transfer 'inputStream' to temporary files to improve efficiency
     */
    private Boolean mandatoryUseInputStream;

    /**
     * Default true
     */
    private Boolean autoCloseStream;

    /**
     * Read not to {@code cn.idev.excel.metadata.BasicParameter#clazz} value, the default will return type.
     * Is only effective when set `useDefaultListener=true` or `useDefaultListener=null`.
     *
     * @see ReadDefaultReturnEnum
     */
    private ReadDefaultReturnEnum readDefaultReturn;

    /**
     * Excel type
     */
    private ExcelTypeEnum excelType;
    /**
     * This object can be read in the Listener {@link AnalysisEventListener#invoke(Object, AnalysisContext)}
     * {@link AnalysisContext#getCustom()}
     */
    private Object customObject;
    /**
     * Ignore empty rows.Default is true.
     */
    private Boolean ignoreEmptyRow;
    /**
     * A cache that stores temp data to save memory.
     */
    private ReadCache readCache;
    /**
     * Select the cache.Default use {@link SimpleReadCacheSelector}
     */
    private ReadCacheSelector readCacheSelector;
    /**
     * Temporary files when reading excel
     */
    private File tempFile;
    /**
     * Whether the encryption
     */
    private String password;
    /**
     * Read some additional fields. None are read by default.
     *
     * @see CellExtraTypeEnum
     */
    private Set<CellExtraTypeEnum> extraReadSet;
    /**
     * Actual sheet data
     */
    private List<ReadSheet> actualSheetDataList;
    /**
     * Parameter sheet data
     */
    private List<ReadSheet> parameterSheetDataList;
    /**
     * Read all
     */
    private Boolean readAll;

    /**
     * Prevent repeating sheet
     */
    private Set<Integer> hasReadSheet;
    /**
     * Ignore hidden sheet.Default is false.
     */
    private Boolean ignoreHiddenSheet;

    public ReadWorkbookHolder(ReadWorkbook readWorkbook) {
        super(readWorkbook, null);
        this.readWorkbook = readWorkbook;
        if (readWorkbook.getInputStream() != null) {
            this.inputStream = readWorkbook.getInputStream();
        }
        this.file = readWorkbook.getFile();

        if (readWorkbook.getCharset() == null) {
            this.charset = Charset.defaultCharset();
        } else {
            this.charset = readWorkbook.getCharset();
        }

        if (readWorkbook.getMandatoryUseInputStream() == null) {
            this.mandatoryUseInputStream = Boolean.FALSE;
        } else {
            this.mandatoryUseInputStream = readWorkbook.getMandatoryUseInputStream();
        }
        if (readWorkbook.getAutoCloseStream() == null) {
            this.autoCloseStream = Boolean.TRUE;
        } else {
            this.autoCloseStream = readWorkbook.getAutoCloseStream();
        }

        if (readWorkbook.getReadDefaultReturn() == null) {
            this.readDefaultReturn = ReadDefaultReturnEnum.STRING;
        } else {
            this.readDefaultReturn = readWorkbook.getReadDefaultReturn();
        }

        this.customObject = readWorkbook.getCustomObject();
        if (readWorkbook.getIgnoreEmptyRow() == null) {
            this.ignoreEmptyRow = Boolean.TRUE;
        } else {
            this.ignoreEmptyRow = readWorkbook.getIgnoreEmptyRow();
        }
        if (readWorkbook.getReadCache() != null) {
            if (readWorkbook.getReadCacheSelector() != null) {
                throw new ExcelAnalysisException("'readCache' and 'readCacheSelector' only one choice.");
            }
            this.readCacheSelector = new EternalReadCacheSelector(readWorkbook.getReadCache());
        } else {
            if (readWorkbook.getReadCacheSelector() == null) {
                this.readCacheSelector = new SimpleReadCacheSelector();
            } else {
                this.readCacheSelector = readWorkbook.getReadCacheSelector();
            }
        }
        if (readWorkbook.getExtraReadSet() == null) {
            this.extraReadSet = new HashSet<CellExtraTypeEnum>();
        } else {
            this.extraReadSet = readWorkbook.getExtraReadSet();
        }
        if (readWorkbook.getIgnoreHiddenSheet() == null) {
            this.ignoreHiddenSheet = Boolean.FALSE;
        } else {
            this.ignoreHiddenSheet = readWorkbook.getIgnoreHiddenSheet();
        }
        this.hasReadSheet = new HashSet<Integer>();
        this.password = readWorkbook.getPassword();
    }

    @Override
    public HolderEnum holderType() {
        return HolderEnum.WORKBOOK;
    }
}
