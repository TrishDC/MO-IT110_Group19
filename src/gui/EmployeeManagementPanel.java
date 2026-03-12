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
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {

    private final EmployeeRepository repo;
    private final Path employeeCsvPath;
    private final Employee currentUser;

    private final JTable table;
    private final DefaultTableModel model;

    private JButton addBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton viewBtn;

    private static final Color TEXT_DARK = new Color(30, 30, 30);
    private static final Color TABLE_HEADER_BG = new Color(245, 245, 245);
    private static final Color TABLE_BORDER = new Color(220, 220, 220);
    private static final Color SELECT_BG = new Color(225, 235, 255);

    public EmployeeManagementPanel(EmployeeRepository repo, Path employeeCsvPath, Employee currentUser) {
        this.repo = repo;
        this.employeeCsvPath = employeeCsvPath;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setOpaque(false);

        this.model = createTableModel();
        this.table = createEmployeeTable();

        add(createContentArea(), BorderLayout.CENTER);
        loadTable();
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
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }

                if (!selected && (col == 4 || col == 6) && v != null) {
                    try {
                        setText(new BigDecimal(v.toString()).toPlainString());
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
        addBtn.addActionListener(e ->
                new AddRecordDialog((Frame) SwingUtilities.getWindowAncestor(this), repo, this::loadTable).setVisible(true)
        );

        updateBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;

            String id = (String) model.getValueAt(r, 0);

            try {
                for (Employee emp : repo.loadAll()) {
                    if (emp.getId().equals(id)) {
                        new UpdateDialog((Frame) SwingUtilities.getWindowAncestor(this), repo, emp, this::loadTable)
                                .setVisible(true);
                        return;
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Cannot edit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;

            if (JOptionPane.showConfirmDialog(
                    this,
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
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;

            String id = (String) model.getValueAt(r, 0);

            try {
                for (Employee emp : repo.loadAll()) {
                    if (emp.getId().equals(id)) {
                        new PayslipSplitDialog((Frame) SwingUtilities.getWindowAncestor(this), emp).setVisible(true);
                        return;
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Cannot open: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadTable() {
        model.setRowCount(0);

        try {
            List<Employee> employees = repo.loadAll();
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
            JOptionPane.showMessageDialog(this, "Load failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}