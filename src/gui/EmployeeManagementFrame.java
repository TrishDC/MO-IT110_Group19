package gui;

import RBAC.Permission;

import model.Employee;

import repository.CsvEmployeeRepository;
import repository.EmployeeRepository;

import service.AuthorizationService;
import service.SessionManager;
import service.EmployeeLeaveUiService;
import service.InMemoryEmployeeLeaveUiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementFrame extends JFrame {

    private final EmployeeRepository repo;
    private final Path employeeCsvPath;

    private final JTable table;
    private final DefaultTableModel model;

    private final Employee currentUser;

    private final String loggedInName;
    private final String loggedInPosition;

    private JButton addBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton viewBtn;

    private static final Color SIDEBAR_BG = Color.BLACK;
    private static final Color MAIN_BG = new Color(242, 242, 242);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_DARK = new Color(30, 30, 30);
    private static final Color ACCENT = new Color(20, 20, 90);
    private static final Color MUTED = new Color(145, 145, 145);
    private static final Color TABLE_HEADER_BG = new Color(245, 245, 245);
    private static final Color TABLE_BORDER = new Color(220, 220, 220);
    private static final Color SELECT_BG = new Color(225, 235, 255);

    public EmployeeManagementFrame(EmployeeRepository repo, Path employeeCsvPath, Employee loggedInEmployee) {
        super("MotorPH Payroll System - Employee Management");

        this.repo = repo;
        this.employeeCsvPath = employeeCsvPath;
        this.currentUser = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser()
                : loggedInEmployee;

        this.loggedInName = currentUser != null
                ? (safe(currentUser.getFirstName()) + " " + safe(currentUser.getLastName())).trim()
                : "Name";

        this.loggedInPosition = currentUser != null && currentUser.getPosition() != null
                ? currentUser.getPosition().trim()
                : "Position";

        applyGlobalFont();

        this.model = createTableModel();
        this.table = createEmployeeTable();

        initFrame();
        loadTable();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private void applyGlobalFont() {
        Font segoe = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Label.font", segoe);
        UIManager.put("Button.font", segoe);
        UIManager.put("Table.font", segoe);
        UIManager.put("TableHeader.font", segoe.deriveFont(Font.BOLD, 14f));
        UIManager.put("TextField.font", segoe);
        UIManager.put("PasswordField.font", segoe);
        UIManager.put("ComboBox.font", segoe);
        UIManager.put("OptionPane.font", segoe);
        UIManager.put("OptionPane.messageFont", segoe);
        UIManager.put("OptionPane.buttonFont", segoe);
    }

    private void initFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 680));
        setSize(1280, 760);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(MAIN_BG);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createMainArea(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(270, 0));
        sidebar.setBorder(new EmptyBorder(28, 30, 28, 30));

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel("MotorPH");
        logoLabel.setForeground(TEXT_LIGHT);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topSection.add(logoLabel);
        topSection.add(Box.createVerticalStrut(50));

        topSection.add(createNavLink("Dashboard", false, () -> showNavigationMessage("Dashboard")));
        topSection.add(Box.createVerticalStrut(22));
        topSection.add(createNavLink("Employees", true, () -> {}));
        topSection.add(Box.createVerticalStrut(22));
        topSection.add(createNavLink("Payroll", false, () -> showNavigationMessage("Payroll")));
        topSection.add(Box.createVerticalStrut(22));
        topSection.add(createNavLink("Leave", false, this::openLeavePage));
        topSection.add(Box.createVerticalStrut(22));
        topSection.add(createNavLink("Attendance", false, () -> showNavigationMessage("Attendance")));

        JPanel bottomSection = new JPanel();
        bottomSection.setOpaque(false);
        bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.Y_AXIS));

        JLabel logoutLabel = createNavLink("Log Out", false, this::handleLogout);
        bottomSection.add(logoutLabel);

        sidebar.add(topSection, BorderLayout.NORTH);
        sidebar.add(bottomSection, BorderLayout.SOUTH);

        return sidebar;
    }

    private JLabel createNavLink(String text, boolean selected, Runnable action) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_LIGHT);
        label.setFont(new Font("Segoe UI", selected ? Font.BOLD : Font.PLAIN, 16));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        label.setPreferredSize(new Dimension(180, 28));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(new Color(210, 210, 210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(TEXT_LIGHT);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return label;
    }

    private JPanel createMainArea() {
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(MAIN_BG);
        mainArea.setBorder(new EmptyBorder(24, 28, 24, 28));

        mainArea.add(createTopBar(), BorderLayout.NORTH);
        mainArea.add(createContentArea(), BorderLayout.CENTER);

        return mainArea;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel rightProfile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightProfile.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(loggedInName);
        nameLabel.setForeground(ACCENT);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel positionLabel = new JLabel(loggedInPosition);
        positionLabel.setForeground(MUTED);
        positionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        positionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(positionLabel);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.BLACK);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(56, 56));
        avatar.setMinimumSize(new Dimension(56, 56));
        avatar.setMaximumSize(new Dimension(56, 56));

        rightProfile.add(textPanel);
        rightProfile.add(avatar);

        topBar.add(rightProfile, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createContentArea() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(createTablePanel(), BorderLayout.CENTER);
        return content;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TABLE_BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        panel.add(createTableToolbar(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTableToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        addBtn = createBlackActionButton("Add Employee");
        updateBtn = createBlackActionButton("Update Employee");
        deleteBtn = createBlackActionButton("Delete Employee");
        viewBtn = createBlackActionButton("View Employee");

        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        viewBtn.setEnabled(false);

        bindActionEvents();
        applyPermissions();

        toolbar.add(addBtn);
        toolbar.add(updateBtn);
        toolbar.add(deleteBtn);
        toolbar.add(viewBtn);

        return toolbar;
    }

    private JButton createBlackActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setPreferredSize(new Dimension(145, 40));
        return button;
    }

    private DefaultTableModel createTableModel() {
        String[] cols = {
                "Employee #", "Last Name", "First Name", "SSS #",
                "PhilHealth #", "TIN #", "Pag-IBIG #"
        };

        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createEmployeeTable() {
        JTable employeeTable = new JTable(model);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(34);
        employeeTable.setShowGrid(false);
        employeeTable.setIntercellSpacing(new Dimension(0, 0));
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employeeTable.setBackground(Color.WHITE);
        employeeTable.setForeground(TEXT_DARK);
        employeeTable.setSelectionBackground(SELECT_BG);
        employeeTable.setSelectionForeground(TEXT_DARK);
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = employeeTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_DARK);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                label.setHorizontalAlignment(CENTER);
                label.setBackground(TABLE_HEADER_BG);
                label.setForeground(TEXT_DARK);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TABLE_BORDER));
                return label;
            }
        });

        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean selected, boolean focus, int row, int col) {

                super.getTableCellRendererComponent(t, v, selected, focus, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));

                if (selected) {
                    setBackground(SELECT_BG);
                    setForeground(TEXT_DARK);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                    setForeground(TEXT_DARK);
                }

                if (!selected && (col == 4 || col == 6) && v != null) {
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

        employeeTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            boolean rowSelected = employeeTable.getSelectedRow() >= 0;

            if (updateBtn != null) {
                updateBtn.setEnabled(rowSelected && AuthorizationService.hasPermission(currentUser, Permission.EDIT_EMPLOYEE));
            }

            if (deleteBtn != null) {
                deleteBtn.setEnabled(rowSelected && AuthorizationService.hasPermission(currentUser, Permission.DELETE_EMPLOYEE));
            }

            if (viewBtn != null) {
                viewBtn.setEnabled(rowSelected && AuthorizationService.hasPermission(currentUser, Permission.VIEW_EMPLOYEE));
            }
        });

        return employeeTable;
    }

    private void applyPermissions() {
        boolean canView = AuthorizationService.hasPermission(currentUser, Permission.VIEW_EMPLOYEE);
        boolean canAdd = AuthorizationService.hasPermission(currentUser, Permission.ADD_EMPLOYEE);
        boolean canEdit = AuthorizationService.hasPermission(currentUser, Permission.EDIT_EMPLOYEE);
        boolean canDelete = AuthorizationService.hasPermission(currentUser, Permission.DELETE_EMPLOYEE);

        addBtn.setVisible(canAdd);
        updateBtn.setVisible(canEdit);
        deleteBtn.setVisible(canDelete);
        viewBtn.setVisible(canView);

        table.setEnabled(canView);
    }

    private void bindActionEvents() {
        addBtn.addActionListener(e -> {
            if (!AuthorizationService.hasPermission(currentUser, Permission.ADD_EMPLOYEE)) {
                showAccessDenied();
                return;
            }

            new AddRecordDialog(EmployeeManagementFrame.this, repo, EmployeeManagementFrame.this::loadTable)
                    .setVisible(true);
        });

        updateBtn.addActionListener(e -> {
            if (!AuthorizationService.hasPermission(currentUser, Permission.EDIT_EMPLOYEE)) {
                showAccessDenied();
                return;
            }

            int r = table.getSelectedRow();
            if (r < 0) return;

            String id = (String) model.getValueAt(r, 0);

            try {
                for (Employee emp : repo.loadAll()) {
                    if (emp.getId().equals(id)) {
                        new UpdateDialog(EmployeeManagementFrame.this, repo, emp,
                                EmployeeManagementFrame.this::loadTable).setVisible(true);
                        return;
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        EmployeeManagementFrame.this,
                        "Cannot edit: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        deleteBtn.addActionListener(e -> {
            if (!AuthorizationService.hasPermission(currentUser, Permission.DELETE_EMPLOYEE)) {
                showAccessDenied();
                return;
            }

            int r = table.getSelectedRow();
            if (r < 0) return;

            if (JOptionPane.showConfirmDialog(
                    EmployeeManagementFrame.this,
                    "Delete this employee?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION) {
                return;
            }

            String id = (String) model.getValueAt(r, 0);

            try {
                List<Employee> tmp = new ArrayList<>();
                for (Employee emp : repo.loadAll()) {
                    if (!emp.getId().equals(id)) {
                        tmp.add(emp);
                    }
                }
                repo.saveAll(tmp);
                loadTable();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        EmployeeManagementFrame.this,
                        "Delete failed: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        viewBtn.addActionListener(e -> {
            if (!AuthorizationService.hasPermission(currentUser, Permission.VIEW_EMPLOYEE)) {
                showAccessDenied();
                return;
            }

            int r = table.getSelectedRow();
            if (r < 0) return;

            String id = (String) model.getValueAt(r, 0);

            try {
                for (Employee emp : repo.loadAll()) {
                    if (emp.getId().equals(id)) {
                        new PayslipSplitDialog(EmployeeManagementFrame.this, emp).setVisible(true);
                        return;
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        EmployeeManagementFrame.this,
                        "Cannot open: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private void showAccessDenied() {
        JOptionPane.showMessageDialog(
                this,
                "You do not have permission for this action.",
                "Access Denied",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showNavigationMessage(String pageName) {
        JOptionPane.showMessageDialog(
                this,
                pageName + " page navigation is not implemented yet.",
                "Navigation",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void openLeavePage() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No logged-in employee found.",
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            EmployeeLeaveUiService leaveService = new InMemoryEmployeeLeaveUiService();

            String employeeId = safe(currentUser.getId());
            String employeeName = (safe(currentUser.getFirstName()) + " " + safe(currentUser.getLastName())).trim();
            String department = getDepartmentSafe(currentUser);
            String position = safe(currentUser.getPosition());

            EmployeeLeavesFrame leaveFrame = new EmployeeLeavesFrame(
                    leaveService,
                    employeeId,
                    employeeName,
                    department,
                    position
            );

            leaveFrame.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open Leave page: " + ex.getMessage(),
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String getDepartmentSafe(Employee employee) {
        return "N/A";
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Log Out",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        SessionManager.logout();
        dispose();

        SwingUtilities.invokeLater(() -> {
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
            EmployeeRepository newRepo = new CsvEmployeeRepository(csvPath.toString());

            new EmployeeManagementFrame(newRepo, csvPath, loggedInEmployee).setVisible(true);
        });
    }

    private void loadTable() {
        model.setRowCount(0);

        try {
            List<Employee> employees = repo.loadAll();

            if (employees.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No employee records found. Please check if the CSV file exists at: "
                                + employeeCsvPath.toAbsolutePath(),
                        "No Data",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            for (Employee emp : employees) {
                model.addRow(new Object[]{
                        emp.getId(),
                        emp.getLastName(),
                        emp.getFirstName(),
                        emp.getSssNumber(),
                        emp.getPhilHealthNumber(),
                        emp.getTinNumber(),
                        emp.getPagIbigNumber()
                });
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Load failed: " + e.getMessage() + "\n"
                            + "Please check if the CSV file exists at: "
                            + employeeCsvPath.toAbsolutePath(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static Path resolveEmployeeCsvPath() {
        String fileName = "MotorPH Employee Record.csv";
        Path[] candidates = new Path[]{
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