package gui;

import RBAC.Permission;
import model.AttendanceRecord;
import model.Employee;
import repository.AttendanceRepository;
import repository.CsvAttendanceRepository;
import repository.EmployeeRepository;
import service.AuthorizationService;
import service.PayrollComputationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PayrollPanel extends JPanel {

    private static final String LIST_CARD = "LIST";
    private static final String DETAIL_CARD = "DETAIL";

    private static final Color PAGE_BG = new Color(245, 245, 245);
    private static final Color BLACK = Color.BLACK;
    private static final Color WHITE = Color.WHITE;
    private static final Color GRID = new Color(220, 220, 220);
    private static final Color FIELD_BORDER = new Color(110, 110, 110);

    private final AttendanceRepository attendanceRepository;
    private final Employee currentUser;
    private final EmployeeRepository employeeRepository;
    private final PayrollComputationService payrollService;

    private final CardLayout innerCardLayout = new CardLayout();
    private final JPanel innerContentPanel = new JPanel(innerCardLayout);
    private final JPanel detailCardHolder = new JPanel(new BorderLayout());

    private final JTextField searchField = new JTextField();
    private final JButton myRecordButton = createActionButton("My Record");
    private final JButton viewAllButton = createActionButton("View All");
    private final JButton generateButton = createActionButton("Generate");
    private final JButton viewButton = createActionButton("View Payslip");
    private final JButton refreshButton = createActionButton("Refresh");

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel infoLabel = new JLabel(" ");

    private List<Employee> allEmployees = new ArrayList<>();
    private List<Employee> displayedEmployees = new ArrayList<>();

    public PayrollPanel(Employee currentUser, EmployeeRepository employeeRepository) {
        this.currentUser = currentUser;
        this.employeeRepository = employeeRepository;
        this.payrollService = new PayrollComputationService();
        this.attendanceRepository = new CsvAttendanceRepository();

        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Employee ID",
                        "Employee Name",
                        "Position",
                        "Basic Salary",
                        "Allowance",
                        "Gross Salary"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable();

        add(buildCardContent(), BorderLayout.CENTER);

        wireEvents();
        applyPermissions();
        loadEmployees();
        loadInitialView();
    }

    private JComponent buildCardContent() {
        innerContentPanel.setOpaque(false);
        innerContentPanel.add(buildListContent(), LIST_CARD);

        detailCardHolder.setOpaque(false);
        innerContentPanel.add(detailCardHolder, DETAIL_CARD);

        return innerContentPanel;
    }

    private JComponent buildListContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        content.add(buildTopBar(), BorderLayout.NORTH);
        content.add(buildTableSection(), BorderLayout.CENTER);

        return content;
    }

    private JComponent buildTopBar() {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchRow.setOpaque(false);

        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setPreferredSize(new Dimension(260, 44));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(0, 14, 0, 14)
        ));

        searchRow.add(searchField);

        JPanel buttonRow = new JPanel(new BorderLayout());
        buttonRow.setOpaque(false);
        buttonRow.setBorder(new EmptyBorder(14, 0, 0, 0));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftButtons.setOpaque(false);
        leftButtons.add(myRecordButton);
        leftButtons.add(viewAllButton);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightButtons.setOpaque(false);
        rightButtons.add(generateButton);
        rightButtons.add(viewButton);
        rightButtons.add(refreshButton);

        buttonRow.add(leftButtons, BorderLayout.WEST);
        buttonRow.add(rightButtons, BorderLayout.EAST);

        container.add(searchRow);
        container.add(buttonRow);

        return container;
    }

    private JComponent buildTableSection() {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 2, 0, 2));

        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(120, 120, 120));
        footer.add(infoLabel, BorderLayout.WEST);

        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(footer, BorderLayout.SOUTH);

        return wrapper;
    }

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setGridColor(GRID);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setBackground(WHITE);
        table.setForeground(BLACK);
        table.setSelectionBackground(new Color(235, 235, 235));
        table.setSelectionForeground(BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(BLACK);
        header.setForeground(WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(WHITE);
        button.setBackground(BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 44));
        return button;
    }

    private void wireEvents() {
        myRecordButton.addActionListener(e -> loadMyRecord());
        viewAllButton.addActionListener(e -> loadAllPermittedRecords());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadEmployees();
            loadInitialView();
            showListCard();
        });

        searchField.addActionListener(e -> performSearch());
        viewButton.addActionListener(e -> viewSelectedRecord());
        generateButton.addActionListener(e -> generateSelectedPayroll());
    }

    private void applyPermissions() {
        searchField.setVisible(canViewAllPayroll());
        viewAllButton.setVisible(canViewAllPayroll());

        myRecordButton.setVisible(canViewOwnPayroll());
        viewButton.setVisible(canViewAnyPayslip());
        generateButton.setVisible(canProcessPayroll());

        refreshButton.setVisible(true);
    }

    private void loadEmployees() {
        try {
            allEmployees = employeeRepository.loadAll();
        } catch (Exception ex) {
            allEmployees = new ArrayList<>();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load employee records:\n" + ex.getMessage(),
                    "Payroll",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadInitialView() {
        if (!canAccessPayrollModule()) {
            tableModel.setRowCount(0);
            displayedEmployees = new ArrayList<>();
            infoLabel.setText("You do not have permission to access payroll.");
            return;
        }

        if (canViewAllPayroll()) {
            loadAllPermittedRecords();
        } else {
            loadMyRecord();
        }
    }

    private void loadMyRecord() {
        List<Employee> result = new ArrayList<>();

        if (currentUser != null) {
            for (Employee employee : allEmployees) {
                if (safe(employee.getId()).equalsIgnoreCase(safe(currentUser.getId()))) {
                    result.add(employee);
                    break;
                }
            }
        }

        displayedEmployees = result;
        refreshTable(result);
        infoLabel.setText(result.size() + " payroll record(s) loaded.");
    }

    private void loadAllPermittedRecords() {
        if (!canViewAllPayroll()) {
            loadMyRecord();
            return;
        }

        displayedEmployees = new ArrayList<>(allEmployees);
        refreshTable(displayedEmployees);
        infoLabel.setText(displayedEmployees.size() + " payroll record(s) loaded.");
    }

    private void performSearch() {
        if (!canViewAllPayroll()) {
            return;
        }

        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadAllPermittedRecords();
            return;
        }

        List<Employee> filtered = new ArrayList<>();

        for (Employee employee : allEmployees) {
            String employeeId = safe(employee.getId()).toLowerCase();
            String firstName = safe(employee.getFirstName()).toLowerCase();
            String lastName = safe(employee.getLastName()).toLowerCase();
            String fullName = (firstName + " " + lastName).trim();

            if (employeeId.contains(keyword)
                    || firstName.contains(keyword)
                    || lastName.contains(keyword)
                    || fullName.contains(keyword)) {
                filtered.add(employee);
            }
        }

        displayedEmployees = filtered;
        refreshTable(filtered);
        infoLabel.setText(filtered.size() + " payroll record(s) found.");
    }

    private void refreshTable(List<Employee> employees) {
        tableModel.setRowCount(0);

        for (Employee employee : employees) {
            BigDecimal allowance = employee.getTotalAllowance();
            BigDecimal grossSalary = employee.calculateSalary();

            tableModel.addRow(new Object[]{
                    safe(employee.getId()),
                    (safe(employee.getFirstName()) + " " + safe(employee.getLastName())).trim(),
                    safe(employee.getPosition()),
                    formatCurrency(employee.getBasicSalary()),
                    formatCurrency(allowance),
                    formatCurrency(grossSalary)
            });
        }
    }

    private void viewSelectedRecord() {
        Employee employee = getSelectedEmployee();
        if (employee == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a payroll record first.",
                    "Payroll",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!canViewEmployeePayroll(employee)) {
            JOptionPane.showMessageDialog(
                    this,
                    "You do not have permission to view this payslip.",
                    "Payroll",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        showDetailCard(employee, false);
    }

    private void generateSelectedPayroll() {
        if (!canProcessPayroll()) {
            JOptionPane.showMessageDialog(
                    this,
                    "You do not have permission to process payroll.",
                    "Payroll",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Employee employee = getSelectedEmployee();
        if (employee == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a payroll record first.",
                    "Payroll",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        showDetailCard(employee, true);
    }

    private Employee getSelectedEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= displayedEmployees.size()) {
            return null;
        }
        return displayedEmployees.get(selectedRow);
    }

    private void showListCard() {
        innerCardLayout.show(innerContentPanel, LIST_CARD);
    }

    private void showDetailCard(Employee employee, boolean generateMode) {
        PayrollDetailsPanel.Mode mode = generateMode
                ? PayrollDetailsPanel.Mode.GENERATE
                : PayrollDetailsPanel.Mode.VIEW;

        PayrollDetailsPanel panel = new PayrollDetailsPanel(mode);
        panel.setBackAction(this::showListCard);

        if (generateMode) {
            panel.setMonthStart(YearMonth.of(2025, 1));
        } else {
            panel.setMonthStart(YearMonth.of(2024, 6));
        }

        if (generateMode) {
            refreshGeneratedPayslip(panel, employee);

            panel.getMonthComboBox().addActionListener(e -> refreshGeneratedPayslip(panel, employee));
            panel.addHoursWorkedChangeListener(() -> refreshGeneratedPayslip(panel, employee));

            panel.setSubmitAction(() -> {
                refreshGeneratedPayslip(panel, employee);

                JOptionPane.showMessageDialog(
                        this,
                        "Payroll generated successfully.",
                        "Payroll",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });
        } else {
            refreshViewedPayslip(panel, employee);
            panel.getMonthComboBox().addActionListener(e -> refreshViewedPayslip(panel, employee));
        }

        detailCardHolder.removeAll();
        detailCardHolder.add(panel, BorderLayout.CENTER);
        detailCardHolder.revalidate();
        detailCardHolder.repaint();

        innerCardLayout.show(innerContentPanel, DETAIL_CARD);
    }

    private boolean canAccessPayrollModule() {
        return canViewAllPayroll()
                || canViewOwnPayroll()
                || canViewAnyPayslip()
                || canProcessPayroll();
    }

    private boolean canViewAllPayroll() {
        return hasPermission(Permission.VIEW_PAYROLL);
    }

    private boolean canProcessPayroll() {
        return hasPermission(Permission.PROCESS_PAYROLL);
    }

    private boolean canViewOwnPayroll() {
        return hasPermission(Permission.VIEW_OWN_PAYROLL)
                || hasPermission(Permission.VIEW_OWN_PAYSLIP);
    }

    private boolean canViewAnyPayslip() {
        return hasPermission(Permission.VIEW_PAYSLIP)
                || hasPermission(Permission.VIEW_OWN_PAYSLIP)
                || hasPermission(Permission.VIEW_PAYROLL)
                || hasPermission(Permission.PROCESS_PAYROLL);
    }

    private boolean canViewEmployeePayroll(Employee employee) {
        if (employee == null) {
            return false;
        }

        if (canViewAllPayroll() || hasPermission(Permission.VIEW_PAYSLIP) || canProcessPayroll()) {
            return true;
        }

        return currentUser != null
                && safe(employee.getId()).equalsIgnoreCase(safe(currentUser.getId()))
                && canViewOwnPayroll();
    }

    private boolean hasPermission(Permission permission) {
        return AuthorizationService.hasPermission(currentUser, permission);
    }

    private String formatCurrency(BigDecimal value) {
        BigDecimal safeValue = value == null ? BigDecimal.ZERO : value;
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format.format(safeValue);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private YearMonth parseYearMonth(String text) {
        try {
            return YearMonth.parse(text, DateTimeFormatter.ofPattern("MMMM yyyy"));
        } catch (Exception ex) {
            return YearMonth.now();
        }
    }

    private void refreshViewedPayslip(PayrollDetailsPanel panel, Employee employee) {
        YearMonth month = parseYearMonth(panel.getSelectedMonthText());

        List<AttendanceRecord> attendanceRecords =
                attendanceRepository.findByEmployeeId(employee.getId());

        PayrollComputationService.PayslipResult result =
                payrollService.computePayslipFromAttendance(employee, attendanceRecords, month);

        panel.displayPayslip(result);
    }

    private void refreshGeneratedPayslip(PayrollDetailsPanel panel, Employee employee) {
        YearMonth month = parseYearMonth(panel.getSelectedMonthText());

        String input = panel.getHoursWorkedInput();
        BigDecimal hoursWorked;

        if (input == null || input.isBlank()) {
            hoursWorked = BigDecimal.ZERO;
        } else {
            try {
                hoursWorked = new BigDecimal(input.trim());
            } catch (NumberFormatException ex) {
                return;
            }
        }

        PayrollComputationService.PayslipResult result =
                payrollService.computePayslipFromHoursWorked(employee, hoursWorked, month);

        panel.displayPayslip(result);
    }
}