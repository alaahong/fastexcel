package cn.idev.excel.write.merge;

import cn.idev.excel.metadata.property.OnceAbsoluteMergeProperty;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * It only merges once when create cell(firstRowIndex,lastRowIndex)
 *
 *
 */
public class OnceAbsoluteMergeStrategy implements SheetWriteHandler {
    /**
     * First row
     */
    private final int firstRowIndex;
    /**
     * Last row
     */
    private final int lastRowIndex;
    /**
     * First column
     */
    private final int firstColumnIndex;
    /**
     * Last row
     */
    private final int lastColumnIndex;

    public OnceAbsoluteMergeStrategy(int firstRowIndex, int lastRowIndex, int firstColumnIndex, int lastColumnIndex) {
        if (firstRowIndex < 0 || lastRowIndex < 0 || firstColumnIndex < 0 || lastColumnIndex < 0) {
            throw new IllegalArgumentException("All parameters must be greater than 0");
        }
        this.firstRowIndex = firstRowIndex;
        this.lastRowIndex = lastRowIndex;
        this.firstColumnIndex = firstColumnIndex;
        this.lastColumnIndex = lastColumnIndex;
    }

    public OnceAbsoluteMergeStrategy(OnceAbsoluteMergeProperty onceAbsoluteMergeProperty) {
        this(
                onceAbsoluteMergeProperty.getFirstRowIndex(),
                onceAbsoluteMergeProperty.getLastRowIndex(),
                onceAbsoluteMergeProperty.getFirstColumnIndex(),
                onceAbsoluteMergeProperty.getLastColumnIndex());
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        CellRangeAddress cellRangeAddress =
                new CellRangeAddress(firstRowIndex, lastRowIndex, firstColumnIndex, lastColumnIndex);
        writeSheetHolder.getSheet().addMergedRegionUnsafe(cellRangeAddress);
    }
}
