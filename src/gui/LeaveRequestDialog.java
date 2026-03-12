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
import service.LeaveRequestValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LeaveRequestDialog extends JDialog {

    private final JTextField txtStartDate = new JTextField(20);
    private final JTextField txtEndDate = new JTextField(20);
    private final JComboBox<String> cmbReason = new JComboBox<>(
            new String[]{"Vacation Leave", "Sick Leave", "Emergency Leave", "Personal Leave", "Other"}
    );
    private final JTextArea txtNotes = new JTextArea(4, 20);

    private boolean saved;
    private final EmployeeLeaveRequest request;

    public LeaveRequestDialog(Frame owner, EmployeeLeaveRequest request, boolean isEditMode) {
        super(owner, isEditMode ? "Update Leave Request" : "File Leave Request", true);
        this.request = request;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(460, 340);
        setLocationRelativeTo(owner);
        setResizable(true);

        initUi();
        bindData();
    }

    private void initUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblStart = createLabel("Start Date (yyyy-MM-dd)");
        JLabel lblEnd = createLabel("End Date (yyyy-MM-dd)");
        JLabel lblReason = createLabel("Reason");
        JLabel lblNotes = createLabel("Notes");

        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(lblStart, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(txtStartDate, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(lblEnd, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(txtEndDate, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(lblReason, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cmbReason, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(lblNotes, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(new JScrollPane(txtNotes), gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(Color.WHITE);

        JButton btnCancel = createButton("Cancel");
        JButton btnSave = createButton("Save");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        actions.add(btnCancel);
        actions.add(btnSave);

        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void bindData() {
        txtStartDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEndDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbReason.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (request.getStartDate() != null) txtStartDate.setText(request.getStartDate());
        if (request.getEndDate() != null) txtEndDate.setText(request.getEndDate());
        if (request.getReason() != null) cmbReason.setSelectedItem(request.getReason());
        if (request.getNotes() != null) txtNotes.setText(request.getNotes());
    }

    private void onSave() {
        request.setStartDate(txtStartDate.getText().trim());
        request.setEndDate(txtEndDate.getText().trim());
        request.setReason((String) cmbReason.getSelectedItem());
        request.setNotes(txtNotes.getText().trim());

        try {
            LeaveRequestValidator.validate(request);
            saved = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        return btn;
    }
}
