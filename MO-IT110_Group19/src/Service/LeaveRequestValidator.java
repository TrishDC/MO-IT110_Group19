/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */

package service;

import model.Leave;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class LeaveRequestValidator {

    private LeaveRequestValidator() {
    }

    public static void validate(Leave leave) {
        if (leave == null) {
            throw new IllegalArgumentException("Leave request cannot be null.");
        }

        requireNotBlank(leave.getEmployeeId(), "Employee ID is required.");
        requireNotBlank(leave.getLeaveType(), "Leave type is required.");
        requireNotBlank(leave.getStartDate(), "Start date is required.");
        requireNotBlank(leave.getEndDate(), "End date is required.");

        LocalDate start = parseDate(leave.getStartDate(), "Start date must use yyyy-MM-dd.");
        LocalDate end = parseDate(leave.getEndDate(), "End date must use yyyy-MM-dd.");

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be earlier than start date.");
        }

        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be earlier than today.");
        }

        if (leave.getNotes() != null && leave.getNotes().length() > 250) {
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