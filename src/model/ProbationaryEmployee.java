/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author trisha
 */
public class ProbationaryEmployee  extends Employee{
    
    public ProbationaryEmployee(String id, String firstName, String lastName, LocalDate birthDate, BigDecimal basicSalary, BigDecimal riceSubsidy, BigDecimal phoneAllowance, BigDecimal clothingAllowance, BigDecimal grossSemiMonthlyRate, BigDecimal hourlyRate) {
        super(id, firstName, lastName, birthDate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossSemiMonthlyRate, hourlyRate);
    }
        @Override
public String toString() {
    return "Probitionary Employee "+getPosition()+ getFirstName() + " " + getLastName();
    
}

    @Override
    public BigDecimal calculateSalary() {
        return getHourlyRate().multiply(new BigDecimal("160"));
    }

    @Override
    public String getEmployeeType() {
        return "Probationary";
    }
    
}



    
 


