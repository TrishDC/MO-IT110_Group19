/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */

package gui;

import model.Leave;
import service.LeaveService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class EmployeeLeavesPanel extends JPanel {

    private static final String LIST_CARD = "LIST";
    private static final String FORM_CARD = "FORM";

    private final LeaveService leaveService;
    private final String currentEmployeeId;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private JTable table;
    private DefaultTableModel model;
    private LeaveFormPanel formPanel;

    private Leave workingLeave;
    private boolean editMode = false;

    public EmployeeLeavesPanel(LeaveService leaveService,
                               String currentEmployeeId,
                               String currentEmployeeName,
                               String currentDepartment,
                               String currentPosition) {
        this.leaveService = leaveService;
        this.currentEmployeeId = currentEmployeeId;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        contentPanel.setOpaque(false);
        contentPanel.add(buildListPage(), LIST_CARD);
        contentPanel.add(buildFormPage(), FORM_CARD);

        add(contentPanel, BorderLayout.CENTER);

        loadLeaveRequests();
        showListPage();
    }

    private JPanel buildListPage() {
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnAdd = createActionButton("Add");
        JButton btnUpdate = createActionButton("Update");
        JButton btnDelete = createActionButton("Delete");
        JButton btnRefresh = createActionButton("Refresh");

        btnAdd.addActionListener(e -> openAddForm());
        btnUpdate.addActionListener(e -> openUpdateForm());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadLeaveRequests());

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnRefresh);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(0, 0, 0, 0)
        ));

        model = new DefaultTableModel(
                new Object[]{"Leave ID", "Leave Type", "Start Date", "End Date", "Notes", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(42);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(225, 225, 225));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(25, 25, 25));
        table.setSelectionBackground(new Color(232, 239, 252));
        table.setSelectionForeground(new Color(25, 25, 25));

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 44));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        cellRenderer.setVerticalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(320);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(scrollPane, BorderLayout.CENTER);

        outer.add(actions, BorderLayout.NORTH);
        outer.add(tableCard, BorderLayout.CENTER);

        return outer;
    }

    private JPanel buildFormPage() {
        formPanel = new LeaveFormPanel();
        formPanel.addBackListener(e -> showListPage());
        formPanel.addSubmitListener(e -> submitForm());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private void openAddForm() {
        editMode = false;
        workingLeave = new Leave();
        workingLeave.setEmployeeId(currentEmployeeId);
        workingLeave.setStatus("Pending");

        formPanel.clearForm();
        formPanel.setFormMode(false);
        formPanel.setLeaveData(workingLeave);

        cardLayout.show(contentPanel, FORM_CARD);
    }

    private void openUpdateForm() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        int leaveId = Integer.parseInt(model.getValueAt(row, 0).toString());
        Leave selected = findSelectedLeave(leaveId);

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Selected leave request was not found.");
            return;
        }

        if (!"Pending".equalsIgnoreCase(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only pending leave requests can be updated.");
            return;
        }

        editMode = true;
        workingLeave = selected;

        formPanel.setFormMode(true);
        formPanel.setLeaveData(workingLeave);

        cardLayout.show(contentPanel, FORM_CARD);
    }

    private void submitForm() {
        if (workingLeave == null) {
            return;
        }

        formPanel.fillLeave(workingLeave);

        try {
            if (editMode) {
                leaveService.updateOwnPendingLeave(workingLeave, currentEmployeeId);
                JOptionPane.showMessageDialog(this, "Leave request updated successfully.");
            } else {
                leaveService.requestLeave(workingLeave);
                JOptionPane.showMessageDialog(this, "Leave request filed successfully.");
            }

            workingLeave = null;
            editMode = false;
            showListPage();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showListPage() {
        loadLeaveRequests();
        cardLayout.show(contentPanel, LIST_CARD);
    }

    private void loadLeaveRequests() {
        if (model == null) {
            return;
        }

        model.setRowCount(0);

        List<Leave> leaves = leaveService.getByEmployeeId(currentEmployeeId);
        if (leaves.isEmpty()) {
            return;
        }

        for (Leave leave : leaves) {
            model.addRow(new Object[]{
                    leave.getLeaveId(),
                    leave.getLeaveType(),
                    leave.getStartDate(),
                    leave.getEndDate(),
                    leave.getNotes(),
                    leave.getStatus()
            });
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        int leaveId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String status = model.getValueAt(row, 5).toString();

        if (!"Pending".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only pending leave requests can be deleted.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this leave request?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                leaveService.deleteOwnPendingLeave(leaveId, currentEmployeeId);
                loadLeaveRequests();
                JOptionPane.showMessageDialog(this, "Leave request deleted successfully.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Leave findSelectedLeave(int leaveId) {
        List<Leave> leaves = leaveService.getByEmployeeId(currentEmployeeId);
        for (Leave leave : leaves) {
            if (leave.getLeaveId() == leaveId) {
                return leave;
            }
        }
        return null;
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
}