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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LeaveRequestDialog extends JDialog {

    private final JTextField txtStartDate = new JTextField(20);
    private final JTextField txtEndDate = new JTextField(20);
    private final JComboBox<String> cmbType = new JComboBox<>(
            new String[]{"Vacation Leave", "Sick Leave", "Emergency Leave", "Personal Leave", "Other"}
    );
    private final JTextField txtReason = new JTextField(20);
    private final JTextArea txtNotes = new JTextArea(4, 20);

    private boolean saved;
    private final Leave leave;

    public LeaveRequestDialog(Frame owner, Leave leave, boolean isEditMode) {
        super(owner, isEditMode ? "Update Leave Request" : "File Leave Request", true);
        this.leave = leave;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(460, 380);
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

        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        addField(form, gbc, 0, "Leave Type", cmbType);
        addField(form, gbc, 1, "Start Date (yyyy-MM-dd)", txtStartDate);
        addField(form, gbc, 2, "End Date (yyyy-MM-dd)", txtEndDate);
        addField(form, gbc, 3, "Reason", txtReason);

        JLabel lblNotes = new JLabel("Notes");
        lblNotes.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(lblNotes, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(new JScrollPane(txtNotes), gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Cancel");
        JButton btnSave = new JButton("Save");

        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSave.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        actions.add(btnCancel);
        actions.add(btnSave);

        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(field, gbc);
    }

    private void bindData() {
        txtStartDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEndDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtReason.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (leave.getLeaveType() != null) cmbType.setSelectedItem(leave.getLeaveType());
        if (leave.getStartDate() != null) txtStartDate.setText(leave.getStartDate());
        if (leave.getEndDate() != null) txtEndDate.setText(leave.getEndDate());
        if (leave.getNotes() != null) txtNotes.setText(leave.getNotes());
    }

    private void onSave() {
        leave.setLeaveType((String) cmbType.getSelectedItem());
        leave.setStartDate(txtStartDate.getText().trim());
        leave.setEndDate(txtEndDate.getText().trim());
        leave.setNotes(txtNotes.getText().trim());

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}