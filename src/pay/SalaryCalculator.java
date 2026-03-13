package pay;

import model.Employee;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

public class SalaryCalculator {

    public static BigDecimal computeMonthlyPay(Employee emp, YearMonth ym) {
        return emp.calculateSalary();
    }

    public static BigDecimal computeSssDeduction(BigDecimal grossMonthly) {
        return grossMonthly.multiply(new BigDecimal("0.04")).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal computePhilHealthDeduction(BigDecimal grossMonthly) {
        return grossMonthly.multiply(new BigDecimal("0.0275")).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal computePagIbigDeduction(BigDecimal grossMonthly) {
        return grossMonthly.multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal computeWithholdingTax(BigDecimal basicSalary, BigDecimal sss, BigDecimal phil, BigDecimal pag) {
        BigDecimal totalMandatory = sss.add(phil).add(pag);
        // Tax is calculated on Basic Salary, not Gross (Allowances are non-taxable)
        BigDecimal taxableIncome = basicSalary.subtract(totalMandatory);

        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return taxableIncome.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
    }
}