package com.recipefind.backend.utils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Component
public class FractionConverter {

    public String decimalToFraction(double decimal) {
        BigDecimal decimalValue = new BigDecimal(decimal);
        BigDecimal tolerance = new BigDecimal("1E-6"); // Tolerance for rounding

        BigInteger numerator = decimalValue.multiply(BigDecimal.valueOf(1000000)).toBigInteger(); // Multiply to avoid precision loss
        BigInteger denominator = BigInteger.valueOf(1000000);  // Use 1000000 as the denominator (you can use higher values for better precision)

        // Simplify the fraction
        BigInteger gcd = numerator.gcd(denominator);
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);

        // Check if it is a whole number
        if (denominator.equals(BigInteger.ONE)) {
            return numerator.toString(); // Return as whole number if denominator is 1
        }

        // Return as a fraction
        return numerator + "/" + denominator;
    }

    public BigDecimal fractionToDecimal(String fraction) {
        if (fraction.contains("/")) {
            String[] parts = fraction.split("/");
            BigDecimal numerator = new BigDecimal(parts[0]);
            BigDecimal denominator = new BigDecimal(parts[1]);
            return numerator.divide(denominator, 6, RoundingMode.HALF_UP); // 6 decimal places, rounding half-up
        } else {
            return new BigDecimal(fraction); // If it's already a whole number, return it as BigDecimal
        }
    }
}
