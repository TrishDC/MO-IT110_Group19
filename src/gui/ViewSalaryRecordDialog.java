package gui;

import model.Employee;
import pay.SalaryCalculator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

public class ViewSalaryRecordDialog extends JDialog {

    public ViewSalaryRecordDialog(Frame owner, Employee employee) {
        super(owner, "Salary Record: " + employee.getId(), true);
        setLayout(new BorderLayout());

        
        BigDecimal gross = employee.calculateSalary();
        BigDecimal basic = employee.getBasicSalary();
        BigDecimal allowances = employee.getTotalAllowance();


        BigDecimal sss = SalaryCalculator.computeSssDeduction(gross);
        BigDecimal philHealth = SalaryCalculator.computePhilHealthDeduction(gross);
        BigDecimal pagIbig = SalaryCalculator.computePagIbigDeduction(gross);
        
       
        BigDecimal tax = SalaryCalculator.computeWithholdingTax(basic, sss, philHealth, pagIbig);
        
        BigDecimal totalDeductions = sss.add(philHealth).add(pagIbig).add(tax);
        BigDecimal netPay = gross.subtract(totalDeductions);

        JPanel content = new JPanel(new GridLayout(0, 2, 10, 10));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));


        addEntry(content, "Employee Name:", employee.getFirstName() + " " + employee.getLastName());
        addEntry(content, "Type:", employee.getEmployeeType());
        
        content.add(new JSeparator()); content.add(new JSeparator());


        addEntry(content, "Basic Salary:", "PHP " + basic.setScale(2, RoundingMode.HALF_UP).toPlainString());
        
    
        if (allowances.compareTo(BigDecimal.ZERO) > 0) {
            addEntry(content, "Rice Subsidy:", "+ " + employee.getRiceSubsidy().toPlainString());
            addEntry(content, "Phone Allowance:", "+ " + employee.getPhoneAllowance().toPlainString());
            addEntry(content, "Clothing Allowance:", "+ " + employee.getClothingAllowance().toPlainString());
        }
        
        JLabel grossLabel = new JLabel("GROSS PAY:");
        grossLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        content.add(grossLabel);
        addEntryValue(content, "PHP " + gross.toPlainString(), Color.BLACK, true);

        content.add(new JSeparator()); content.add(new JSeparator());

        
        addEntry(content, "SSS Deduction:", "- " + sss.toPlainString());
        addEntry(content, "PhilHealth:", "- " + philHealth.toPlainString());
        addEntry(content, "Pag-IBIG:", "- " + pagIbig.toPlainString());
        addEntry(content, "Withholding Tax:", "- " + tax.toPlainString());
        
        JLabel totalLabel = new JLabel("Total Deductions:");
        totalLabel.setForeground(Color.RED);
        content.add(totalLabel);
        addEntryValue(content, "- " + totalDeductions.toPlainString(), Color.RED, true);

        content.add(new JSeparator()); content.add(new JSeparator());

   
        JLabel netLabel = new JLabel("NET PAY:");
        netLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        content.add(netLabel);
        addEntryValue(content, "PHP " + netPay.toPlainString(), new Color(0, 102, 51), true);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);

        add(new JScrollPane(content), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(450, 550));
        setLocationRelativeTo(owner);
    }

    private void addEntry(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        JLabel valLabel = new JLabel(value);
        valLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(valLabel);
    }

    // Helper for styled values
    private void addEntryValue(JPanel panel, String value, Color color, boolean bold) {
        JLabel valLabel = new JLabel(value);
        valLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        valLabel.setForeground(color);
        if (bold) valLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(valLabel);
    }
}