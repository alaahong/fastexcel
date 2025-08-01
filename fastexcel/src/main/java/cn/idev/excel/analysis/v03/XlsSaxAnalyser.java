package cn.idev.excel.analysis.v03;

import cn.idev.excel.analysis.ExcelReadExecutor;
import cn.idev.excel.analysis.v03.handlers.BlankRecordHandler;
import cn.idev.excel.analysis.v03.handlers.BofRecordHandler;
import cn.idev.excel.analysis.v03.handlers.BoolErrRecordHandler;
import cn.idev.excel.analysis.v03.handlers.BoundSheetRecordHandler;
import cn.idev.excel.analysis.v03.handlers.DateWindow1904RecordHandler;
import cn.idev.excel.analysis.v03.handlers.DummyRecordHandler;
import cn.idev.excel.analysis.v03.handlers.EofRecordHandler;
import cn.idev.excel.analysis.v03.handlers.FormulaRecordHandler;
import cn.idev.excel.analysis.v03.handlers.HyperlinkRecordHandler;
import cn.idev.excel.analysis.v03.handlers.IndexRecordHandler;
import cn.idev.excel.analysis.v03.handlers.LabelRecordHandler;
import cn.idev.excel.analysis.v03.handlers.LabelSstRecordHandler;
import cn.idev.excel.analysis.v03.handlers.MergeCellsRecordHandler;
import cn.idev.excel.analysis.v03.handlers.NoteRecordHandler;
import cn.idev.excel.analysis.v03.handlers.NumberRecordHandler;
import cn.idev.excel.analysis.v03.handlers.ObjRecordHandler;
import cn.idev.excel.analysis.v03.handlers.RkRecordHandler;
import cn.idev.excel.analysis.v03.handlers.SstRecordHandler;
import cn.idev.excel.analysis.v03.handlers.StringRecordHandler;
import cn.idev.excel.analysis.v03.handlers.TextObjectRecordHandler;
import cn.idev.excel.context.xls.XlsReadContext;
import cn.idev.excel.exception.ExcelAnalysisException;
import cn.idev.excel.exception.ExcelAnalysisStopException;
import cn.idev.excel.exception.ExcelAnalysisStopSheetException;
import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.read.metadata.holder.xls.XlsReadWorkbookHolder;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.IndexRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.TextObjectRecord;

/**
 * A text extractor for Excel files.
 * <p>
 * Returns the textual content of the file, suitable for indexing by something like Lucene, but not really intended for
 * display to the user.
 * </p>
 *
 * <p>
 * To turn an excel file into a CSV or similar, then see the XLS2CSVmra example
 * </p>
 *
 * @see <a href="http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/hssf/eventusermodel/examples/XLS2CSVmra.java">XLS2CSVmra</a>
 */
@Slf4j
public class XlsSaxAnalyser implements HSSFListener, ExcelReadExecutor {

    private static final short DUMMY_RECORD_SID = -1;
    private final XlsReadContext xlsReadContext;
    private static final Map<Short, XlsRecordHandler> XLS_RECORD_HANDLER_MAP = new HashMap<Short, XlsRecordHandler>(32);

