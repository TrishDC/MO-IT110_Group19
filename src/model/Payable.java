package model;

import java.math.BigDecimal;

public interface Payable {
    BigDecimal calculateSalary();
    BigDecimal getGrossMonthlySalary();
    BigDecimal getTotalAllowance();
}