package oop_project.pay;

import com.motorph.employeeapp.model.Employee;

import java.math.BigDecimal;
import java.time.YearMonth;


/**
 * Computes earnings and deductions.
 */
public abstract class SalaryCalculator implements SalaryCalculator_Interface{
    /** Total pay = gross monthly + all allowances.
     * @param emp
     * @param ym
     * @return  */
    
    
    @Override
    public  BigDecimal computeMonthlyPay(Employee emp, YearMonth ym) {
        BigDecimal grossMonthly = emp.getGrossSemiMonthlyRate()
                                     .multiply(BigDecimal.valueOf(2));
        return grossMonthly
            .add(emp.getRiceSubsidy())
            .add(emp.getPhoneAllowance())
            .add(emp.getClothingAllowance());
    }

    /** 4% SSS deduction on gross monthly.
     * @param grossMonthly
     * @return  */
    @Override
    public  BigDecimal computeSssDeduction(BigDecimal grossMonthly) {
        return grossMonthly.multiply(new BigDecimal("0.04"));
    }

    /** 2.75% PhilHealth deduction on gross monthly.
     * @param grossMonthly
     * @return  */
    
    @Override
    public  BigDecimal computePhilHealthDeduction(BigDecimal grossMonthly) {
        return grossMonthly.multiply(new BigDecimal("0.0275"));
    }

    /** 2% Pag-IBIG deduction on gross monthly.
     * @param grossMonthly
     * @return  */
    @Override
    public  BigDecimal computePagIbigDeduction(BigDecimal grossMonthly) {
        return grossMonthly.multiply(new BigDecimal("0.02"));
    }

    /** 10% withholding tax on (earnings − mandatory deductions).
     * @param earnings
     * @param sss
     * @param philHealth
     * @param pagIbig
     * @return  */
    @Override
    public  BigDecimal computeWithholdingTax(
        BigDecimal earnings,
        BigDecimal sss,
        BigDecimal philHealth,
        BigDecimal pagIbig
    ) {
        BigDecimal taxable = earnings
            .subtract(sss)
            .subtract(philHealth)
            .subtract(pagIbig);
        return taxable.multiply(new BigDecimal("0.10"));
    }
}
