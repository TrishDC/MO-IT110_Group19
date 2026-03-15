package gui;

import model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class EmployeeFormPanel extends JPanel {

    public interface SaveListener {
        void onSave(EmployeeFormPanel panel);
    }

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    private final Runnable onBack;
    private final JLabel titleLabel = new JLabel();

    private SaveListener saveListener;
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
            Employee employee,
            Runnable onBack
    ) {
        this.editingEmployee = employee;
        this.onBack = onBack;

        configureDateSpinner();
        configureComponents();
        buildLayout(title);
        setEmployee(employee);
    }

    public void setSaveListener(SaveListener saveListener) {
        this.saveListener = saveListener;
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
        saveBtn.addActionListener(e -> {
            if (saveListener != null) {
                saveListener.onSave(this);
            }
        });

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
            idField.setText("");
            idField.setEditable(false);
            statusCombo.setEnabled(true);
            return;
        }

        titleLabel.setText("Update Employee");
        idField.setText(safeText(employee.getId()));
        idField.setEditable(false);

        lastNameField.setText(safeText(employee.getLastName()));
        firstNameField.setText(safeText(employee.getFirstName()));

        if (employee.getBirthDate() != null) {
            birthdaySpinner.setValue(Date.from(
                    employee.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            ));
        }

        addressField.setText(safeText(employee.getAddress()));
        phoneField.setText(safeText(employee.getPhone()));
        sssField.setText(safeText(employee.getSssNumber()));
        philHealthField.setText(safeText(employee.getPhilHealthNumber()));
        tinField.setText(safeText(employee.getTinNumber()));
        pagIbigField.setText(safeText(employee.getPagIbigNumber()));
        statusCombo.setSelectedItem(safeText(employee.getStatus()).isEmpty() ? "Regular" : employee.getStatus());
        positionField.setText(safeText(employee.getPosition()));
        supervisorField.setText(safeText(employee.getSupervisor()));
        basicSalaryField.setText(toDisplayText(employee.getBasicSalary()));
        riceSubsidyField.setText(toDisplayText(employee.getRiceSubsidy()));
        phoneAllowanceField.setText(toDisplayText(employee.getPhoneAllowance()));
        clothingAllowanceField.setText(toDisplayText(employee.getClothingAllowance()));
        semiMonthlyRateField.setText(toDisplayText(employee.getGrossSemiMonthlyRate()));
        hourlyRateField.setText(toDisplayText(employee.getHourlyRate()));
        roleCombo.setSelectedItem(employee.getRole() != null ? employee.getRole().getName() : "SALES");
    }

    public void setCreateMode() {
        setEmployee(null);
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

    public void setEmployeeId(String employeeId) {
        idField.setText(employeeId == null ? "" : employeeId.trim());
    }

    public String getEmployeeIdInput() {
        return idField.getText().trim();
    }

    public String getLastNameInput() {
        return lastNameField.getText().trim();
    }

    public String getFirstNameInput() {
        return firstNameField.getText().trim();
    }

    public LocalDate getBirthDateInput() {
        Date date = (Date) birthdaySpinner.getValue();
        return date == null ? null : toLocalDate(date);
    }

    public String getAddressInput() {
        return addressField.getText().trim();
    }

    public String getPhoneInput() {
        return phoneField.getText().trim();
    }

    public String getSssInput() {
        return sssField.getText().trim();
    }

    public String getPhilHealthInput() {
        return philHealthField.getText().trim();
    }

    public String getTinInput() {
        return tinField.getText().trim();
    }

    public String getPagIbigInput() {
        return pagIbigField.getText().trim();
    }

    public String getStatusInput() {
        Object selected = statusCombo.getSelectedItem();
        return selected == null ? "" : selected.toString().trim();
    }

    public String getPositionInput() {
        return positionField.getText().trim();
    }

    public String getSupervisorInput() {
        return supervisorField.getText().trim();
    }

    public String getBasicSalaryInput() {
        return basicSalaryField.getText().trim();
    }

    public String getRiceSubsidyInput() {
        return riceSubsidyField.getText().trim();
    }

    public String getPhoneAllowanceInput() {
        return phoneAllowanceField.getText().trim();
    }

    public String getClothingAllowanceInput() {
        return clothingAllowanceField.getText().trim();
    }

    public String getSemiMonthlyRateInput() {
        return semiMonthlyRateField.getText().trim();
    }

    public String getHourlyRateInput() {
        return hourlyRateField.getText().trim();
    }

    public String getRoleInput() {
        Object selected = roleCombo.getSelectedItem();
        return selected == null ? "" : selected.toString().trim();
    }

    private LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String toDisplayText(java.math.BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }
}