/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */

package gui;

import model.EmployeeLeaveRequest;
import service.EmployeeLeaveUiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class EmployeeLeavesPanel extends JPanel {

    private static final Color MUTED = new Color(130, 130, 130);

    private final EmployeeLeaveUiService leaveService;
    private final String currentEmployeeId;
    private final String currentEmployeeName;
    private final String currentDepartment;
    private final String currentPosition;

    private final EmployeeLeaveTableModel tableModel = new EmployeeLeaveTableModel();
    private JTable table;

    public EmployeeLeavesPanel(EmployeeLeaveUiService leaveService,
                               String currentEmployeeId,
                               String currentEmployeeName,
                               String currentDepartment,
                               String currentPosition) {
        this.leaveService = leaveService;
        this.currentEmployeeId = currentEmployeeId;
        this.currentEmployeeName = currentEmployeeName;
        this.currentDepartment = currentDepartment;
        this.currentPosition = currentPosition;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildCenterPanel(), BorderLayout.CENTER);
        loadLeaveRequests();
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnAdd = createActionButton("Add");
        JButton btnUpdate = createActionButton("Update");
        JButton btnDelete = createActionButton("Delete");
        JButton btnRefresh = createActionButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadLeaveRequests());

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnRefresh);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(buildTablePane(), BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane buildTablePane() {
        table = new JTable(tableModel);
        table.setRowHeight(42);
        table.setShowGrid(true);
        table.setGridColor(new Color(210, 210, 210));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 44));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private void loadLeaveRequests() {
        List<EmployeeLeaveRequest> requests = leaveService.getLeavesByEmployee(currentEmployeeId);
        tableModel.setRequests(requests);
    }

    private void onAdd() {
        EmployeeLeaveRequest request = new EmployeeLeaveRequest();
        request.setEmployeeId(currentEmployeeId);
        request.setEmployeeName(currentEmployeeName);
        request.setDepartment(currentDepartment);
        request.setStatus("Pending");

        LeaveRequestDialog dialog = new LeaveRequestDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                request,
                false
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            leaveService.fileLeave(request);
            loadLeaveRequests();
            JOptionPane.showMessageDialog(this, "Leave request filed successfully.");
        }
    }

    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        EmployeeLeaveRequest selected = tableModel.getRequestAt(row);
        if (selected == null) return;

        if (!"Pending".equalsIgnoreCase(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only pending leave requests can be updated.");
            return;
        }

        LeaveRequestDialog dialog = new LeaveRequestDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                selected,
                true
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            leaveService.updateLeave(selected);
            loadLeaveRequests();
            JOptionPane.showMessageDialog(this, "Leave request updated successfully.");
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        EmployeeLeaveRequest selected = tableModel.getRequestAt(row);
        if (selected == null) return;

        if (!"Pending".equalsIgnoreCase(selected.getStatus())) {
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
            leaveService.deleteLeave(selected.getLeaveId(), currentEmployeeId);
            loadLeaveRequests();
            JOptionPane.showMessageDialog(this, "Leave request deleted successfully.");
        }
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 44));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        return button;
    }
}