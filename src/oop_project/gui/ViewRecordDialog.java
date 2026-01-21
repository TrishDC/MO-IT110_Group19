package com.motorph.employeeapp.gui;

import com.motorph.employeeapp.model.Employee;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ViewRecordDialog extends JDialog {
    public ViewRecordDialog(Frame owner, Employee employee) {
        super(owner, "View Employee Record", true);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;

        // make the right column expand
        c.weightx = 1.0;
        c.fill    = GridBagConstraints.HORIZONTAL;

        String[] labels = {
            "Employee #:", "Last Name:", "First Name:", "Birthday:", "Address:", "Phone:", "SSS #:",
            "PhilHealth #:", "TIN #:", "Pag-IBIG #:", "Status:", "Position:", "Supervisor:",
            "Basic Salary:", "Rice Subsidy:", "Phone Allowance:", "Clothing Allowance:",
            "Semi-monthly Rate:", "Hourly Rate:"
        };
        String[] values = {
            employee.getId(),
            employee.getLastName(),
            employee.getFirstName(),
            employee.getBirthDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
            employee.getAddress(),
            employee.getPhone(),
            employee.getSssNumber(),
            employee.getPhilHealthNumber(),
            employee.getTinNumber(),
            employee.getPagIbigNumber(),
            employee.getStatus(),
            employee.getPosition(),
            employee.getSupervisor(),
            employee.getBasicSalary().toPlainString(),
            employee.getRiceSubsidy().toPlainString(),
            employee.getPhoneAllowance().toPlainString(),
            employee.getClothingAllowance().toPlainString(),
            employee.getGrossSemiMonthlyRate().toPlainString(),
            employee.getHourlyRate().toPlainString()
        };

        for (int i = 0; i < labels.length; i++) {
            // label
            c.gridx = 0; 
            c.gridy = i;
            c.weightx = 0;
            c.fill    = GridBagConstraints.NONE;
            form.add(new JLabel(labels[i]), c);

            // field
            c.gridx = 1;
            c.weightx = 1.0;
            c.fill    = GridBagConstraints.HORIZONTAL;

            if ("Address:".equals(labels[i])) {
                // multi-line address
                JTextArea area = new JTextArea(values[i]);
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setEditable(false);
                area.setBackground(UIManager.getColor("TextField.background"));
                area.setBorder(UIManager.getBorder("TextField.border"));
                area.setRows(3);
                JScrollPane sp = new JScrollPane(area);
                sp.setPreferredSize(new Dimension(300, area.getPreferredSize().height * 2));
                form.add(sp, c);
            } else {
                JTextField field = new JTextField(values[i], 30);
                field.setEditable(false);
                field.setBorder(null);
                form.add(field, c);
            }
        }

        // Close button
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(_ -> dispose());
        buttons.add(closeBtn);

        add(new JScrollPane(form), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }
}
