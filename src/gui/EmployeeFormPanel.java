/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

/**
 *
 * @author Rhynne Gracelle
 */

import RBAC.RBACSetup;
import RBAC.Role;
import model.Employee;
import model.RegularEmployee;
import model.ProbationaryEmployee;
import repository.EmployeeRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EmployeeFormPanel extends JPanel {

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    private final EmployeeRepository repo;
    private final Runnable onSaveSuccess;
    private final Runnable onBack;
    private final Map<String, Role> availableRoles = RBACSetup.setupRoles();

    private final JLabel titleLabel = new JLabel();

    private Employee editingEmployee;

    private final JTextField idField = new JTextField(10);
    private final JTextField lastNameField = new JTextField(18);
    private final JTextField firstNameField = new JTextField(18);
    private final JSpinner birthdaySpinner = new JSpinner(new SpinnerDateModel());
    private final JTextField addressField = new JTextField(22);
    private final JTextField phoneField = new JTextField(14);
    private final JTextField sssField = new JTextField(16);
    private final JTextField philHealthField = new JTextField(16);
    private final JTextField tinField = new JTextField(16);
    private final JTextField pagIbigField = new JTextField(16);
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Regular", "Probationary"});
    private final JTextField positionField = new JTextField(18);
    private final JTextField supervisorField = new JTextField(18);
    private final JTextField basicSalaryField = new JTextField(12);
    private final JTextField riceSubsidyField = new JTextField(12);
    private final JTextField phoneAllowanceField = new JTextField(12);
    private final JTextField clothingAllowanceField = new JTextField(12);
    private final JTextField semiMonthlyRateField = new JTextField(12);
    private final JTextField hourlyRateField = new JTextField(12);
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{
            "EXECUTIVE", "HR", "PAYROLL", "ACCOUNTING", "IT", "SALES"
    });

    public EmployeeFormPanel(
            String title,
            EmployeeRepository repo,
            Employee employee,
            Runnable onSaveSuccess,
            Runnable onBack
    ) {
        this.repo = repo;
        this.editingEmployee = employee;
        this.onSaveSuccess = onSaveSuccess;
        this.onBack = onBack;

        configureDateSpinner();
        configureComponents();
        buildLayout(title);
        setEmployee(employee);
    }

    private void configureDateSpinner() {
        JSpinner.DateEditor editor = new JSpinner.DateEditor(birthdaySpinner, "MM/dd/yyyy");
        birthdaySpinner.setEditor(editor);
    }

    private void configureComponents() {
        JTextField[] textFields = {
                idField, lastNameField, firstNameField, addressField, phoneField,
                sssField, philHealthField, tinField, pagIbigField, positionField,
                supervisorField, basicSalaryField, riceSubsidyField, phoneAllowanceField,
                clothingAllowanceField, semiMonthlyRateField, hourlyRateField
        };

        for (JTextField field : textFields) {
            field.setFont(INPUT_FONT);
            field.setPreferredSize(new Dimension(220, 36));
        }

        birthdaySpinner.setFont(INPUT_FONT);
        birthdaySpinner.setPreferredSize(new Dimension(220, 36));

        statusCombo.setFont(INPUT_FONT);
        statusCombo.setPreferredSize(new Dimension(220, 36));

        roleCombo.setFont(INPUT_FONT);
        roleCombo.setPreferredSize(new Dimension(220, 36));
    }

    private void buildLayout(String title) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JButton backButton = new JButton("Back");
        backButton.setFont(BUTTON_FONT);
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(90, 40));
        backButton.addActionListener(e -> onBack.run());

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setText(title);

        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        row = addField(form, c, row, "Employee #", idField);
        row = addField(form, c, row, "Last Name", lastNameField);
        row = addField(form, c, row, "First Name", firstNameField);
        row = addField(form, c, row, "Birthday", birthdaySpinner);
        row = addField(form, c, row, "Address", addressField);
        row = addField(form, c, row, "Phone Number", phoneField);
        row = addField(form, c, row, "SSS #", sssField);
        row = addField(form, c, row, "PhilHealth #", philHealthField);
        row = addField(form, c, row, "TIN #", tinField);
        row = addField(form, c, row, "Pag-IBIG #", pagIbigField);
        row = addField(form, c, row, "Status", statusCombo);
        row = addField(form, c, row, "Position", positionField);
        row = addField(form, c, row, "Immediate Supervisor", supervisorField);
        row = addField(form, c, row, "Basic Salary", basicSalaryField);
        row = addField(form, c, row, "Rice Subsidy", riceSubsidyField);
        row = addField(form, c, row, "Phone Allowance", phoneAllowanceField);
        row = addField(form, c, row, "Clothing Allowance", clothingAllowanceField);
        row = addField(form, c, row, "Gross Semi-monthly Rate", semiMonthlyRateField);
        row = addField(form, c, row, "Hourly Rate", hourlyRateField);
        row = addField(form, c, row, "Role", roleCombo);

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(14, 0, 0, 0));

        JButton saveBtn = createBlackButton("Save");
        saveBtn.addActionListener(e -> onSave());

        buttonPanel.add(saveBtn);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private int addField(JPanel panel, GridBagConstraints c, int row, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText + ":");
        label.setFont(LABEL_FONT);

        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0.32;
        panel.add(label, c);

        c.gridx = 1;
        c.weightx = 0.68;
        panel.add(field, c);

        return row + 1;
    }

    private JButton createBlackButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(110, 40));
        return button;
    }

    public void setEmployee(Employee employee) {
        this.editingEmployee = employee;

        if (employee == null) {
            titleLabel.setText("Add Employee");
            clearFields();
            idField.setText(nextId());
            idField.setEditable(false);
            statusCombo.setEnabled(true);
            return;
        }

        titleLabel.setText("Update Employee");
        idField.setText(safe(employee.getId()));
        idField.setEditable(false);

        lastNameField.setText(safe(employee.getLastName()));
        firstNameField.setText(safe(employee.getFirstName()));

        if (employee.getBirthDate() != null) {
            birthdaySpinner.setValue(Date.from(
                    employee.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            ));
        }

        addressField.setText(safe(employee.getAddress()));
        phoneField.setText(safe(employee.getPhone()));
        sssField.setText(safe(employee.getSssNumber()));
        philHealthField.setText(safe(employee.getPhilHealthNumber()));
        tinField.setText(safe(employee.getTinNumber()));
        pagIbigField.setText(safe(employee.getPagIbigNumber()));
        statusCombo.setSelectedItem(safe(employee.getStatus()).isEmpty() ? "Regular" : employee.getStatus());
        positionField.setText(safe(employee.getPosition()));
        supervisorField.setText(safe(employee.getSupervisor()));
        basicSalaryField.setText(toText(employee.getBasicSalary()));
        riceSubsidyField.setText(toText(employee.getRiceSubsidy()));
        phoneAllowanceField.setText(toText(employee.getPhoneAllowance()));
        clothingAllowanceField.setText(toText(employee.getClothingAllowance()));
        semiMonthlyRateField.setText(toText(employee.getGrossSemiMonthlyRate()));
        hourlyRateField.setText(toText(employee.getHourlyRate()));
        roleCombo.setSelectedItem(employee.getRole() != null ? employee.getRole().getName() : "SALES");
    }

    private void clearFields() {
        JTextField[] textFields = {
                lastNameField, firstNameField, addressField, phoneField, sssField,
                philHealthField, tinField, pagIbigField, positionField, supervisorField,
                basicSalaryField, riceSubsidyField, phoneAllowanceField,
                clothingAllowanceField, semiMonthlyRateField, hourlyRateField
        };

        for (JTextField field : textFields) {
            field.setText("");
        }

        birthdaySpinner.setValue(new Date());
        statusCombo.setSelectedItem("Regular");
        roleCombo.setSelectedItem("SALES");
    }

    private String nextId() {
        try {
            List<Employee> all = repo.loadAll();
            return all.stream()
                    .map(Employee::getId)
                    .map(id -> {
                        try {
                            return Integer.parseInt(id);
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .max(Comparator.naturalOrder())
                    .map(maxId -> String.valueOf(maxId + 1))
                    .orElse("10001");
        } catch (IOException ex) {
            return "10001";
        }
    }

    private void onSave() {
        try {
            validateInput();

            LocalDate birthDate = toLocalDate((Date) birthdaySpinner.getValue());

            BigDecimal basicSalary = parseDecimalRequired(basicSalaryField.getText(), "Basic Salary");
            BigDecimal riceSubsidy = parseDecimalOrZero(riceSubsidyField.getText());
            BigDecimal phoneAllowance = parseDecimalOrZero(phoneAllowanceField.getText());
            BigDecimal clothingAllowance = parseDecimalOrZero(clothingAllowanceField.getText());
            BigDecimal grossSemiMonthlyRate = parseDecimalRequired(semiMonthlyRateField.getText(), "Gross Semi-monthly Rate");
            BigDecimal hourlyRate = parseDecimalRequired(hourlyRateField.getText(), "Hourly Rate");

            Employee employee = createEmployeeByStatus(
                    idField.getText().trim(),
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    birthDate,
                    basicSalary,
                    riceSubsidy,
                    phoneAllowance,
                    clothingAllowance,
                    grossSemiMonthlyRate,
                    hourlyRate
            );

            employee.setAddress(addressField.getText().trim());
            employee.setPhone(phoneField.getText().trim());
            employee.setSssNumber(sssField.getText().trim());
            employee.setPhilHealthNumber(philHealthField.getText().trim());
            employee.setTinNumber(tinField.getText().trim());
            employee.setPagIbigNumber(pagIbigField.getText().trim());
            employee.setStatus(String.valueOf(statusCombo.getSelectedItem()));
            employee.setPosition(positionField.getText().trim());
            employee.setSupervisor(supervisorField.getText().trim());
            employee.setRole(resolveRole(String.valueOf(roleCombo.getSelectedItem())));

            List<Employee> employees = repo.loadAll();

            if (editingEmployee == null) {
                employees.add(employee);
            } else {
                for (int i = 0; i < employees.size(); i++) {
                    if (employees.get(i).getId().equals(editingEmployee.getId())) {
                        employees.set(i, employee);
                        break;
                    }
                }
            }

            repo.saveAll(employees);

            JOptionPane.showMessageDialog(this, "Employee record saved successfully.");

            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not save employee record:\n" + ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validateInput() {
        requireNotBlank(lastNameField.getText(), "Last Name");
        requireNotBlank(firstNameField.getText(), "First Name");
        requireNotBlank(addressField.getText(), "Address");
        requireNotBlank(phoneField.getText(), "Phone Number");
        requireNotBlank(sssField.getText(), "SSS #");
        requireNotBlank(philHealthField.getText(), "PhilHealth #");
        requireNotBlank(tinField.getText(), "TIN #");
        requireNotBlank(pagIbigField.getText(), "Pag-IBIG #");
        requireNotBlank(positionField.getText(), "Position");
        requireNotBlank(supervisorField.getText(), "Immediate Supervisor");

        parseDecimalRequired(basicSalaryField.getText(), "Basic Salary");
        parseDecimalRequired(semiMonthlyRateField.getText(), "Gross Semi-monthly Rate");
        parseDecimalRequired(hourlyRateField.getText(), "Hourly Rate");
    }

    private void requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    private BigDecimal parseDecimalRequired(String value, String fieldName) {
        try {
            String cleaned = value == null ? "" : value.trim().replace(",", "");
            if (cleaned.isEmpty()) {
                throw new IllegalArgumentException(fieldName + " is required.");
            }
            return new BigDecimal(cleaned);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private BigDecimal parseDecimalOrZero(String value) {
        String cleaned = value == null ? "" : value.trim().replace(",", "");
        if (cleaned.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Allowance fields must be valid numbers.");
        }
    }

    private LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private Role resolveRole(String roleName) {
        Role role = availableRoles.get(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Invalid role selected: " + roleName);
        }
        return role;
    }

    private Employee createEmployeeByStatus(
            String id,
            String firstName,
            String lastName,
            LocalDate birthDate,
            BigDecimal basicSalary,
            BigDecimal riceSubsidy,
            BigDecimal phoneAllowance,
            BigDecimal clothingAllowance,
            BigDecimal grossSemiMonthlyRate,
            BigDecimal hourlyRate
    ) {
        String status = String.valueOf(statusCombo.getSelectedItem());

        if ("Regular".equalsIgnoreCase(status)) {
            return new RegularEmployee(
                    id, firstName, lastName, birthDate,
                    basicSalary, riceSubsidy, phoneAllowance, clothingAllowance,
                    grossSemiMonthlyRate, hourlyRate
            );
        }

        return new ProbationaryEmployee(
                id, firstName, lastName, birthDate,
                basicSalary, riceSubsidy, phoneAllowance, clothingAllowance,
                grossSemiMonthlyRate, hourlyRate
        );
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String toText(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }
}
