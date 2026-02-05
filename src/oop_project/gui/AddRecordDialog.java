package com.motorph.employeeapp.gui;
import com.motorph.employeeapp.repository.EmployeeRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import oop_project.model.Employee;

/**
 * Dialog to add a new Employee. Uses JSpinner for birthday
 * so you don’t need any external calendar library.
 */
public class AddRecordDialog extends JDialog {
    private final EmployeeRepository repo;
    private final Runnable onSave;

    // Fields
    private final JTextField idField               = new JTextField(6);
    private final JTextField lastNameField         = new JTextField(15);
    private final JTextField firstNameField        = new JTextField(15);
    private final JSpinner birthdaySpinner         = new JSpinner(new SpinnerDateModel());
    private final JTextField addressField          = new JTextField(20);
    private final JTextField phoneField            = new JTextField(12);
    private final JTextField sssField              = new JTextField(12);
    private final JTextField philHealthField       = new JTextField(12);
    private final JTextField tinField              = new JTextField(12);
    private final JTextField pagIbigField          = new JTextField(12);
    private final JTextField statusField           = new JTextField(10);
    private final JTextField positionField         = new JTextField(15);
    private final JTextField supervisorField       = new JTextField(15);
    private final JTextField basicSalaryField      = new JTextField(10);
    private final JTextField riceSubsidyField      = new JTextField(10);
    private final JTextField phoneAllowanceField   = new JTextField(10);
    private final JTextField clothingAllowanceField= new JTextField(10);
    private final JTextField semiMonthlyRateField  = new JTextField(10);
    private final JTextField hourlyRateField       = new JTextField(10);

    public AddRecordDialog(Frame owner, EmployeeRepository repo, Runnable onSave) {
        super(owner, "Add New Employee", true);
        this.repo   = repo;
        this.onSave = onSave;

        // format the spinner like M/d/yyyy
        JSpinner.DateEditor de = new JSpinner.DateEditor(birthdaySpinner, "M/d/yyyy");
        birthdaySpinner.setEditor(de);

        buildForm();
        pack();
        setLocationRelativeTo(owner);

        // auto‐fill next numeric ID
        idField.setText(nextId());
        idField.setEditable(false);
    }

    // find max existing ID and add 1
    private String nextId() {
        try {
            List<Employee> all = repo.loadAll();
            return all.stream()
                .map(Employee::getId)
                .map(id -> {
                    try { return Integer.parseInt(id); }
                    catch (Exception e) { return 0; }
                })
                .max(Comparator.naturalOrder())
                .map(n -> n + 1)
                .orElse(10001)
                .toString();
        } catch (IOException ex) {
            return "10001";
        }
    }

    private void buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.WEST;

        String[] labels = {
            "Employee #:", "Last Name:", "First Name:", "Birthday:",
            "Address:", "Phone:", "SSS #:", "PhilHealth #:", "TIN #:",
            "Pag-IBIG #:", "Status:", "Position:", "Supervisor:",
            "Basic Salary:", "Rice Subsidy:", "Phone Allowance:",
            "Clothing Allowance:", "Semi-monthly Rate:", "Hourly Rate:"
        };
        JComponent[] fields = {
            idField, lastNameField, firstNameField, birthdaySpinner,
            addressField, phoneField, sssField, philHealthField,
            tinField, pagIbigField, statusField, positionField,
            supervisorField, basicSalaryField, riceSubsidyField,
            phoneAllowanceField, clothingAllowanceField,
            semiMonthlyRateField, hourlyRateField
        };

        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0; c.gridy = i;
            form.add(new JLabel(labels[i]), c);
            c.gridx = 1;
            form.add(fields[i], c);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn  = new JButton("Save");
        JButton closeBtn = new JButton("Close");
        saveBtn.addActionListener(this::onSave);
        closeBtn.addActionListener(_ -> dispose());
        buttons.add(saveBtn);
        buttons.add(closeBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(form), BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void onSave(ActionEvent ev) {
        try {
            // basic validation
            if (lastNameField.getText().trim().isEmpty() ||
                firstNameField.getText().trim().isEmpty() ||
                birthdaySpinner.getValue() == null ||
                sssField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Please fill in Last Name, First Name, Birthday, and SSS #.");
            }

            // spinner → LocalDate
            Date dt = (Date) birthdaySpinner.getValue();
            LocalDate bday = Instant.ofEpochMilli(dt.getTime())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

            Employee e = new Employee(
                idField.getText().trim(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                bday,
                parseDecimal(basicSalaryField.getText().trim()),
                parseDecimal(riceSubsidyField.getText().trim()),
                parseDecimal(phoneAllowanceField.getText().trim()),
                parseDecimal(clothingAllowanceField.getText().trim()),
                parseDecimal(semiMonthlyRateField.getText().trim()),
                parseDecimal(hourlyRateField.getText().trim())
            ) {};
            // set the rest
            e.setAddress(         addressField.getText().trim());
            e.setPhone(           phoneField.getText().trim());
            e.setSssNumber(       sssField.getText().trim());
            e.setPhilHealthNumber(philHealthField.getText().trim());
            e.setTinNumber(       tinField.getText().trim());
            e.setPagIbigNumber(   pagIbigField.getText().trim());
            e.setStatus(          statusField.getText().trim());
            e.setPosition(        positionField.getText().trim());
            e.setSupervisor(      supervisorField.getText().trim());

            // save and notify
            List<Employee> all = repo.loadAll();
            all.add(e);
            repo.saveAll(all);
            JOptionPane.showMessageDialog(this, "Employee Record is saved.");
            dispose();
            if (onSave != null) onSave.run();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid input: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BigDecimal parseDecimal(String txt) {
        if (txt.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(txt);
    }
}
