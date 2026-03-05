package com.motorph.employeeapp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import oop_project.gui.LoginService;

public class LoginDialog extends JDialog {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JButton btnChangePassword;
    private boolean succeeded;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        lbUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);

        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (LoginService.validate(getUsername(), getPassword())) {
                    succeeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Invalid username or password",
                            "Login",
                            JOptionPane.ERROR_MESSAGE);
                    // reset username and password
                    tfUsername.setText("");
                    pfPassword.setText("");
                    succeeded = false;
                }
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                succeeded = false;
                dispose();
            }
        });
        btnChangePassword = new JButton("Change Password");
        btnChangePassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showChangePasswordDialog();
            }
        });
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnChangePassword);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        ensureInitialAccount();

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void showChangePasswordDialog() {
        JTextField usernameField = new JTextField(20);
        usernameField.setText(getUsername());
        JPasswordField oldPasswordField = new JPasswordField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Username"));
        form.add(usernameField);
        form.add(new JLabel("Current Password"));
        form.add(oldPasswordField);
        form.add(new JLabel("New Password"));
        form.add(newPasswordField);
        form.add(new JLabel("Confirm New Password"));
        form.add(confirmPasswordField);

        int option = JOptionPane.showConfirmDialog(
            this,
            form,
            "Change Password",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String username = usernameField.getText() != null ? usernameField.getText().trim() : "";
        char[] oldPassword = oldPasswordField.getPassword();
        char[] newPassword = newPasswordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        try {
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username is required.", "Change Password", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (oldPassword.length == 0 || newPassword.length == 0) {
                JOptionPane.showMessageDialog(this, "Current and new password are required.", "Change Password", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Arrays.equals(newPassword, confirmPassword)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match.", "Change Password", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean changed = LoginService.changePassword(username, oldPassword, newPassword);
            if (!changed) {
                JOptionPane.showMessageDialog(this, "Unable to change password. Check your username and current password.", "Change Password", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Password changed successfully.", "Change Password", JOptionPane.INFORMATION_MESSAGE);
            tfUsername.setText(username);
            pfPassword.setText("");
        } finally {
            Arrays.fill(confirmPassword, '\0');
        }
    }

    private void ensureInitialAccount() {
        if (LoginService.hasAccounts()) {
            return;
        }

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmField = new JPasswordField(20);

        JPanel setupPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        setupPanel.add(new JLabel("No login account found. Create admin account:"));
        setupPanel.add(new JLabel("Username"));
        setupPanel.add(usernameField);
        setupPanel.add(new JLabel("Password"));
        setupPanel.add(passwordField);
        setupPanel.add(new JLabel("Confirm Password"));
        setupPanel.add(confirmField);

        int option = JOptionPane.showConfirmDialog(
            this,
            setupPanel,
            "Initial Account Setup",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String username = usernameField.getText() != null ? usernameField.getText().trim() : "";
        char[] password = passwordField.getPassword();
        char[] confirm = confirmField.getPassword();

        try {
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username is required.", "Setup", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (password.length == 0) {
                JOptionPane.showMessageDialog(this, "Password is required.", "Setup", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Arrays.equals(password, confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Setup", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean saved = LoginService.registerOrUpdate(username, password);
            if (!saved) {
                JOptionPane.showMessageDialog(this, "Unable to save account.", "Setup", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Account created. Please login.", "Setup", JOptionPane.INFORMATION_MESSAGE);
            tfUsername.setText(username);
            pfPassword.setText("");
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirm, '\0');
        }
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
