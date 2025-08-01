package cn.idev.excel.write.metadata.fill;

import cn.idev.excel.enums.WriteTemplateAnalysisCellTypeEnum;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Read the cells of the template while populating the data.
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class AnalysisCell {
    private int columnIndex;
    private int rowIndex;
    private List<String> variableList;
    private List<String> prepareDataList;
    private Boolean onlyOneVariable;
    private WriteTemplateAnalysisCellTypeEnum cellType;
    private String prefix;
    private Boolean firstRow;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalysisCell that = (AnalysisCell) o;
        if (columnIndex != that.columnIndex) {
            return false;
        }
        return rowIndex == that.rowIndex;
    }

    @Override
    public int hashCode() {
        int result = columnIndex;
        result = 31 * result + rowIndex;
        return result;
    }
}
