package gui;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import model.Employee;
import repository.CsvEmployeeRepository;
import repository.EmployeeRepository;
import RBAC.Permission;
import RBAC.Role;

public class EmployeeManagementFrame extends JFrame {
    private final EmployeeRepository repo;
    private final Path employeeCsvPath;
    private final JTable table;
    private final DefaultTableModel model;
    private String loggedInUsername;
    private boolean canEditEmployees = false;

    public EmployeeManagementFrame(EmployeeRepository repo, Path employeeCsvPath, String loggedInUsername) {
        super("Employee Management");
        this.loggedInUsername = loggedInUsername;
        this.repo = repo;
        this.employeeCsvPath = employeeCsvPath;
        checkUserPermissions();

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
        JButton leaveBtn = new JButton("Leave Management");

        List<JButton> btnsList = List.of(addBtn, updateBtn, deleteBtn, viewBtn, leaveBtn);
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
        leaveBtn.setBackground(DARK_PRIMARY);

        // Check RBAC permissions and disable buttons if user cannot edit employees
        if (!canEditEmployees) {
            addBtn.setEnabled(false);
            addBtn.setToolTipText("You don't have permission to add employees");
            updateBtn.setEnabled(false);
            updateBtn.setToolTipText("You don't have permission to edit employees");
            deleteBtn.setEnabled(false);
            deleteBtn.setToolTipText("You don't have permission to delete employees");
        } else {
            // Disable until selection
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
        viewBtn.setEnabled(false);

        // Selection listener
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean sel = table.getSelectedRow() >= 0;
                // Only enable update/delete if user has RBAC permission
                if (canEditEmployees) {
                    updateBtn.setEnabled(sel);
                    deleteBtn.setEnabled(sel);
                }
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
        leaveBtn.addActionListener(e -> {
            new LeaveManagementFrame().setVisible(true);
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
        side.add(Box.createVerticalStrut(8));
        side.add(leaveBtn);

        getContentPane().setBackground(BG_WHITE);
        setLayout(new BorderLayout());
        add(side, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900,550);
        setLocationRelativeTo(null);
        loadTable();
    }

    private void checkUserPermissions() {
        // Keep the initial admin account as full-access even if it is not in employee CSV.
        if (loggedInUsername != null && "admin".equalsIgnoreCase(loggedInUsername.trim())) {
            canEditEmployees = true;
            return;
        }

        try {
            List<Employee> employees = repo.loadAll();
            for (Employee emp : employees) {
                if (emp.getId().equals(loggedInUsername)) {
                    Role role = emp.getRole();
                    if (role != null) {
                        canEditEmployees = role.hasPermission(Permission.EDIT_EMPLOYEE);
                        System.out.println("User " + loggedInUsername + " has role: " + role.getName() + 
                                         ", can edit: " + canEditEmployees);
                    } else {
                        System.out.println("User " + loggedInUsername + " has no role assigned");
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking permissions: " + e.getMessage());
            canEditEmployees = false;
        }
    }

    private void loadTable() {
        model.setRowCount(0);
        try {
            List<Employee> employees = repo.loadAll();
            if (employees.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No employee records found. Please check if the CSV file exists at: " + 
                    employeeCsvPath.toAbsolutePath(),
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
                employeeCsvPath.toAbsolutePath(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Show login dialog first
            JFrame dummy = new JFrame();
            dummy.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            LoginDialog loginDialog = new LoginDialog(dummy);
            loginDialog.setVisible(true);
            boolean loginSuccess = loginDialog.isSucceeded();
            String username = loginSuccess ? loginDialog.getUsername() : null;
            dummy.dispose();
            
            if (!loginSuccess || username == null) {
                System.exit(0);
            }

            // If login successful, show the employee management frame with username
            Path csvPath = resolveEmployeeCsvPath();
            EmployeeRepository repo = new CsvEmployeeRepository(csvPath.toString());
            new EmployeeManagementFrame(repo, csvPath, username).setVisible(true);
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

