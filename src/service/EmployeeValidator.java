/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */

package service;

import java.math.BigDecimal;

public final class EmployeeValidator {

    private EmployeeValidator() {
    }

    public static void validate(EmployeeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Employee request cannot be null.");
        }

        requireNotBlank(request.getId(), "Employee ID is required.");
        requireNotBlank(request.getFirstName(), "First name is required.");
        requireNotBlank(request.getLastName(), "Last name is required.");
        requireNotBlank(request.getAddress(), "Address is required.");
        requireNotBlank(request.getPhone(), "Phone number is required.");
        requireNotBlank(request.getSssNumber(), "SSS number is required.");
        requireNotBlank(request.getPhilHealthNumber(), "PhilHealth number is required.");
        requireNotBlank(request.getTinNumber(), "TIN number is required.");
        requireNotBlank(request.getPagIbigNumber(), "Pag-IBIG number is required.");
        requireNotBlank(request.getStatus(), "Status is required.");
        requireNotBlank(request.getPosition(), "Position is required.");
        requireNotBlank(request.getSupervisor(), "Supervisor is required.");
        requireNotBlank(request.getRoleName(), "Role is required.");

        if (request.getBirthDate() == null) {
            throw new IllegalArgumentException("Birth date is required.");
        }

        requireNonNegative(request.getBasicSalary(), "Basic salary");
        requireNonNegative(request.getRiceSubsidy(), "Rice subsidy");
        requireNonNegative(request.getPhoneAllowance(), "Phone allowance");
        requireNonNegative(request.getClothingAllowance(), "Clothing allowance");
        requireNonNegative(request.getGrossSemiMonthlyRate(), "Gross semi-monthly rate");
        requireNonNegative(request.getHourlyRate(), "Hourly rate");
    }

    private static void requireNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void requireNonNegative(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
    }
}