package gui;

import model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class EmployeeDetailsPanel extends JPanel {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final Runnable onBack;

    private final JPanel contentPanel = new JPanel(new GridBagLayout());
    private final JLabel titleLabel = new JLabel("Employee Details");

    public EmployeeDetailsPanel(Runnable onBack) {
        this.onBack = onBack;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        add(createHeader(), BorderLayout.NORTH);
        add(createScrollableContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(90, 40));
        backButton.addActionListener(e -> onBack.run());

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);

        return header;
    }

    private JScrollPane createScrollableContent() {
        contentPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    public void displayEmployee(
            Employee employee,
            boolean showPersonalDetails,
            boolean showGovernmentIds,
            boolean showCompensation
    ) {
        titleLabel.setText("Employee Details - " + employee.getId());
        contentPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        row = addSectionTitle(c, row, "Basic Information");
        row = addEntry(c, row, "Employee #", employee.getId());
        row = addEntry(c, row, "First Name", employee.getFirstName());
        row = addEntry(c, row, "Last Name", employee.getLastName());
        row = addEntry(c, row, "Status", employee.getStatus());
        row = addEntry(c, row, "Position", employee.getPosition());
        row = addEntry(c, row, "Immediate Supervisor", employee.getSupervisor());
        row = addEntry(c, row, "Role", employee.getRole() != null ? employee.getRole().getName() : "");

        if (showPersonalDetails) {
            row = addSeparator(c, row);
            row = addSectionTitle(c, row, "Personal Details");
            row = addEntry(c, row, "Birthday",
                    employee.getBirthDate() != null ? employee.getBirthDate().format(DATE_FORMAT) : "");
            row = addEntry(c, row, "Address", employee.getAddress());
            row = addEntry(c, row, "Phone Number", employee.getPhone());
        }

        if (showGovernmentIds) {
            row = addSeparator(c, row);
            row = addSectionTitle(c, row, "Government IDs");
            row = addEntry(c, row, "SSS #", employee.getSssNumber());
            row = addEntry(c, row, "PhilHealth #", employee.getPhilHealthNumber());
            row = addEntry(c, row, "TIN #", employee.getTinNumber());
            row = addEntry(c, row, "Pag-IBIG #", employee.getPagIbigNumber());
        }

        if (showCompensation) {
            row = addSeparator(c, row);
            row = addSectionTitle(c, row, "Compensation");
            row = addEntry(c, row, "Basic Salary", formatMoney(employee.getBasicSalary()));
            row = addEntry(c, row, "Rice Subsidy", formatMoney(employee.getRiceSubsidy()));
            row = addEntry(c, row, "Phone Allowance", formatMoney(employee.getPhoneAllowance()));
            row = addEntry(c, row, "Clothing Allowance", formatMoney(employee.getClothingAllowance()));
            row = addEntry(c, row, "Gross Semi-monthly Rate", formatMoney(employee.getGrossSemiMonthlyRate()));
            row = addEntry(c, row, "Hourly Rate", formatMoney(employee.getHourlyRate()));
        }

        revalidate();
        repaint();
    }

    private int addSectionTitle(GridBagConstraints c, int row, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(Color.BLACK);

        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        contentPanel.add(label, c);

        return row + 1;
    }

    private int addSeparator(GridBagConstraints c, int row) {
        JSeparator separator = new JSeparator();
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        contentPanel.add(separator, c);
        return row + 1;
    }

    private int addEntry(GridBagConstraints c, int row, String label, String value) {
        JLabel left = new JLabel(label + ":");
        left.setFont(new Font("Segoe UI", Font.BOLD, 13));
        left.setForeground(new Color(40, 40, 40));

        JLabel right = new JLabel(value == null ? "" : value);
        right.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        right.setForeground(new Color(70, 70, 70));

        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0.35;
        contentPanel.add(left, c);

        c.gridx = 1;
        c.weightx = 0.65;
        contentPanel.add(right, c);

        return row + 1;
    }

    private String formatMoney(BigDecimal value) {
        return value == null ? "0.00" : value.toPlainString();
    }
}