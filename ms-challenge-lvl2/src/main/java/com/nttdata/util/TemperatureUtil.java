package com.nttdata.util;

import static com.nttdata.constant.TemperatureConstant.FAHRENHEIT;

public class TemperatureUtil {
    public static double convert(double temperature, String unit) {
        return FAHRENHEIT.equalsIgnoreCase(unit) ? (temperature * 9/5) + 32 : temperature;
    }
}
