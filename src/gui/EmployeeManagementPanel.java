/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import RBAC.Permission;
import RBAC.Role;
import model.Employee;
import repository.EmployeeRepository;
import service.AuthorizationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {

    private static final String CARD_LIST = "LIST";
    private static final String CARD_DETAILS = "DETAILS";
    private static final String CARD_ADD = "ADD";
    private static final String CARD_UPDATE = "UPDATE";

    private static final String[] TABLE_COLUMNS = {
            "Employee No.", "Name", "Status", "Position", "Immediate Supervisor", "Role"
    };

    private static final Color PAGE_BG = new Color(242, 242, 242);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color HEADER_BG = Color.BLACK;
    private static final Color HEADER_FG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(30, 30, 30);
    private static final Color MUTED = new Color(120, 120, 120);
    private static final Color BORDER = new Color(220, 220, 220);
    private static final Color SELECT_BG = new Color(225, 235, 255);
    private static final Color ALT_ROW = new Color(250, 250, 250);

    private final EmployeeRepository repo;
    private final Path employeeCsvPath;
    private final Employee currentUser;

    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField searchField;
    private final JLabel infoLabel;

    private final CardLayout contentCardLayout = new CardLayout();
    private final JPanel contentCardPanel = new JPanel(contentCardLayout);

    private EmployeeDetailsPanel detailsPanel;
    private EmployeeFormPanel addFormPanel;
    private EmployeeFormPanel updateFormPanel;

    private JButton searchBtn;
    private JButton addBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton viewBtn;
    private JButton refreshBtn;

    public EmployeeManagementPanel(EmployeeRepository repo, Path employeeCsvPath, Employee currentUser) {
        this.repo = repo;
        this.employeeCsvPath = employeeCsvPath;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(110, 50, 20, 50));

        this.model = createTableModel();
        this.table = createEmployeeTable();
        this.searchField = createSearchField();
        this.infoLabel = createInfoLabel();

        add(createMainLayout(), BorderLayout.CENTER);

        if (canViewEmployeeList()) {
            loadTable();
            showListCard();
        } else {
            showAccessLimitedState();
            showListCard();
        }
    }

    private JPanel createMainLayout() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);

        wrapper.add(createActionBar(), BorderLayout.NORTH);
        wrapper.add(createContentArea(), BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createActionBar() {
        JPanel actionBar = new JPanel(new BorderLayout(12, 0));
        actionBar.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        searchBtn = createOutlineButton("Search");

        left.add(searchField);
        left.add(searchBtn);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        addBtn = createBlackActionButton("Add Employee");
        updateBtn = createBlackActionButton("Update Employee");
        deleteBtn = createBlackActionButton("Delete Employee");
        viewBtn = createBlackActionButton("View Details");
        refreshBtn = createOutlineButton("Refresh");

        right.add(addBtn);
        right.add(updateBtn);
        right.add(deleteBtn);
        right.add(viewBtn);
        right.add(refreshBtn);

        actionBar.add(left, BorderLayout.WEST);
        actionBar.add(right, BorderLayout.EAST);

        bindActionEvents();
        applyPermissions();
        updateActionButtonStates();

        return actionBar;
    }

    private JPanel createContentArea() {
        contentCardPanel.setOpaque(false);

        contentCardPanel.add(createListCard(), CARD_LIST);

        detailsPanel = new EmployeeDetailsPanel(this::showListCard);
        contentCardPanel.add(detailsPanel, CARD_DETAILS);

        addFormPanel = new EmployeeFormPanel(
                "Add Employee",
                repo,
                null,
                this::handleFormSaved,
                this::showListCard
        );
        contentCardPanel.add(addFormPanel, CARD_ADD);

        updateFormPanel = new EmployeeFormPanel(
                "Update Employee",
                repo,
                null,
                this::handleFormSaved,
                this::showListCard
        );
        contentCardPanel.add(updateFormPanel, CARD_UPDATE);

        return contentCardPanel;
    }

    private JPanel createListCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 0, 0, 0)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 14, 10, 14));
        footer.add(infoLabel, BorderLayout.WEST);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JTextField createSearchField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(240, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 12, 0, 12)
        ));
        return field;
    }

    private JLabel createInfoLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(MUTED);
        return label;
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
        button.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
        button.setPreferredSize(new Dimension(145, 40));
        return button;
    }

    private JButton createOutlineButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(TEXT_DARK);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 16, 10, 16)
        ));
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createEmployeeTable() {
        JTable employeeTable = new JTable(model);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(40);
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
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_FG);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 52));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                label.setHorizontalAlignment(CENTER);
                label.setOpaque(true);
                label.setBackground(HEADER_BG);
                label.setForeground(HEADER_FG);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                return label;
            }
        });

        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean selected, boolean focus, int row, int col) {

                super.getTableCellRendererComponent(table, value, selected, focus, row, col);

                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(CENTER);

                if (selected) {
                    setBackground(SELECT_BG);
                    setForeground(TEXT_DARK);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW);
                    setForeground(TEXT_DARK);
                }

                return this;
            }
        });

        employeeTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                updateActionButtonStates();
            }
        });

        return employeeTable;
    }

    private void bindActionEvents() {
        searchBtn.addActionListener(e -> filterTable());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadTable();
            showListCard();
        });

        addBtn.addActionListener(e -> handleAddEmployee());
        updateBtn.addActionListener(e -> handleUpdateEmployee());
        deleteBtn.addActionListener(e -> handleDeleteEmployee());
        viewBtn.addActionListener(e -> handleViewEmployee());

        searchField.addActionListener(e -> filterTable());
    }

    private void applyPermissions() {
        boolean canViewList = canViewEmployeeList();
        boolean canAdd = canAddEmployee();
        boolean canEdit = canEditEmployee();
        boolean canDelete = canDeleteEmployee();
        boolean canViewDetails = canViewEmployeeDetails();

        searchField.setEnabled(canViewList);
        searchBtn.setVisible(canViewList);

        addBtn.setVisible(canAdd);
        updateBtn.setVisible(canEdit);
        deleteBtn.setVisible(canDelete);
        viewBtn.setVisible(canViewDetails);
        refreshBtn.setVisible(canViewList);

        table.setEnabled(canViewList);
    }

    private void updateActionButtonStates() {
        boolean rowSelected = table.getSelectedRow() >= 0;

        if (viewBtn != null) {
            viewBtn.setEnabled(rowSelected && canViewEmployeeDetails());
        }

        if (updateBtn != null) {
            updateBtn.setEnabled(rowSelected && canEditEmployee());
        }

        if (deleteBtn != null) {
            deleteBtn.setEnabled(rowSelected && canDeleteEmployee());
        }
    }

    private boolean canViewEmployeeList() {
        return hasPermission(Permission.VIEW_EMPLOYEE_LIST) || hasPermission(Permission.VIEW_EMPLOYEE);
    }

    private boolean canViewEmployeeDetails() {
        return hasPermission(Permission.VIEW_EMPLOYEE_BASIC_DETAILS) || hasPermission(Permission.VIEW_EMPLOYEE);
    }

    private boolean canViewPersonalDetails() {
        return hasPermission(Permission.VIEW_EMPLOYEE_PERSONAL_DETAILS);
    }

    private boolean canViewGovernmentIds() {
        return hasPermission(Permission.VIEW_EMPLOYEE_GOVERNMENT_IDS);
    }

    private boolean canViewCompensation() {
        return hasPermission(Permission.VIEW_EMPLOYEE_COMPENSATION);
    }

    private boolean canAddEmployee() {
        return hasPermission(Permission.ADD_EMPLOYEE);
    }

    private boolean canEditEmployee() {
        return hasPermission(Permission.EDIT_EMPLOYEE);
    }

    private boolean canDeleteEmployee() {
        return hasPermission(Permission.DELETE_EMPLOYEE);
    }

    private boolean hasPermission(Permission permission) {
        return AuthorizationService.hasPermission(currentUser, permission);
    }

    private void showAccessLimitedState() {
        model.setRowCount(0);
        infoLabel.setText("You do not have permission to view the employee directory.");
    }

    private void showListCard() {
        contentCardLayout.show(contentCardPanel, CARD_LIST);
    }

    private void showDetailsCard(Employee employee) {
        detailsPanel.displayEmployee(
                employee,
                canViewPersonalDetails(),
                canViewGovernmentIds(),
                canViewCompensation()
        );
        contentCardLayout.show(contentCardPanel, CARD_DETAILS);
    }

    private void showAddCard() {
        addFormPanel.setEmployee(null);
        contentCardLayout.show(contentCardPanel, CARD_ADD);
    }

    private void showUpdateCard(Employee employee) {
        updateFormPanel.setEmployee(employee);
        contentCardLayout.show(contentCardPanel, CARD_UPDATE);
    }

    private void handleFormSaved() {
        loadTable();
        showListCard();
    }

    private void handleAddEmployee() {
        if (!canAddEmployee()) {
            showAccessDenied();
            return;
        }
        showAddCard();
    }

    private void handleUpdateEmployee() {
        if (!canEditEmployee()) {
            showAccessDenied();
            return;
        }

        Employee employee = getSelectedEmployee();
        if (employee == null) {
            return;
        }

        showUpdateCard(employee);
    }

    private void handleDeleteEmployee() {
        if (!canDeleteEmployee()) {
            showAccessDenied();
            return;
        }

        Employee employee = getSelectedEmployee();
        if (employee == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete employee " + employee.getId() + " - " + employee.getLastName() + ", " + employee.getFirstName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            List<Employee> updated = new ArrayList<>();
            for (Employee emp : repo.loadAll()) {
                if (!emp.getId().equals(employee.getId())) {
                    updated.add(emp);
                }
            }
            repo.saveAll(updated);
            loadTable();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Delete failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleViewEmployee() {
        if (!canViewEmployeeDetails()) {
            showAccessDenied();
            return;
        }

        Employee employee = getSelectedEmployee();
        if (employee == null) {
            return;
        }

        showDetailsCard(employee);
    }

    private Employee getSelectedEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        String employeeId = String.valueOf(model.getValueAt(selectedRow, 0));

        try {
            for (Employee employee : repo.loadAll()) {
                if (employeeId.equals(employee.getId())) {
                    return employee;
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load selected employee: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return null;
    }

    private void filterTable() {
        if (!canViewEmployeeList()) {
            showAccessDenied();
            return;
        }

        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadTable();
            return;
        }

        model.setRowCount(0);

        try {
            List<Employee> employees = repo.loadAll();
            int count = 0;

            for (Employee emp : employees) {
                String id = safe(emp.getId());
                String name = (safe(emp.getLastName()) + ", " + safe(emp.getFirstName())).trim();
                String status = safe(emp.getStatus());
                String position = safe(emp.getPosition());
                String supervisor = safe(emp.getSupervisor());
                String roleName = getRoleName(emp);

                String combined = (id + " " + name + " " + status + " " + position + " " + supervisor + " " + roleName)
                        .toLowerCase();

                if (combined.contains(keyword)) {
                    model.addRow(new Object[]{
                            id,
                            name,
                            status,
                            position,
                            supervisor,
                            roleName
                    });
                    count++;
                }
            }

            infoLabel.setText(count + " employee record(s) found.");
            showListCard();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Search failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadTable() {
        if (!canViewEmployeeList()) {
            showAccessLimitedState();
            return;
        }

        model.setRowCount(0);

        try {
            List<Employee> employees = repo.loadAll();

            if (employees.isEmpty()) {
                infoLabel.setText("No employee records found. Please check: " + employeeCsvPath.toAbsolutePath());
                return;
            }

            for (Employee emp : employees) {
                model.addRow(new Object[]{
                        safe(emp.getId()),
                        (safe(emp.getLastName()) + ", " + safe(emp.getFirstName())).trim(),
                        safe(emp.getStatus()),
                        safe(emp.getPosition()),
                        safe(emp.getSupervisor()),
                        getRoleName(emp)
                });
            }

            infoLabel.setText(employees.size() + " employee record(s) loaded.");
            updateActionButtonStates();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Load failed: " + e.getMessage() + "\nPlease check if the CSV file exists at: "
                            + employeeCsvPath.toAbsolutePath(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String getRoleName(Employee employee) {
        if (employee == null) {
            return "";
        }

        Role role = employee.getRole();
        return role == null ? "" : safe(role.getName());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private void showAccessDenied() {
        JOptionPane.showMessageDialog(
                this,
                "You do not have permission for this action.",
                "Access Denied",
                JOptionPane.WARNING_MESSAGE
        );
    }
}