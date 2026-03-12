package gui;

import RBAC.Permission;
import model.Employee;
import model.RegularEmployee;
import repository.EmployeeRepository;
import pay.SalaryCalculator;
import service.AuthorizationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;

public class PayrollPanel extends JPanel {

    private final EmployeeRepository repo;
    private final Employee currentUser;
    private final JTable table;
    private final DefaultTableModel model;
    private JButton viewSalaryBtn;

    
    private static final Color TABLE_HEADER_BG = new Color(245, 245, 245);
    private static final Color TABLE_BORDER = new Color(220, 220, 220);
    private static final Color SELECT_BG = new Color(225, 235, 255);

    public PayrollPanel(EmployeeRepository repo, Path employeeCsvPath, Employee currentUser) {
        this.repo = repo;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setOpaque(false);

   
        String[] columns = {"ID", "Basic Salary", "Hourly Rate", "Allowances", "Deductions", "Net Pay",};
        this.model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        this.table = createPayrollTable();

       


        
        if (!(currentUser instanceof RegularEmployee)) {
            safeHideColumnByIndex(3);
        }

        add(createContentArea(), BorderLayout.CENTER);
        loadTable();
    }

    /**
     * Finds a column by its Model Index and removes it from the User View.
     */
    private void safeHideColumnByIndex(int modelIndex) {
        try {
            for (int i = 0; i < table.getColumnCount(); i++) {
                if (table.convertColumnIndexToModel(i) == modelIndex) {
                    table.removeColumn(table.getColumnModel().getColumn(i));
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Column Hide Error: " + e.getMessage());
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

        // Black View Button
        viewSalaryBtn = new JButton("View Salary Record");
        viewSalaryBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewSalaryBtn.setBackground(Color.BLACK);
        viewSalaryBtn.setForeground(Color.WHITE);
        viewSalaryBtn.setOpaque(true);
        viewSalaryBtn.setBorderPainted(false);
        viewSalaryBtn.setPreferredSize(new Dimension(180, 40));

        // Check RBAC Permission
        viewSalaryBtn.setVisible(AuthorizationService.hasPermission(currentUser, Permission.VIEW_PAYROLL));
        viewSalaryBtn.setEnabled(false);
        viewSalaryBtn.addActionListener(e -> openSalaryDialog());

        toolbar.add(viewSalaryBtn, BorderLayout.EAST);
        container.add(toolbar, BorderLayout.NORTH);
        container.add(new JScrollPane(table), BorderLayout.CENTER);

        return container;
    }

    private JTable createPayrollTable() {
        JTable pTable = new JTable(model);
        pTable.setRowHeight(36);
        pTable.setShowGrid(false);
        pTable.setSelectionBackground(SELECT_BG);

  
        JTableHeader header = pTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setBackground(TABLE_HEADER_BG);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setHorizontalAlignment(c == 0 ? CENTER : RIGHT);
                return lbl;
            }
        });


        pTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(c == 0 ? CENTER : RIGHT);
                setBackground(isS ? SELECT_BG : (r % 2 == 0 ? Color.WHITE : new Color(250, 250, 250)));
                return this;
            }
        });

        pTable.getSelectionModel().addListSelectionListener(e -> {
            viewSalaryBtn.setEnabled(pTable.getSelectedRow() >= 0);
        });

        return pTable;
    }

    private void loadTable() {
        model.setRowCount(0);
        try {
            List<Employee> employees = repo.loadAll();
            for (Employee emp : employees) {
                BigDecimal gross = SalaryCalculator.computeMonthlyPay(emp, YearMonth.now());
                
                
                BigDecimal allowances = BigDecimal.ZERO;
                String typeFlag = "PROBATIONARY";

                if (emp instanceof RegularEmployee) {
                    allowances = emp.getRiceSubsidy().add(emp.getPhoneAllowance()).add(emp.getClothingAllowance());
                    typeFlag = "REGULAR";
                }

                BigDecimal sss = SalaryCalculator.computeSssDeduction(gross);
                BigDecimal phil = SalaryCalculator.computePhilHealthDeduction(gross);
                BigDecimal pag = SalaryCalculator.computePagIbigDeduction(gross);
                BigDecimal tax = SalaryCalculator.computeWithholdingTax(gross, sss, phil, pag);
                BigDecimal totalDed = sss.add(phil).add(pag).add(tax);

                model.addRow(new Object[]{
                    emp.getId(),
                    emp.getBasicSalary().setScale(2),
                    emp.getHourlyRate().setScale(2),
                    allowances.setScale(2), // Displays 0.00 for non-Regulars
                    totalDed.setScale(2),
                    gross.subtract(totalDed).setScale(2),
                    typeFlag
                });
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void openSalaryDialog() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        String id = (String) model.getValueAt(r, 0);
        try {
            for (Employee emp : repo.loadAll()) {
                if (emp.getId().equals(id)) {
                    new ViewSalaryRecordDialog((Frame) SwingUtilities.getWindowAncestor(this), emp).setVisible(true);
                    return;
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}