package gui;

import model.Employee;
import model.Leave;
import RBAC.Role;
import repository.CsvLeaveRepository;
import service.LeaveService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaveManagementFrame extends JFrame {

    private final LeaveService service;
    private final Employee loggedInUser;

    private JTable table;
    private DefaultTableModel model;

    private JButton btnRequests;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;

    public LeaveManagementFrame(Employee user) {
        this.loggedInUser = user;
        this.service = new LeaveService(new CsvLeaveRepository());

        initializeFrame();
        initializeComponents();
        configureRoleAccess();
        loadLeaves();
    }

    private void initializeFrame() {
        setTitle("Leave Management");
        setSize(1100, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        int[] widths = {80, 100, 130, 110, 110, 180, 180, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        btnRequests = new JButton("Requests");
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        btnRequests.addActionListener(e -> openRequestsDialog());
        btnAdd.addActionListener(e -> openLeaveForm(null));
        btnUpdate.addActionListener(e -> updateSelectedLeave());
        btnDelete.addActionListener(e -> deleteSelectedLeave());
        btnRefresh.addActionListener(e -> loadLeaves());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(btnRequests);
        topPanel.add(btnAdd);
        topPanel.add(btnUpdate);
        topPanel.add(btnDelete);
        topPanel.add(btnRefresh);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void configureRoleAccess() {

        boolean isHr = false;

        if (loggedInUser != null && loggedInUser.getRole() != null) {
            String roleText = loggedInUser.getRole().toString().trim().toUpperCase();
            isHr = roleText.equals("HR");
        }

        btnRequests.setVisible(isHr);
    }
    private void loadLeaves() {
        model.setRowCount(0);

        List<Leave> leaves = service.getVisibleLeaves(loggedInUser);

        for (Leave leave : leaves) {
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

    private void openRequestsDialog() {
        HrLeaveRequestsDialog dialog = new HrLeaveRequestsDialog(this, service);
        dialog.setVisible(true);
        loadLeaves();
    }

    private void openLeaveForm(Leave existingLeave) {
        JTextField tfEmployeeId = new JTextField();
        JComboBox<String> cbLeaveType = new JComboBox<>(new String[]{
                "Vacation Leave", "Sick Leave", "Emergency Leave", "Maternity Leave", "Paternity Leave", "Other"
        });
        JTextField tfStartDate = new JTextField();
        JTextField tfEndDate = new JTextField();
        JTextArea taNotes = new JTextArea(3, 20);

        taNotes.setLineWrap(true);
        taNotes.setWrapStyleWord(true);

        if (existingLeave != null) {
            tfEmployeeId.setText(String.valueOf(existingLeave.getEmployeeId()));
            tfEmployeeId.setEnabled(service.canManageRequests(loggedInUser));
            cbLeaveType.setSelectedItem(existingLeave.getLeaveType());
            tfStartDate.setText(existingLeave.getStartDate());
            tfEndDate.setText(existingLeave.getEndDate());
            taNotes.setText(existingLeave.getNotes());
        } else {
            if (loggedInUser != null) {
                tfEmployeeId.setText(String.valueOf(loggedInUser.getId()));
                tfEmployeeId.setEnabled(service.canManageRequests(loggedInUser));
            }
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(tfEmployeeId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Leave Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(cbLeaveType, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(tfStartDate, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(tfEndDate, gbc);

        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; panel.add(new JScrollPane(taNotes), gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                existingLeave == null ? "Add Leave" : "Update Leave",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Leave leave = (existingLeave == null) ? new Leave() : existingLeave;

            leave.setEmployeeId(tfEmployeeId.getText().trim());
            leave.setLeaveType(String.valueOf(cbLeaveType.getSelectedItem()));
            leave.setStartDate(tfStartDate.getText().trim());
            leave.setEndDate(tfEndDate.getText().trim());
            leave.setNotes(taNotes.getText().trim());

            if (existingLeave == null) {
                leave.setStatus("Pending");
                service.submitLeave(leave);
                JOptionPane.showMessageDialog(this, "Leave request submitted successfully.");
            } else {
                service.updateLeave(leave);
                JOptionPane.showMessageDialog(this, "Leave record updated successfully.");
            }

            loadLeaves();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Employee ID must be a valid number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedLeave() {
        Leave selected = getSelectedLeave();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a leave record first.");
            return;
        }

        if (!service.canEditOrDelete(loggedInUser, selected)) {
            JOptionPane.showMessageDialog(this, "You do not have permission to update this leave.");
            return;
        }

        openLeaveForm(selected);
    }

    private void deleteSelectedLeave() {
        Leave selected = getSelectedLeave();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a leave record first.");
            return;
        }

        if (!service.canEditOrDelete(loggedInUser, selected)) {
            JOptionPane.showMessageDialog(this, "You do not have permission to delete this leave.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete Leave ID " + selected.getLeaveId() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            service.delete(selected.getLeaveId());
            loadLeaves();
            JOptionPane.showMessageDialog(this, "Leave record deleted successfully.");
        }
    }

    private Leave getSelectedLeave() {
        int row = table.getSelectedRow();
        if (row == -1) return null;

        int leaveId = Integer.parseInt(model.getValueAt(row, 0).toString());
        return service.findById(leaveId);
    }
}