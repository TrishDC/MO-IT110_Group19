package gui;

import model.Employee;
import repository.BulkAccountGenerator;
import repository.CsvEmployeeRepository;
import repository.EmployeeRepository;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EmployeeManagementLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            try {
                Path csvPath = resolveEmployeeCsvPath();

                // Always generate employee accounts from CSV
                CsvEmployeeRepository repo = new CsvEmployeeRepository(csvPath.toString());
                BulkAccountGenerator.generateAccounts(repo);

                System.out.println("Employee accounts generated.");

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JFrame dummy = new JFrame();
            dummy.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            LoginDialog loginDialog = new LoginDialog(dummy);
            loginDialog.setVisible(true);

            if (!loginDialog.isSucceeded()) {
                dummy.dispose();
                System.exit(0);
                return;
            }

            Employee loggedInEmployee = loginDialog.getLoggedInEmployee();
            dummy.dispose();

            Path csvPath = resolveEmployeeCsvPath();
            EmployeeRepository repo = new CsvEmployeeRepository(csvPath.toString());

            new EmployeeManagementFrame(repo, csvPath, loggedInEmployee).setVisible(true);
        });
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