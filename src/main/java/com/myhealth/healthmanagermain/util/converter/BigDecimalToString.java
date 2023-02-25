package com.myhealth.healthmanagermain.util.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.core.convert.converter.Converter;

public class BigDecimalToString implements Converter<BigDecimal, String> {

  @Override
  public String convert(BigDecimal from) {
    return from.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros()
        .toPlainString();
  }
}
