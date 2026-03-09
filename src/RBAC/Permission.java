/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package RBAC;

/**
 *
 * @author trisha
 */
public enum Permission {
    // Employee Management
    VIEW_EMPLOYEE,
    ADD_EMPLOYEE,
    EDIT_EMPLOYEE,
    DELETE_EMPLOYEE,

    // Payroll
    VIEW_PAYROLL,
    PROCESS_PAYROLL,

    // Payslip
    GENERATE_PAYSLIP,
    VIEW_PAYSLIP,
    VIEW_OWN_PAYSLIP,

    // Leave
    SUBMIT_LEAVE,
    VIEW_LEAVE_REQUESTS,
    VIEW_LEAVE_HISTORY,
    APPROVE_LEAVE,
    REJECT_LEAVE,

    // Attendance
    TIME_IN,
    TIME_OUT,
    VIEW_ATTENDANCE,
    VIEW_OWN_ATTENDANCE,
    EDIT_ATTENDANCE,

    // System / IT / Admin
    ACCESS_SYSTEM_TOOLS,
    MANAGE_USERS,
    RESET_PASSWORD
}