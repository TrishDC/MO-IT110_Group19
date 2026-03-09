/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RBAC;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author trisha
 */

public class RBACSetup {

    public static Map<String, Role> setupRoles() {

        Map<String, Role> roles = new HashMap<>();

        // EXECUTIVE ROLE
        roles.put("EXECUTIVE", new Role("EXECUTIVE", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL,
                Permission.VIEW_PAYSLIP,
                Permission.VIEW_ATTENDANCE,
                Permission.VIEW_LEAVE_REQUESTS
        )));

        // HR ROLE
        roles.put("HR", new Role("HR", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.ADD_EMPLOYEE,
                Permission.EDIT_EMPLOYEE,
                Permission.DELETE_EMPLOYEE,

                Permission.APPROVE_LEAVE,
                Permission.REJECT_LEAVE,
                Permission.VIEW_LEAVE_REQUESTS,
                Permission.VIEW_LEAVE_HISTORY,

                Permission.VIEW_ATTENDANCE,
                Permission.EDIT_ATTENDANCE
        )));

        // PAYROLL ROLE
        roles.put("PAYROLL", new Role("PAYROLL", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL,
                Permission.PROCESS_PAYROLL,
                Permission.GENERATE_PAYSLIP,
                Permission.VIEW_PAYSLIP
        )));

        // ACCOUNTING ROLE
        roles.put("ACCOUNTING", new Role("ACCOUNTING", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL,
                Permission.VIEW_PAYSLIP
        )));

        // IT ROLE
        roles.put("IT", new Role("IT", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.EDIT_EMPLOYEE,

                Permission.ACCESS_SYSTEM_TOOLS,
                Permission.RESET_PASSWORD,
                Permission.MANAGE_USERS
        )));

        // SALES ROLE
        roles.put("SALES", new Role("SALES", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYSLIP
        )));

        return roles;
    }
}