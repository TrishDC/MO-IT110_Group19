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

        roles.put("EXECUTIVE", new Role("EXECUTIVE", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.EDIT_EMPLOYEE,
                Permission.PROCESS_PAYROLL,
                Permission.APPROVE_LEAVE,
                Permission.REJECT_LEAVE,
                Permission.VIEW_PAYSLIP
        )));

        roles.put("IT", new Role("IT", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.EDIT_EMPLOYEE
        )));

        roles.put("HR", new Role("HR", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.EDIT_EMPLOYEE,
                Permission.APPROVE_LEAVE,
                Permission.REJECT_LEAVE
        )));

        roles.put("ACCOUNTING", new Role("ACCOUNTING", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.VIEW_PAYSLIP
        )));

        roles.put("PAYROLL", new Role("PAYROLL", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.PROCESS_PAYROLL
        )));

        roles.put("SALES", new Role("SALES", Set.of(
                Permission.VIEW_EMPLOYEE,
                Permission.PROCESS_PAYROLL
        )));

        return roles;
    }
}

