package gui;

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
import java.util.Set;
import java.util.TreeSet;
import model.Employee;
import repository.EmployeeRepository;

public class AddRecordDialog extends JDialog {
    private final EmployeeRepository repo;
    private final Runnable onSave;

    // Standard Fields
    private final JTextField idField                = new JTextField(6);
    private final JTextField lastNameField          = new JTextField(15);
    private final JTextField firstNameField         = new JTextField(15);
    private final JSpinner birthdaySpinner          = new JSpinner(new SpinnerDateModel());
    private final JTextField addressField           = new JTextField(20);
    private final JTextField phoneField             = new JTextField(12);
    private final JTextField sssField               = new JTextField(12);
    private final JTextField philHealthField        = new JTextField(12);
    private final JTextField tinField               = new JTextField(12);
    private final JTextField pagIbigField           = new JTextField(12);
    
    // Status: Strictly these two options
    private final JComboBox<String> statusCB        = new JComboBox<>(new String[]{"Regular", "Probationary"});
    
    // Position & Supervisor: Dynamic from CSV + Editable for new entries
    private final JComboBox<String> positionCB      = new JComboBox<>();
    private final JComboBox<String> supervisorCB    = new JComboBox<>();

    // Salary Fields
    private final JTextField basicSalaryField       = new JTextField(10);
    private final JTextField riceSubsidyField       = new JTextField(10);
    private final JTextField phoneAllowanceField    = new JTextField(10);
    private final JTextField clothingAllowanceField = new JTextField(10);
    private final JTextField semiMonthlyRateField   = new JTextField(10);
    private final JTextField hourlyRateField        = new JTextField(10);

    public AddRecordDialog(Frame owner, EmployeeRepository repo, Runnable onSave) {
        super(owner, "Add New Employee", true);
        this.repo   = repo;
        this.onSave = onSave;

        JSpinner.DateEditor de = new JSpinner.DateEditor(birthdaySpinner, "M/d/yyyy");
        birthdaySpinner.setEditor(de);

        // Make these editable so you can type new values not yet in the CSV
        positionCB.setEditable(true);
        supervisorCB.setEditable(true);

        // Initialize dynamic lists
        populateDynamicCombos();
        
        buildForm();
        pack();
        setLocationRelativeTo(owner);

        idField.setText(nextId());
        idField.setEditable(false);
    }

    private void populateDynamicCombos() {
        supervisorCB.addItem("N/A");
        
        try {
            List<Employee> all = repo.loadAll();
            Set<String> positions = new TreeSet<>();
            Set<String> supervisors = new TreeSet<>();
            
            for (Employee e : all) {
                if (e.getPosition() != null && !e.getPosition().isBlank()) 
                    positions.add(e.getPosition().trim());
                
                if (e.getSupervisor() != null && !e.getSupervisor().isBlank() && !e.getSupervisor().equals("N/A")) 
                    supervisors.add(e.getSupervisor().trim());
            }
            
            positions.forEach(positionCB::addItem);
            supervisors.forEach(supervisorCB::addItem);
            
        } catch (IOException ex) {
            System.err.println("Error loading dynamic lists: " + ex.getMessage());
        }
    }

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
            tinField, pagIbigField, statusCB, positionCB,
            supervisorCB, basicSalaryField, riceSubsidyField,
            phoneAllowanceField, clothingAllowanceField,
            semiMonthlyRateField, hourlyRateField
        };

        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0; c.gridy = i;
            form.add(new JLabel(labels[i]), c);
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            form.add(fields[i], c);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn  = new JButton("Save");
        JButton closeBtn = new JButton("Close");
        saveBtn.addActionListener(this::onSave);
        closeBtn.addActionListener(e -> dispose());
        buttons.add(saveBtn);
        buttons.add(closeBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(form), BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void onSave(ActionEvent ev) {
        try {
            if (lastNameField.getText().trim().isEmpty() || firstNameField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Full name is required.");
            }

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
            ) {
                @Override
                public BigDecimal calculateSalary() {
                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                }

                @Override
                public BigDecimal getTotalAllowance() {
                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                }
            };

            e.setAddress(addressField.getText().trim());
            e.setPhone(phoneField.getText().trim());
            e.setSssNumber(sssField.getText().trim());
            e.setPhilHealthNumber(philHealthField.getText().trim());
            e.setTinNumber(tinField.getText().trim());
            e.setPagIbigNumber(pagIbigField.getText().trim());
            
            // Getting values from combo boxes (handles both selection and typed text)
            e.setStatus((String) statusCB.getSelectedItem());
            e.setPosition(((String) positionCB.getSelectedItem()).trim());
            e.setSupervisor(((String) supervisorCB.getSelectedItem()).trim());

            List<Employee> all = repo.loadAll();
            all.add(e);
            repo.saveAll(all);
            
            JOptionPane.showMessageDialog(this, "Employee Record is saved.");
            dispose();
            if (onSave != null) onSave.run();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BigDecimal parseDecimal(String txt) {
        if (txt == null || txt.isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(txt);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }
}