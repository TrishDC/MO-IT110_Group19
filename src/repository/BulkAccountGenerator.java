/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import gui.PasswordManager;
import model.Employee;

import java.io.IOException;
import java.util.List;

public class BulkAccountGenerator {

    public static void generateAccounts(CsvEmployeeRepository repo) throws IOException {
        PasswordManager pm = new PasswordManager();
        List<Employee> employees = repo.loadAll();

        for (Employee e : employees) {
            String empId = e.getId() != null ? e.getId().trim() : "";
            String firstName = e.getFirstName() != null ? e.getFirstName().trim() : "";

            if (empId.isEmpty() || firstName.isEmpty()) {
                continue; // skip invalid rows
            }

            String username = empId;
            String password = empId + Character.toUpperCase(firstName.charAt(0));

            pm.upsertAccount(username, password.toCharArray());
        }

        System.out.println("All employee accounts have been added to PasswordManager.");
    }

    public static void main(String[] args) throws IOException {
        CsvEmployeeRepository repo = new CsvEmployeeRepository("data/MotorPH Employee Record.csv");
        generateAccounts(repo);
    }
}
