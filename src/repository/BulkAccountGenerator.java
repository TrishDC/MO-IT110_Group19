package repository;

import gui.PasswordManager;
import model.Employee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


// This is a bulk account generator wherein employee credentials are made:
public class BulkAccountGenerator {

    public static void generateAccounts(CsvEmployeeRepository repo) throws IOException {
        PasswordManager pm = new PasswordManager();
        List<Employee> employees = repo.loadAll();

        for (Employee e : employees) {
            String empId = e.getId() != null ? e.getId().trim() : "";
            String firstName = e.getFirstName() != null ? e.getFirstName().trim() : "";

            if (empId.isEmpty() || firstName.isEmpty()) {
                continue;
            }

            String username = empId;
            String password = empId + Character.toUpperCase(firstName.charAt(0));

            pm.upsertAccount(username, password.toCharArray());
            System.out.println("Created account -> Username: " + username + " | Password: " + password);
        }

        System.out.println("All employee accounts have been added to PasswordManager.");
    }

    public static void main(String[] args) {
        try {
            Path csvPath = resolveEmployeeCsvPath();
            CsvEmployeeRepository repo = new CsvEmployeeRepository(csvPath.toString());
            generateAccounts(repo);
        } catch (IOException e) {
            System.err.println("Failed to generate accounts: " + e.getMessage());
            e.printStackTrace();
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