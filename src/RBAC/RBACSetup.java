package RBAC;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Role-Based Access Control Setup
 * Defines permissions for each organizational role.
 */
public class RBACSetup {

    public static Map<String, Role> setupRoles() {
        Map<String, Role> roles = new HashMap<>();

        // EXECUTIVE ROLE: Full visibility
        roles.put("EXECUTIVE", new Role("EXECUTIVE", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL, // Added/Confirmed
                Permission.VIEW_PAYSLIP,
                Permission.VIEW_ATTENDANCE,
                Permission.VIEW_LEAVE_REQUESTS
        )));

        // HR ROLE: Added VIEW_PAYROLL so HR can see the button
        roles.put("HR", new Role("HR", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL, // Added
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

        // PAYROLL ROLE: Full Payroll Access
        roles.put("PAYROLL", new Role("PAYROLL", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL, // Added/Confirmed
                Permission.PROCESS_PAYROLL,
                Permission.GENERATE_PAYSLIP,
                Permission.VIEW_PAYSLIP
        )));

        // ACCOUNTING ROLE: Financial Oversight
        roles.put("ACCOUNTING", new Role("ACCOUNTING", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL, // Added/Confirmed
                Permission.VIEW_PAYSLIP
        )));

        // IT ROLE: Added VIEW_PAYROLL for system troubleshooting
        roles.put("IT", new Role("IT", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL, // Added
                Permission.EDIT_EMPLOYEE,
                Permission.ACCESS_SYSTEM_TOOLS,
                Permission.RESET_PASSWORD,
                Permission.MANAGE_USERS
        )));

        // PROBATIONARY ROLE: Can see records (but Table Renderer handles hiding allowance)
        roles.put("PROBATIONARY", new Role("PROBATIONARY", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL, // Added/Confirmed
                Permission.VIEW_PAYSLIP
        )));
        
        // REGULAR ROLE: Standard access
        roles.put("REGULAR", new Role("REGULAR", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYROLL, // Added/Confirmed
                Permission.VIEW_PAYSLIP
        )));

        return roles;
    }
}