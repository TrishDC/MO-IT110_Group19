package gui;

import model.Employee;
import pay.SalaryCalculator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.YearMonth;

public class ViewSalaryRecordDialog extends JDialog {

    public ViewSalaryRecordDialog(Frame owner, Employee employee) {
        super(owner, "Salary Record: " + employee.getId(), true);
        setLayout(new BorderLayout());

        BigDecimal gross = SalaryCalculator.computeMonthlyPay(employee, YearMonth.now());
        BigDecimal sss = SalaryCalculator.computeSssDeduction(gross);
        BigDecimal philHealth = SalaryCalculator.computePhilHealthDeduction(gross);
        BigDecimal pagIbig = SalaryCalculator.computePagIbigDeduction(gross);
        BigDecimal tax = SalaryCalculator.computeWithholdingTax(gross, sss, philHealth, pagIbig);
        BigDecimal totalDeductions = sss.add(philHealth).add(pagIbig).add(tax);
        BigDecimal netPay = gross.subtract(totalDeductions);

        JPanel content = new JPanel(new GridLayout(0, 2, 10, 10));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));


        addEntry(content, "Employee Name:", employee.getFirstName() + " " + employee.getLastName());
        addEntry(content, "Position:", employee.getPosition());
        addEntry(content, "Employment Type:", employee.getEmployeeType());
        
        content.add(new JSeparator()); content.add(new JSeparator());

        addEntry(content, "Gross Monthly Pay:", "PHP " + gross.toPlainString());
        addEntry(content, "SSS Contribution (4%):", "- " + sss.toPlainString());
        addEntry(content, "PhilHealth (2.75%):", "- " + philHealth.toPlainString());
        addEntry(content, "Pag-IBIG (2%):", "- " + pagIbig.toPlainString());
        addEntry(content, "Withholding Tax (10%):", "- " + tax.toPlainString());

        content.add(new JSeparator()); content.add(new JSeparator());

        JLabel netLabel = new JLabel("NET PAY:");
        netLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel netValue = new JLabel("PHP " + netPay.toPlainString());
        netValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        netValue.setForeground(new Color(0, 102, 51));
        
        content.add(netLabel);
        content.add(netValue);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);

        add(content, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(400, 450));
        setLocationRelativeTo(owner);
    }

    private void addEntry(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        JLabel valLabel = new JLabel(value);
        valLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(valLabel);
    }
}