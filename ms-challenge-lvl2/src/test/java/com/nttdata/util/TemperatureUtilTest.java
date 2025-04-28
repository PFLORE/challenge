package com.nttdata.util;

import org.junit.jupiter.api.Test;

import static com.nttdata.constant.TemperatureConstant.CELSIUS;
import static com.nttdata.constant.TemperatureConstant.FAHRENHEIT;
import static org.junit.jupiter.api.Assertions.*;

class TemperatureUtilTest {

    @Test
    void testConvertToFahrenheit() {
        double celsius = 0.0;
        double fahrenheit = TemperatureUtil.convert(celsius, FAHRENHEIT);
        assertEquals(32.0, fahrenheit, 0.01);
    }

    @Test
    void testConvertToCelsius() {
        double celsius = 25.0;
        double result = TemperatureUtil.convert(celsius, CELSIUS);
        assertEquals(25.0, result, 0.01);
    }

    @Test
    void testConvertCaseInsensitive() {
        double celsius = 100.0;
        double fahrenheit = TemperatureUtil.convert(celsius, FAHRENHEIT.toLowerCase());
        assertEquals(212.0, fahrenheit, 0.01);
    }

}