package com.convallyria.schematics.extended.util;

public class MathsUtil {

    public static int roundHalfUp(int value, int multiplier) {
        if (multiplier <= 0) throw new IllegalArgumentException();
        return (value + (value < 0 ? multiplier / -2 : multiplier / 2)) / multiplier * multiplier;
    }
}
