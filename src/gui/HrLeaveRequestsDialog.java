package gui;

import model.Leave;
import service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HrLeaveRequestsDialog extends JDialog {

    private final LeaveService service;

    private JTable table;
    private DefaultTableModel model;

    private JButton btnApprove;
    private JButton btnReject;
    private JButton btnRefresh;
    private JButton btnClose;

    public HrLeaveRequestsDialog(Frame owner, LeaveService service) {
        super(owner, "HR Leave Requests", true);
        this.service = service;

        initializeDialog();
        initializeComponents();
        loadPendingRequests();
    }

    private void initializeDialog() {
        setSize(980, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
    }

    private void initializeComponents() {
        model = new DefaultTableModel(
                new Object[]{
                        "Leave ID", "Employee ID", "Type",
                        "Start Date", "End Date", "Notes", "Status"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // 7 columns only
        int[] widths = {80, 100, 130, 110, 110, 220, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        btnApprove = new JButton("Approve");
        btnReject = new JButton("Reject");
        btnRefresh = new JButton("Refresh");
        btnClose = new JButton("Close");

        btnApprove.addActionListener(e -> approveSelected());
        btnReject.addActionListener(e -> rejectSelected());
        btnRefresh.addActionListener(e -> loadPendingRequests());
        btnClose.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.add(btnApprove);
        bottomPanel.add(btnReject);
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnClose);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadPendingRequests() {
        model.setRowCount(0);

        List<Leave> requests = service.getPendingRequests();

        for (Leave leave : requests) {
            model.addRow(new Object[]{
                    leave.getLeaveId(),
                    leave.getEmployeeId(),
                    leave.getLeaveType(),
                    leave.getStartDate(),
                    leave.getEndDate(),
                    leave.getNotes(),
                    leave.getStatus()
            });
        }
    }

    private void approveSelected() {
        Leave selected = getSelectedLeave();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a pending request first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Approve Leave ID " + selected.getLeaveId() + "?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.approveLeave(selected.getLeaveId());
                loadPendingRequests();
                JOptionPane.showMessageDialog(this, "Leave request approved.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejectSelected() {
        Leave selected = getSelectedLeave();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a pending request first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reject Leave ID " + selected.getLeaveId() + "?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.rejectLeave(selected.getLeaveId());
                loadPendingRequests();
                JOptionPane.showMessageDialog(this, "Leave request rejected.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Leave getSelectedLeave() {
        int row = table.getSelectedRow();
        if (row == -1) return null;

        int leaveId = Integer.parseInt(model.getValueAt(row, 0).toString());
        return service.findById(leaveId);
    }
}