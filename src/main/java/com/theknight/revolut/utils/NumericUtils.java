package com.theknight.revolut.utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

public class NumericUtils {

    /**
     * @param value
     * @return Integer represents of value or null if value not is number;
     */
    public static Integer parseInt(String value) {
        if (NumberUtils.isParsable(value)){
            return Integer.parseInt(value);
        }
        return null;
    }

    /**
     * @param value
     * @return BigDecimal represents of value or null if value not is number;
     */
    public static BigDecimal parseMoney(String value) {
        if (NumberUtils.isParsable(value)) {
            return new BigDecimal(value);
        }
        return null;
    }

}
