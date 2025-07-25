package cn.idev.excel.metadata.property;

import cn.idev.excel.annotation.format.NumberFormat;
import java.math.RoundingMode;

/**
 * Configuration from annotations
 *
 *
 */
public class NumberFormatProperty {
    private String format;
    private RoundingMode roundingMode;

    public NumberFormatProperty(String format, RoundingMode roundingMode) {
        this.format = format;
        this.roundingMode = roundingMode;
    }

    public static NumberFormatProperty build(NumberFormat numberFormat) {
        if (numberFormat == null) {
            return null;
        }
        return new NumberFormatProperty(numberFormat.value(), numberFormat.roundingMode());
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }
}
