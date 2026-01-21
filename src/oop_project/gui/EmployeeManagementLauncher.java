package com.motorph.employeeapp.gui;

import javax.swing.*;
import java.awt.*;

public class EmployeeManagementLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Show login dialog first
            JFrame dummy = new JFrame();
            dummy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            dummy.setSize(0, 0);
            dummy.setLocationRelativeTo(null);

            LoginDialog loginDlg = new LoginDialog(dummy);
            loginDlg.setVisible(true);
            
            if (!loginDlg.isSucceeded()) {
                System.exit(0);
            }
            dummy.dispose();

            // If login successful, run the original main method
            try {
                // Launch the original EmployeeManagementFrame
                java.lang.reflect.Method method = Class.forName("com.motorph.employeeapp.gui.EmployeeManagementFrame")
                    .getMethod("main", String[].class);
                method.invoke(null, (Object) new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
