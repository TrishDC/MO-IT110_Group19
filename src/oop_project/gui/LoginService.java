package oop_project.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class LoginService {
    private static final String ACCOUNTS_FILE = "src" + java.io.File.separator + "com" + java.io.File.separator + "Motor" +java.io.File.separator + "PH" + java.io.File.separator+ java.io.File.separator + "Employee" + java.io.File.separator + "Record.csv";

    public static Map<String, String> loadAccounts() throws IOException {
        Map<String, String> accounts = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    accounts.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        return accounts;
    }

    public static boolean validate(String username, String password) {
        try {
            Map<String, String> accounts = loadAccounts();
            return password.equals(accounts.get(username));
        } catch (IOException e) {
            System.err.println("Error reading accounts.csv: " + e.getMessage());
            return false;
        }
    }
} 