package my.framework.util;

import java.math.BigDecimal;

public class DecimalHelper {
    public static float scaleFloat(float floatValue, int scale) {
        return scaleFloat(floatValue, scale, BigDecimal.ROUND_HALF_UP);
    }
    
    public static float scaleFloat(float floatValue, int scale, int roundingMode) {
        return new BigDecimal(floatValue).setScale(scale, roundingMode).floatValue();
    }
    
    public static int closestInteger(int source, int[] target) {
        if (target == null || target.length == 0)
            return source;
        int minDelta = Integer.MAX_VALUE, result = source;
        for (int t : target) {
            int delta = Math.abs(source - t);
            if (delta < minDelta) {
                minDelta = delta;
                result = t;
            }
        }
        return result;
    }
}
