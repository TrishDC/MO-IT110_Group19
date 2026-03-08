package oop_project.gui;

import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.repository.CsvEmployeeRepository;
import com.motorph.employeeapp.repository.EmployeeRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class LoginService {
    private static final PasswordManager PASSWORD_MANAGER = new PasswordManager();
    private static final DateTimeFormatter BIRTHDAY_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");

    public static boolean hasAccounts() {
        try {
            return PASSWORD_MANAGER.hasAccounts();
        } catch (IOException e) {
            System.err.println("Error checking accounts: " + e.getMessage());
            return false;
        }
    }

    public static boolean registerOrUpdate(String username, char[] password) {
        try {
            PASSWORD_MANAGER.upsertAccount(username, password);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error saving account: " + e.getMessage());
            return false;
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
        }
    }

    public static boolean validate(String username, String password) {
        char[] passwordChars = password != null ? password.toCharArray() : new char[0];
        try {
            return PASSWORD_MANAGER.validate(username, passwordChars);
        } catch (IOException e) {
            System.err.println("Error validating credentials: " + e.getMessage());
            return false;
        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    public static boolean changePassword(String username, char[] oldPassword, char[] newPassword) {
        try {
            return PASSWORD_MANAGER.changePassword(username, oldPassword, newPassword);
        } catch (IOException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        } finally {
            if (oldPassword != null) {
                Arrays.fill(oldPassword, '\0');
            }
            if (newPassword != null) {
                Arrays.fill(newPassword, '\0');
            }
        }
    }

    public static boolean registerEmployeeAccount(String employeeId, String birthDateText, char[] password) {
        try {
            if (employeeId == null || employeeId.trim().isEmpty() || birthDateText == null || birthDateText.trim().isEmpty()) {
                return false;
            }
            if (password == null || password.length == 0) {
                return false;
            }

            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(birthDateText.trim(), BIRTHDAY_FORMAT);
            } catch (DateTimeParseException ex) {
                return false;
            }

            EmployeeRepository employeeRepository = new CsvEmployeeRepository(resolveEmployeeCsvPath().toString());
            List<Employee> employees = employeeRepository.loadAll();

            String normalizedId = employeeId.trim();
            for (Employee employee : employees) {
                if (normalizedId.equals(employee.getId()) && birthDate.equals(employee.getBirthDate())) {
                    PASSWORD_MANAGER.upsertAccount(normalizedId, password);
                    return true;
                }
            }
            return false;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error registering employee account: " + e.getMessage());
            return false;
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
        }
    }

    private static Path resolveEmployeeCsvPath() {
        String fileName = "MotorPH Employee Record.csv";
        Path[] candidates = new Path[] {
            Paths.get("data", fileName),
            Paths.get("src", "data", fileName),
            Paths.get(System.getProperty("user.dir"), "data", fileName),
            Paths.get(System.getProperty("user.dir"), "src", "data", fileName)
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }
        return candidates[1].toAbsolutePath().normalize();
    }
} 