package model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a probationary employee.
 */
public class ProbationaryEmployee extends Employee {

    private static final BigDecimal STANDARD_MONTHLY_HOURS = new BigDecimal("160");

    public ProbationaryEmployee(
            String id,
            String firstName,
            String lastName,
            LocalDate birthDate,
            BigDecimal basicSalary,
            BigDecimal riceSubsidy,
            BigDecimal phoneAllowance,
            BigDecimal clothingAllowance,
            BigDecimal grossSemiMonthlyRate,
            BigDecimal hourlyRate
    ) {
        super(
                id,
                firstName,
                lastName,
                birthDate,
                basicSalary,
                riceSubsidy,
                phoneAllowance,
                clothingAllowance,
                grossSemiMonthlyRate,
                hourlyRate
        );
    }

    /**
     * Computes salary based on hourly rate.
     */
    @Override
    public BigDecimal calculateSalary() {
        return getHourlyRate().multiply(STANDARD_MONTHLY_HOURS);
    }

    @Override
    public String getEmployeeType() {
        return "Probationary";
    }

    @Override
    public String toString() {
        return "Probationary Employee - "
                + getFirstName() + " "
                + getLastName()
                + " (" + getPosition() + ")";
    }
}