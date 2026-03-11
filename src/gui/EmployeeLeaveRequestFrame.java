package gui;

import javax.swing.*;
import java.awt.event.*;
import service.LeaveService;

public class EmployeeLeaveRequestFrame extends JFrame {

    private JTextField txtEmployeeId;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextField txtReason;

    public EmployeeLeaveRequestFrame() {

        setTitle("Leave Request");
        setSize(400,300);
        setLayout(null);

        JLabel lblId = new JLabel("Employee ID:");
        lblId.setBounds(30,30,100,25);
        add(lblId);

        txtEmployeeId = new JTextField();
        txtEmployeeId.setBounds(150,30,200,25);
        add(txtEmployeeId);

        JLabel lblStart = new JLabel("Start Date:");
        lblStart.setBounds(30,70,100,25);
        add(lblStart);

        txtStartDate = new JTextField();
        txtStartDate.setBounds(150,70,200,25);
        add(txtStartDate);

        JLabel lblEnd = new JLabel("End Date:");
        lblEnd.setBounds(30,110,100,25);
        add(lblEnd);

        txtEndDate = new JTextField();
        txtEndDate.setBounds(150,110,200,25);
        add(txtEndDate);

        JLabel lblReason = new JLabel("Reason:");
        lblReason.setBounds(30,150,100,25);
        add(lblReason);

        txtReason = new JTextField();
        txtReason.setBounds(150,150,200,25);
        add(txtReason);

        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setBounds(150,200,100,30);
        add(btnSubmit);

        btnSubmit.addActionListener(e -> submitLeave());
    }

    private void submitLeave() {
        LeaveService service = new LeaveService();

        service.requestLeave(
            txtEmployeeId.getText(),
            txtStartDate.getText(),
            txtEndDate.getText(),
            txtReason.getText()
        );

        JOptionPane.showMessageDialog(this, "Leave Request Submitted!");
    }
}
