/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pay;

import java.math.BigDecimal;
import java.time.YearMonth;
import model.Employee;

/**
 *
 * @author trisha
 */
public interface ISalaryCalculator {
/**
 *
 * @author trisha
 */
interface SalaryCalculator_Interface {
  BigDecimal computeMonthlyPay(Employee emp, YearMonth ym);
  BigDecimal computeSssDeduction(BigDecimal grossMonthly);
  BigDecimal computePhilHealthDeduction(BigDecimal grossMonthly);
  BigDecimal computePagIbigDeduction(BigDecimal grossMonthly);
  BigDecimal computeWithholdingTax(BigDecimal earnings,
        BigDecimal sss,
        BigDecimal philHealth,
        BigDecimal pagIbig);
}
}
