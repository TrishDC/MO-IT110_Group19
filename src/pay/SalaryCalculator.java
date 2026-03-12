package pay;

import model.Employee;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

/**
 * Computes earnings and deductions accurately using Polymorphism.
 */
public class SalaryCalculator {

    /** * Computes the Monthly Pay based on the specific Employee type.
     * If Regular: Basic + Allowances.
     * If Probationary: Hourly Rate * 160.
     */
    public static BigDecimal computeMonthlyPay(Employee emp, YearMonth ym) {
        // We no longer manually calculate gross here. 
        // We let the Employee object tell us what its specific gross is.
        return emp.calculateSalary();
    }

    /** * SSS Deduction: 4% of Gross.
     */
    public static BigDecimal computeSssDeduction(BigDecimal grossMonthly) {
        return grossMonthly.multiply(new BigDecimal("0.04"))
                           .setScale(2, RoundingMode.HALF_UP);
    }

    /** * PhilHealth Deduction: 2.75% of Gross.
     */
    public static BigDecimal computePhilHealthDeduction(BigDecimal grossMonthly) {
        return grossMonthly.multiply(new BigDecimal("0.0275"))
                           .setScale(2, RoundingMode.HALF_UP);
    }

    /** * Pag-IBIG Deduction: 2% of Gross.
     */
    public static BigDecimal computePagIbigDeduction(BigDecimal grossMonthly) {
        return grossMonthly.multiply(new BigDecimal("0.02"))
                           .setScale(2, RoundingMode.HALF_UP);
    }

    /** * Withholding Tax: 10% on (Earnings - Mandatory Deductions).
     */
    public static BigDecimal computeWithholdingTax(
        BigDecimal earnings,
        BigDecimal sss,
        BigDecimal philHealth,
        BigDecimal pagIbig
    ) {
        BigDecimal totalMandatory = sss.add(philHealth).add(pagIbig);
        BigDecimal taxableIncome = earnings.subtract(totalMandatory);

        // Guard against negative taxable income
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return taxableIncome.multiply(new BigDecimal("0.10"))
                            .setScale(2, RoundingMode.HALF_UP);
    }
}