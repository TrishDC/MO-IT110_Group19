package gui;

import javax.swing.*;
import java.awt.*;
import model.Leave;
import service.LeaveService;

public class AddLeaveDialog extends JDialog {

    private final LeaveService service;

    private JTextField txtLeaveId = new JTextField();
    private JTextField txtEmpId = new JTextField();
    private JComboBox<String> cboType = new JComboBox<>(new String[]{"Sick", "Vacation", "Emergency", "Other"});
    private JTextField txtStart = new JTextField("2026-02-18");
    private JTextField txtEnd = new JTextField("2026-02-18");
    private JComboBox<String> cboStatus = new JComboBox<>(new String[]{"Pending", "Approved", "Rejected"});

    public AddLeaveDialog(Frame owner, LeaveService service) {
        super(owner, "Add Leave", true);
        this.service = service;

        setSize(420, 320);
        setLocationRelativeTo(owner);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(7, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        p.add(new JLabel("Leave ID:")); p.add(txtLeaveId);
        p.add(new JLabel("Employee ID:")); p.add(txtEmpId);
        p.add(new JLabel("Type:")); p.add(cboType);
        p.add(new JLabel("Start Date (yyyy-mm-dd):")); p.add(txtStart);
        p.add(new JLabel("End Date (yyyy-mm-dd):")); p.add(txtEnd);
        p.add(new JLabel("Status:")); p.add(cboStatus);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        p.add(btnSave); p.add(btnCancel);

        add(p);
    }

    private void save() {
        try {
            int leaveId = Integer.parseInt(txtLeaveId.getText().trim());
            int empId = Integer.parseInt(txtEmpId.getText().trim());
            String type = cboType.getSelectedItem().toString();
            String start = txtStart.getText().trim();
            String end = txtEnd.getText().trim();
            String status = cboStatus.getSelectedItem().toString();
            if (status == null || status.trim().isEmpty()) {
                status = "Pending";
            }

            service.add(new Leave(leaveId, empId, type, start, end, status));
            JOptionPane.showMessageDialog(this, "Leave added!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