    static {
        // Initialize a map of record handlers to process different types of Excel records.
        XLS_RECORD_HANDLER_MAP.put(BlankRecord.sid, new BlankRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(BOFRecord.sid, new BofRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(BoolErrRecord.sid, new BoolErrRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(BoundSheetRecord.sid, new BoundSheetRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(DUMMY_RECORD_SID, new DummyRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(EOFRecord.sid, new EofRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(FormulaRecord.sid, new FormulaRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(HyperlinkRecord.sid, new HyperlinkRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(IndexRecord.sid, new IndexRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(LabelRecord.sid, new LabelRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(LabelSSTRecord.sid, new LabelSstRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(MergeCellsRecord.sid, new MergeCellsRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(NoteRecord.sid, new NoteRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(NumberRecord.sid, new NumberRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(ObjRecord.sid, new ObjRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(RKRecord.sid, new RkRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(SSTRecord.sid, new SstRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(StringRecord.sid, new StringRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(TextObjectRecord.sid, new TextObjectRecordHandler());
        XLS_RECORD_HANDLER_MAP.put(DateWindow1904Record.sid, new DateWindow1904RecordHandler());
    }

    /**
     * Constructor to initialize the XlsSaxAnalyser with the given context.
     *
     * @param xlsReadContext The context containing necessary information for reading the Excel file.
     */
    public XlsSaxAnalyser(XlsReadContext xlsReadContext) {
        this.xlsReadContext = xlsReadContext;
    }

    /**
     * Retrieves the list of sheets in the workbook.
     * <p>
     * If the sheet data list is not already loaded, it triggers the execution of a listener to load the data.
     *
     * @return A list of ReadSheet objects representing the sheets in the workbook.
     */
    @Override
    public List<ReadSheet> sheetList() {
        try {
            if (xlsReadContext.readWorkbookHolder().getActualSheetDataList() == null) {
                new XlsListSheetListener(xlsReadContext).execute();
            }
        } catch (ExcelAnalysisStopException e) {
            if (log.isDebugEnabled()) {
                log.debug("Custom stop!");
            }
        }
        List<ReadSheet> actualSheetDataList =
                xlsReadContext.readWorkbookHolder().getActualSheetDataList();
        if (xlsReadContext.readWorkbookHolder().getIgnoreHiddenSheet()) {
            return actualSheetDataList.stream()
                    .filter(readSheet -> (!readSheet.isHidden() && !readSheet.isVeryHidden()))
                    .collect(Collectors.toList());
        }
        return actualSheetDataList;
    }

    /**
     * Executes the parsing process for the Excel file.
     * <p>
     * This method sets up the necessary listeners and processes the workbook events using HSSFEventFactory.
     */
    @Override
    public void execute() {
        XlsReadWorkbookHolder xlsReadWorkbookHolder = xlsReadContext.xlsReadWorkbookHolder();
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        xlsReadWorkbookHolder.setFormatTrackingHSSFListener(new FormatTrackingHSSFListener(listener));
        EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener =
                new EventWorkbookBuilder.SheetRecordCollectingListener(
                        xlsReadWorkbookHolder.getFormatTrackingHSSFListener());
        xlsReadWorkbookHolder.setHssfWorkbook(workbookBuildingListener.getStubHSSFWorkbook());
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        request.addListenerForAllRecords(xlsReadWorkbookHolder.getFormatTrackingHSSFListener());
        try {
            factory.processWorkbookEvents(request, xlsReadWorkbookHolder.getPoifsFileSystem());
        } catch (IOException e) {
            throw new ExcelAnalysisException(e);
        }

        // There are some special xls that do not have the terminator "[EOF]", so an additional
        xlsReadContext.analysisEventProcessor().endSheet(xlsReadContext);
    }

    /**
     * Processes a single Excel record.
     * <p>
     * This method retrieves the appropriate handler for the given record and processes it. If the record is ignorable or
     * unsupported, it skips processing.
     *
     * @param record The Excel record to be processed.
     */
    @Override
    public void processRecord(Record record) {
        XlsRecordHandler handler = XLS_RECORD_HANDLER_MAP.get(record.getSid());
        if (handler == null) {
            return;
        }
        boolean ignoreRecord = (handler instanceof IgnorableXlsRecordHandler)
                && xlsReadContext.xlsReadWorkbookHolder().getIgnoreRecord();
        if (ignoreRecord) {
            // No need to read the current sheet
            return;
        }
        if (!handler.support(xlsReadContext, record)) {
            return;
        }

        try {
            handler.processRecord(xlsReadContext, record);
        } catch (ExcelAnalysisStopSheetException e) {
            if (log.isDebugEnabled()) {
                log.debug("Custom stop!", e);
            }
            xlsReadContext.xlsReadWorkbookHolder().setIgnoreRecord(Boolean.TRUE);
            xlsReadContext.xlsReadWorkbookHolder().setCurrentSheetStopped(Boolean.TRUE);
        }
    }
}
