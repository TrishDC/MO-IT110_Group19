package oop_project.gui;

import java.io.IOException;
import java.util.Arrays;

public class LoginService {
    private static final PasswordManager PASSWORD_MANAGER = new PasswordManager();

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
} 