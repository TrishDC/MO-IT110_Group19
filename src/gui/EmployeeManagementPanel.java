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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EmployeeManagementPanel extends JPanel {

    private static final String CARD_LIST = "LIST";
    private static final String CARD_DETAILS = "DETAILS";

    private static final String[] TABLE_COLUMNS = {
            "Employee No.", "Name", "Status", "Position", "Immediate Supervisor", "Role"
    };

    private static final Color BORDER = new Color(220, 220, 220);

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
        setBorder(new EmptyBorder(0, 0, 0, 0));

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
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(0, 0, 0, 0));
        outer.add(createContentArea(), BorderLayout.CENTER);
        return outer;
    }

    private JPanel createActionBar() {
        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftActions.setOpaque(false);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        searchBtn = createSearchButton("Search");
        addBtn = createActionButton("Add");
        updateBtn = createActionButton("Update");
        deleteBtn = createActionButton("Delete");
        viewBtn = createActionButton("View");
        refreshBtn = createRefreshButton("Refresh");

        leftActions.add(searchField);
        leftActions.add(searchBtn);

        rightActions.add(addBtn);
        rightActions.add(updateBtn);
        rightActions.add(deleteBtn);
        rightActions.add(viewBtn);
        rightActions.add(refreshBtn);

        actions.add(leftActions, BorderLayout.WEST);
        actions.add(rightActions, BorderLayout.EAST);

        bindActionEvents();
        applyPermissions();
        updateActionButtonStates();

        return actions;
    }

    private JPanel createContentArea() {
        contentCardPanel.setOpaque(false);

        contentCardPanel.add(createListCard(), CARD_LIST);

        detailsPanel = new EmployeeDetailsPanel(this::showListCard);
        detailsPanel.setActionListener(new EmployeeDetailsPanel.EmployeeDetailsActionListener() {
            @Override
            public void onCreate(EmployeeDetailsPanel panel) {
                handleCreateFromDetails(panel);
            }

            @Override
            public void onUpdate(EmployeeDetailsPanel panel) {
                handleUpdateFromDetails(panel);
            }
        });
        contentCardPanel.add(detailsPanel, CARD_DETAILS);

        return contentCardPanel;
    }

    private JPanel createListCard() {
        JPanel listCard = new JPanel(new BorderLayout(0, 16));
        listCard.setOpaque(false);

        listCard.add(createActionBar(), BorderLayout.NORTH);
        listCard.add(createTableCard(), BorderLayout.CENTER);

        return listCard;
    }

    private JPanel createTableCard() {
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(0, 0, 0, 0)
        ));

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(new Color(245, 245, 245));
        tableScrollPane.setBackground(new Color(245, 245, 245));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 14, 10, 14));
        footer.add(infoLabel, BorderLayout.WEST);

        tableCard.add(tableScrollPane, BorderLayout.CENTER);
        tableCard.add(footer, BorderLayout.SOUTH);

        return tableCard;
    }

    private JTextField createSearchField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(210, 44));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 12, 0, 12)
        ));
        return field;
    }

    private JLabel createInfoLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(130, 130, 130));
        return label;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 44));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createSearchButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 44));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BORDER));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createRefreshButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 44));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BORDER));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        employeeTable.setRowHeight(42);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employeeTable.setFillsViewportHeight(true);

        employeeTable.setBackground(new Color(245, 245, 245));
        employeeTable.setForeground(new Color(35, 35, 35));
        employeeTable.setSelectionBackground(new Color(200, 212, 232));
        employeeTable.setSelectionForeground(new Color(25, 25, 25));

        employeeTable.setGridColor(new Color(235, 235, 235));
        employeeTable.setShowVerticalLines(false);
        employeeTable.setShowHorizontalLines(true);
        employeeTable.setIntercellSpacing(new Dimension(0, 1));
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = employeeTable.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 44));
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                label.setOpaque(true);
                label.setBackground(Color.BLACK);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));

                return label;
            }
        });

        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean selected, boolean focus, int row, int column) {

                super.getTableCellRendererComponent(table, value, selected, focus, row, column);

                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
                setVerticalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setForeground(new Color(35, 35, 35));

                if (selected) {
                    setBackground(new Color(200, 212, 232));
                } else {
                    setBackground(row % 2 == 0
                            ? new Color(245, 245, 245)
                            : new Color(239, 239, 239));
                }

                return this;
            }
        });

        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(230);
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(220);
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(220);
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(120);

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

    private void showDetailsCard(
            Employee employee,
            EmployeeDetailsPanel.Mode mode,
            boolean showPersonalDetails,
            boolean showGovernmentIds,
            boolean showCompensation
    ) {
        detailsPanel.displayEmployee(
                employee,
                showPersonalDetails,
                showGovernmentIds,
                showCompensation
        );
        detailsPanel.setMode(mode);
        contentCardLayout.show(contentCardPanel, CARD_DETAILS);
    }

    private void handleAddEmployee() {
        if (!canAddEmployee()) {
            showAccessDenied();
            return;
        }

        showDetailsCard(
                null,
                EmployeeDetailsPanel.Mode.CREATE,
                true,
                true,
                true
        );
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

        showDetailsCard(
                employee,
                EmployeeDetailsPanel.Mode.UPDATE,
                canViewPersonalDetails(),
                canViewGovernmentIds(),
                canViewCompensation()
        );
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

        showDetailsCard(
                employee,
                EmployeeDetailsPanel.Mode.VIEW,
                canViewPersonalDetails(),
                canViewGovernmentIds(),
                canViewCompensation()
        );
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

    private void handleCreateFromDetails(EmployeeDetailsPanel panel) {
        try {
            Employee newEmployee = buildEmployeeFromPanel(panel, null);

            List<Employee> employees = repo.loadAll();

            for (Employee existing : employees) {
                if (existing.getId().equalsIgnoreCase(newEmployee.getId())) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Employee ID already exists: " + newEmployee.getId(),
                            "Duplicate Employee ID",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            employees.add(newEmployee);
            repo.saveAll(employees);

            JOptionPane.showMessageDialog(
                    this,
                    "Employee added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadTable();
            showListCard();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save employee: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleUpdateFromDetails(EmployeeDetailsPanel panel) {
        try {
            Employee original = detailsPanel.getCurrentEmployee();

            if (original == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No employee is selected for update.",
                        "Update Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Employee updatedEmployee = buildEmployeeFromPanel(panel, original);

            List<Employee> employees = repo.loadAll();
            List<Employee> updatedList = new ArrayList<>();

            for (Employee emp : employees) {
                if (emp.getId().equals(original.getId())) {
                    updatedList.add(updatedEmployee);
                } else {
                    updatedList.add(emp);
                }
            }

            repo.saveAll(updatedList);

            JOptionPane.showMessageDialog(
                    this,
                    "Employee updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadTable();
            showListCard();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to update employee: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private Employee buildEmployeeFromPanel(EmployeeDetailsPanel panel, Employee existingEmployee) {
        String id = requireValue(panel.getEmployeeIdInput(), "Employee No.");
        String firstName = requireValue(panel.getFirstNameInput(), "First Name");
        String lastName = requireValue(panel.getLastNameInput(), "Last Name");
        LocalDate birthDate = parseBirthDate(panel.getBirthDateInput());

        BigDecimal basicSalary = parseMoney(panel.getBasicSalaryInput(), "Basic Salary");
        BigDecimal riceSubsidy = parseMoney(panel.getRiceSubsidyInput(), "Rice Subsidy");
        BigDecimal phoneAllowance = parseMoney(panel.getPhoneAllowanceInput(), "Phone Allowance");
        BigDecimal clothingAllowance = parseMoney(panel.getClothingAllowanceInput(), "Clothing Allowance");
        BigDecimal grossSemiMonthly = parseMoney(panel.getGrossSemiMonthlyInput(), "Gross Semi-Monthly Rate");
        BigDecimal hourlyRate = parseMoney(panel.getHourlyRateInput(), "Hourly Rate");

        Employee employee = new Employee(
                id,
                firstName,
                lastName,
                birthDate,
                basicSalary,
                riceSubsidy,
                phoneAllowance,
                clothingAllowance,
                grossSemiMonthly,
                hourlyRate
        ) {
            @Override
            public BigDecimal calculateSalary() {
                return getBasicSalary()
                        .add(getRiceSubsidy())
                        .add(getPhoneAllowance())
                        .add(getClothingAllowance());
            }

            @Override
            public String getEmployeeType() {
                return existingEmployee != null
                        ? existingEmployee.getEmployeeType()
                        : "Employee";
            }
        };

        employee.setStatus(panel.getStatusInput());
        employee.setPosition(panel.getPositionInput());
        employee.setSupervisor(panel.getSupervisorInput());

        if (!panel.getAddressInput().isBlank()) {
            employee.setAddress(panel.getAddressInput());
        } else {
            employee.setAddress("");
        }

        if (!panel.getPhoneInput().isBlank()) {
            employee.setPhone(panel.getPhoneInput());
        }

        if (!panel.getSssInput().isBlank()) {
            employee.setSssNumber(panel.getSssInput());
        }

        if (!panel.getPhilHealthInput().isBlank()) {
            employee.setPhilHealthNumber(panel.getPhilHealthInput());
        }

        if (!panel.getTinInput().isBlank()) {
            employee.setTinNumber(panel.getTinInput());
        }

        if (!panel.getPagIbigInput().isBlank()) {
            employee.setPagIbigNumber(panel.getPagIbigInput());
        }

        employee.setRole(resolveRoleFromInput(panel.getRoleInput(), existingEmployee));

        return employee;
    }

    private String requireValue(String value, String fieldName) {
        String cleaned = safe(value);
        if (cleaned.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return cleaned;
    }

    private LocalDate parseBirthDate(String input) {
        String cleaned = requireValue(input, "Birth date");

        try {
            return LocalDate.parse(cleaned, java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Birth date must use format: Month dd, yyyy (e.g. January 05, 2000).");
        }
    }

    private BigDecimal parseMoney(String input, String fieldName) {
        String cleaned = safe(input).replace(",", "");

        if (cleaned.isBlank()) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private Role resolveRoleFromInput(String roleInput, Employee existingEmployee) {
        String roleName = safe(roleInput);

        if (roleName.isBlank()) {
            return existingEmployee != null ? existingEmployee.getRole() : null;
        }

        if (existingEmployee != null
                && existingEmployee.getRole() != null
                && roleName.equalsIgnoreCase(existingEmployee.getRole().getName())) {
            return existingEmployee.getRole();
        }

        if (currentUser != null
                && currentUser.getRole() != null
                && roleName.equalsIgnoreCase(currentUser.getRole().getName())) {
            return currentUser.getRole();
        }

        try {
            for (Employee emp : repo.loadAll()) {
                if (emp.getRole() != null && roleName.equalsIgnoreCase(emp.getRole().getName())) {
                    return emp.getRole();
                }
            }
        } catch (IOException e) {
            // fall through
        }

        return existingEmployee != null ? existingEmployee.getRole() : null;
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