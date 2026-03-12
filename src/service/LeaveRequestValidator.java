/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */


package service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import model.EmployeeLeaveRequest;

public final class LeaveRequestValidator {

    private LeaveRequestValidator() {
    }

    public static void validate(EmployeeLeaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Leave request cannot be null.");
        }

        requireNotBlank(request.getEmployeeId(), "Employee ID is required.");
        requireNotBlank(request.getEmployeeName(), "Employee name is required.");
        requireNotBlank(request.getDepartment(), "Department is required.");
        requireNotBlank(request.getStartDate(), "Start date is required.");
        requireNotBlank(request.getEndDate(), "End date is required.");
        requireNotBlank(request.getReason(), "Reason is required.");

        if (request.getReason().trim().length() < 5) {
            throw new IllegalArgumentException("Reason must be at least 5 characters.");
        }

        LocalDate start = parseDate(request.getStartDate(), "Start date must use yyyy-MM-dd.");
        LocalDate end = parseDate(request.getEndDate(), "End date must use yyyy-MM-dd.");

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be earlier than start date.");
        }

        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be earlier than today.");
        }

        if (request.getNotes() != null && request.getNotes().length() > 250) {
            throw new IllegalArgumentException("Notes must not exceed 250 characters.");
        }
    }

    private static void requireNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private static LocalDate parseDate(String value, String message) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(message);
        }
    }
}