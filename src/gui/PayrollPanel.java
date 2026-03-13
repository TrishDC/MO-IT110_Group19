/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */

package gui;

import RBAC.Permission;
import model.Employee;
import model.RegularEmployee;
import pay.SalaryCalculator;
import repository.EmployeeRepository;
import service.AuthorizationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;


public class PayrollPanel extends JPanel {

    private static final Color TABLE_HEADER_BG = new Color(245, 245, 245);
    private static final Color TABLE_BORDER = new Color(220, 220, 220);
    private static final Color SELECT_BG = new Color(225, 235, 255);
    private static final Color ALT_ROW_BG = new Color(250, 250, 250);

    private static final int COL_ID = 0;
    private static final int COL_BASIC_SALARY = 1;
    private static final int COL_HOURLY_RATE = 2;
    private static final int COL_ALLOWANCES = 3;
    private static final int COL_DEDUCTIONS = 4;
    private static final int COL_NET_PAY = 5;

    private final EmployeeRepository repo;
    private final Employee currentUser;
    private final JTable table;
    private final DefaultTableModel model;
    private final boolean canViewPayroll;

    private JButton viewSalaryBtn;

    public PayrollPanel(EmployeeRepository repo, Path employeeCsvPath, Employee currentUser) {
        this.repo = repo;
        this.currentUser = currentUser;
        this.canViewPayroll = currentUser != null
                && AuthorizationService.hasPermission(currentUser, Permission.VIEW_PAYROLL);

        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columns = {
                "ID",
                "Basic Salary",
                "Hourly Rate",
                "Allowances",
                "Deductions",
                "Net Pay"
        };

        this.model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.table = createPayrollTable();

        if (!shouldShowAllowancesColumn()) {
            safeHideColumnByIndex(COL_ALLOWANCES);
        }

        add(createContentArea(), BorderLayout.CENTER);
        loadTable();
    }

    private boolean shouldShowAllowancesColumn() {
        return currentUser instanceof RegularEmployee;
    }

    /**
     * Finds a column by its model index and removes it from the visible table.
     * The data stays in the table model but is hidden from the UI.
     */
    private void safeHideColumnByIndex(int modelIndex) {
        try {
            for (int viewIndex = 0; viewIndex < table.getColumnCount(); viewIndex++) {
                if (table.convertColumnIndexToModel(viewIndex) == modelIndex) {
                    table.removeColumn(table.getColumnModel().getColumn(viewIndex));
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Column hide error: " + e.getMessage());
        }
    }

    private JPanel createContentArea() {
        JPanel container = new JPanel(new BorderLayout(0, 14));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TABLE_BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JLabel title = new JLabel("Payroll Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        toolbar.add(title, BorderLayout.WEST);

        viewSalaryBtn = new JButton("View Salary Record");
        viewSalaryBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewSalaryBtn.setBackground(Color.BLACK);
        viewSalaryBtn.setForeground(Color.WHITE);
        viewSalaryBtn.setOpaque(true);
        viewSalaryBtn.setBorderPainted(false);
        viewSalaryBtn.setFocusPainted(false);
        viewSalaryBtn.setPreferredSize(new Dimension(180, 40));
        viewSalaryBtn.setVisible(canViewPayroll);
        viewSalaryBtn.setEnabled(false);
        viewSalaryBtn.addActionListener(e -> openSalaryDialog());

        toolbar.add(viewSalaryBtn, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        container.add(toolbar, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JTable createPayrollTable() {
        JTable payrollTable = new JTable(model);
        payrollTable.setRowHeight(36);
        payrollTable.setShowGrid(false);
        payrollTable.setIntercellSpacing(new Dimension(0, 0));
        payrollTable.setSelectionBackground(SELECT_BG);
        payrollTable.setSelectionForeground(Color.BLACK);
        payrollTable.setFillsViewportHeight(true);

        JTableHeader header = payrollTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );
                label.setOpaque(true);
                label.setBackground(TABLE_HEADER_BG);
                label.setForeground(Color.BLACK);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBorder(new EmptyBorder(0, 12, 0, 12));
                label.setHorizontalAlignment(column == COL_ID ? CENTER : RIGHT);
                return label;
            }
        });

        payrollTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBorder(new EmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(column == COL_ID ? CENTER : RIGHT);
                setBackground(isSelected ? SELECT_BG : (row % 2 == 0 ? Color.WHITE : ALT_ROW_BG));
                setForeground(Color.BLACK);

                return this;
            }
        });

        payrollTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && viewSalaryBtn != null) {
                viewSalaryBtn.setEnabled(canViewPayroll && payrollTable.getSelectedRow() >= 0);
            }
        });

        return payrollTable;
    }

    private void loadTable() {
        model.setRowCount(0);

        if (repo == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Employee repository is not available.",
                    "Payroll Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            List<Employee> employees = repo.loadAll();

            for (Employee employee : employees) {
                if (employee == null) {
                    continue;
                }

                BigDecimal grossPay = SalaryCalculator.computeMonthlyPay(employee, YearMonth.now());
                BigDecimal allowances = computeAllowances(employee);

                BigDecimal sss = SalaryCalculator.computeSssDeduction(grossPay);
                BigDecimal philHealth = SalaryCalculator.computePhilHealthDeduction(grossPay);
                BigDecimal pagIbig = SalaryCalculator.computePagIbigDeduction(grossPay);
                BigDecimal tax = SalaryCalculator.computeWithholdingTax(grossPay, sss, philHealth, pagIbig);

                BigDecimal totalDeductions = sss.add(philHealth).add(pagIbig).add(tax);
                BigDecimal netPay = grossPay.subtract(totalDeductions);

                model.addRow(new Object[]{
                        safeScale(employee.getId()),
                        safeScale(employee.getBasicSalary()),
                        safeScale(employee.getHourlyRate()),
                        safeScale(allowances),
                        safeScale(totalDeductions),
                        safeScale(netPay)
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load payroll records.\n" + e.getMessage(),
                    "Payroll Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private BigDecimal computeAllowances(Employee employee) {
        if (!(employee instanceof RegularEmployee)) {
            return BigDecimal.ZERO;
        }

        BigDecimal riceSubsidy = defaultIfNull(employee.getRiceSubsidy());
        BigDecimal phoneAllowance = defaultIfNull(employee.getPhoneAllowance());
        BigDecimal clothingAllowance = defaultIfNull(employee.getClothingAllowance());

        return riceSubsidy.add(phoneAllowance).add(clothingAllowance);
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Object safeScale(Object value) {
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.setScale(2, RoundingMode.HALF_UP);
        }
        return value;
    }

    private void openSalaryDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || repo == null) {
            return;
        }

        String employeeId = String.valueOf(model.getValueAt(selectedRow, COL_ID));

        try {
            for (Employee employee : repo.loadAll()) {
                if (employee != null && employeeId.equals(employee.getId())) {
                    Window owner = SwingUtilities.getWindowAncestor(this);
                    Frame frameOwner = owner instanceof Frame ? (Frame) owner : null;

                    new ViewSalaryRecordDialog(frameOwner, employee).setVisible(true);
                    return;
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Selected employee record was not found.",
                    "Payroll",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to open salary record.\n" + e.getMessage(),
                    "Payroll Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}