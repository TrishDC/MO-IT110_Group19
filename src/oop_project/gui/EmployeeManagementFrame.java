package com.motorph.employeeapp.gui;

import oop_project.gui.PayslipSplitDialog;
import com.motorph.employeeapp.model.Employee;
import com.motorph.employeeapp.repository.CsvEmployeeRepository;
import com.motorph.employeeapp.repository.EmployeeRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementFrame extends JFrame {
    private final EmployeeRepository repo;
    private final JTable table;
    private final DefaultTableModel model;

    public EmployeeManagementFrame(EmployeeRepository repo) {
        super("Employee Management");
        this.repo = repo;

        // Nimbus look & feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        // Define color palette
        Color PRIMARY      = new Color(45,137,239);
        Color DARK_PRIMARY = new Color(30,100,180);
        Color ACCENT       = new Color(245,245,245);
        Color BG_WHITE     = Color.WHITE;
        Color ADD_GREEN    = new Color(76,175,80);
        Color DEL_RED      = new Color(244,67,54);
        Color UPD_ORANGE   = new Color(255,152,0);

        // Table & model
        String[] cols = {"Employee #","Last Name","First Name","SSS #","PhilHealth #","TIN #","Pag-IBIG #"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setRowHeight(28);
        table.setFont(table.getFont().deriveFont(14f));
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(table);

        // Header styling
        JTableHeader hdr = table.getTableHeader();
        hdr.setOpaque(true);
        hdr.setBackground(DARK_PRIMARY);
        hdr.setForeground(Color.WHITE);
        hdr.setFont(hdr.getFont().deriveFont(Font.BOLD, 14f));
        hdr.setPreferredSize(new Dimension(hdr.getPreferredSize().width, 32));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));
        hdr.setReorderingAllowed(false);
        // Force header renderer with solid blue background
        hdr.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                lbl.setBackground(DARK_PRIMARY);
                lbl.setForeground(Color.WHITE);
                lbl.setHorizontalAlignment(CENTER);
                // add right border to separate columns
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, PRIMARY));
                return lbl;
            }
        });

        // Banded rows + formatting renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean focus, int row, int col
            ) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);

                if (sel) {
                    setBackground(PRIMARY);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? BG_WHITE : ACCENT);
                    setForeground(Color.DARK_GRAY);
                }

                // Plain-string for PhilHealth (col=4) & Pag-IBIG (col=6)
                if (!sel && (col == 4 || col == 6) && v != null) {
                    try {
                        BigDecimal bd = new BigDecimal(v.toString());
                        setText(bd.toPlainString());
                    } catch (Exception ex) {
                        setText(v.toString());
                    }
                }
                return this;
            }
        });

        // Buttons
        JButton addBtn    = new JButton("Add Employee");
        JButton updateBtn = new JButton("Update Employee");
        JButton deleteBtn = new JButton("Delete Employee");
        JButton viewBtn   = new JButton("View Employee");

        List<JButton> btnsList = List.of(addBtn, updateBtn, deleteBtn, viewBtn);
        for (JButton b : btnsList) {
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(new EmptyBorder(8,16,8,16));
        }
        addBtn.setBackground(ADD_GREEN);
        updateBtn.setBackground(UPD_ORANGE);
        deleteBtn.setBackground(DEL_RED);
        viewBtn.setBackground(PRIMARY);

        // Disable until selection
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        viewBtn.setEnabled(false);

        // Selection listener
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean sel = table.getSelectedRow() >= 0;
                updateBtn.setEnabled(sel);
                deleteBtn.setEnabled(sel);
                viewBtn.setEnabled(sel);
            }
        });

        // ActionListeners
        addBtn.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent ignored) {
                new AddRecordDialog(EmployeeManagementFrame.this, repo, EmployeeManagementFrame.this::loadTable)
                    .setVisible(true);
            }
        });
        updateBtn.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent ignored) {
                int r = table.getSelectedRow(); if (r < 0) return;
                String id = (String) model.getValueAt(r, 0);
                try {
                    for (Object o : repo.loadAll()) {
                        Employee emp = (Employee) o;
                        if (emp.getId().equals(id)) {
                            new UpdateDialog(EmployeeManagementFrame.this, repo, emp, EmployeeManagementFrame.this::loadTable)
                                .setVisible(true);
                            return;
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(EmployeeManagementFrame.this,
                        "Cannot edit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        deleteBtn.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent ignored) {
                int r = table.getSelectedRow(); if (r < 0) return;
                if (JOptionPane.showConfirmDialog(EmployeeManagementFrame.this,
                    "Delete this employee?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                    return;
                String id = (String) model.getValueAt(r, 0);
                try {
                    List<Employee> tmp = new ArrayList<>();
                    for (Object o : repo.loadAll()) {
                        Employee emp = (Employee) o;
                        if (!emp.getId().equals(id)) tmp.add(emp);
                    }
                    repo.saveAll(tmp);
                    loadTable();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(EmployeeManagementFrame.this,
                        "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        viewBtn.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent ignored) {
                int r = table.getSelectedRow(); if (r < 0) return;
                String id = (String) model.getValueAt(r, 0);
                try {
                    for (Object o : repo.loadAll()) {
                        Employee emp = (Employee) o;
                        if (emp.getId().equals(id)) {
                            new PayslipSplitDialog(EmployeeManagementFrame.this, emp)
                                .setVisible(true);
                            return;
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(EmployeeManagementFrame.this,
                        "Cannot open: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout side panel
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(BG_WHITE);
        side.setBorder(new EmptyBorder(10,10,10,10));
        side.add(addBtn); side.add(Box.createVerticalStrut(8));
        side.add(updateBtn); side.add(Box.createVerticalStrut(8));
        side.add(deleteBtn); side.add(Box.createVerticalStrut(8));
        side.add(viewBtn);

        getContentPane().setBackground(BG_WHITE);
        setLayout(new BorderLayout());
        add(side, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900,550);
        setLocationRelativeTo(null);
        loadTable();
    }

    private void loadTable() {
        model.setRowCount(0);
        try {
            List<Employee> employees = repo.loadAll();
            if (employees.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No employee records found. Please check if the CSV file exists at: " + 
                    new java.io.File("data" + File.separator + "MotorPH Employee Record.csv").getAbsolutePath(),
                    "No Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            for (Employee emp : employees) {
                model.addRow(new Object[]{
                    emp.getId(), emp.getLastName(), emp.getFirstName(),
                    emp.getSssNumber(), emp.getPhilHealthNumber(),
                    emp.getTinNumber(), emp.getPagIbigNumber()
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Load failed: " + e.getMessage() + "\n" +
                "Please check if the CSV file exists at: " + 
                new java.io.File("data" + File.separator + "MotorPH Employee Record.csv").getAbsolutePath(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Show login dialog first
            boolean loginSuccess = showLoginDialog();
            if (!loginSuccess) {
                System.exit(0);
            }

            // If login successful, show the employee management frame
            EmployeeRepository repo = new CsvEmployeeRepository(
                "data" + File.separator + "MotorPH Employee Record.csv"
            );
            new EmployeeManagementFrame(repo).setVisible(true);
        });
    }
    
    private static boolean showLoginDialog() {
        JDialog loginDialog = new JDialog((Frame) null, "MotorPH Employee Management - Login", true);
        loginDialog.setSize(520, 250);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginDialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Employee Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Login form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username field
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField usernameField = new JTextField(30);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(280, 35));
        formPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPasswordField passwordField = new JPasswordField(30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(280, 35));
        formPanel.add(passwordField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
        
        loginBtn.setPreferredSize(new Dimension(100, 35));
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        loginBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        final boolean[] loginSuccess = {false};
        
        loginBtn.addActionListener(_ -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            
            if ("admin".equals(username) && "admin123".equals(password)) {
                loginSuccess[0] = true;
                loginDialog.dispose();
                JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                usernameField.requestFocus();
            }
        });
        
        cancelBtn.addActionListener(_ -> {
            loginSuccess[0] = false;
            loginDialog.dispose();
        });
        
        // Enter key to login
        loginDialog.getRootPane().setDefaultButton(loginBtn);
        
        loginDialog.add(mainPanel);
        loginDialog.setVisible(true);
        
        return loginSuccess[0];
    }
}
