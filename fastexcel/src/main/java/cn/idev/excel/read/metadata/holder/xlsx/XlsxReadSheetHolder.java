package cn.idev.excel.read.metadata.holder.xlsx;

import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.read.metadata.holder.ReadSheetHolder;
import cn.idev.excel.read.metadata.holder.ReadWorkbookHolder;
import java.util.Deque;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;

/**
 * sheet holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class XlsxReadSheetHolder extends ReadSheetHolder {
    /**
     * Record the label of the current operation to prevent NPE.
     */
    private Deque<String> tagDeque;
    /**
     * Current Column
     */
    private Integer columnIndex;
    /**
     * Data for current label.
     */
    private StringBuilder tempData;
    /**
     * Formula for current label.
     */
    private StringBuilder tempFormula;
    /**
     * excel Relationship
     */
    private PackageRelationshipCollection packageRelationshipCollection;

    public XlsxReadSheetHolder(ReadSheet readSheet, ReadWorkbookHolder readWorkbookHolder) {
        super(readSheet, readWorkbookHolder);
        this.tagDeque = new LinkedList<String>();
        packageRelationshipCollection = ((XlsxReadWorkbookHolder) readWorkbookHolder)
                .getPackageRelationshipCollectionMap()
                .get(readSheet.getSheetNo());
    }
}
