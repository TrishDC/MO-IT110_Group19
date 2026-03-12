package service;

import gui.PasswordManager;
import model.Employee;
import repository.CsvEmployeeRepository;
import repository.EmployeeRepository;

import java.io.IOException;
import java.util.List;

public class AuthenticationService {

    private final PasswordManager passwordManager;
    private final EmployeeRepository employeeRepository;

    public AuthenticationService(EmployeeRepository employeeRepository) {
        this.passwordManager = new PasswordManager();
        this.employeeRepository = employeeRepository;
    }

    public Employee login(String username, String password) {

        try {

            boolean valid = passwordManager.validate(username, password.toCharArray());

            if (!valid) {
                return null;
            }

            List<Employee> employees = employeeRepository.loadAll();

            for (Employee e : employees) {
                if (e.getId().equals(username)) {
                    return e;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}