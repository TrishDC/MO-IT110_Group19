/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */
package service;

import model.Employee;

public class SessionManager {

    private static Employee currentUser;

    public static void setCurrentUser(Employee employee) {
        currentUser = employee;
    }

    public static Employee getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}
