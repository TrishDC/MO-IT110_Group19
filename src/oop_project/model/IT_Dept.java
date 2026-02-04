/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oop_project.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author trisha
 */
public class IT_Dept extends Employee {
    
    public IT_Dept(String id, String firstName, String lastName, LocalDate birthDate, BigDecimal basicSalary, BigDecimal riceSubsidy, BigDecimal phoneAllowance, BigDecimal clothingAllowance, BigDecimal grossSemiMonthlyRate, BigDecimal hourlyRate) {
        super(id, firstName, lastName, birthDate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossSemiMonthlyRate, hourlyRate);
    }
    
}
