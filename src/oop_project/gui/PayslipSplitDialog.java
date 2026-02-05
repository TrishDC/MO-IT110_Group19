package com.motorph.employeeapp.gui;
import oop_project.model.Employee;
import oop_project.pay.SalaryCalculator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import oop_project.model.Employee;

public class PayslipSplitDialog extends JDialog {
    private final Employee emp;

    // left fields
    private final JTextField idF   = new JTextField(15);
    private final JTextField lnF   = new JTextField(15);
    private final JTextField fnF   = new JTextField(15);
    private final JTextField bdF   = new JTextField(15);
    // Address area now multi-line
    private final JTextArea addr   = new JTextArea(3, 20);
    private final JTextField phF   = new JTextField(15);
    private final JTextField sssF  = new JTextField(15);
    private final JTextField philF = new JTextField(15);
    private final JTextField tinF  = new JTextField(15);
    private final JTextField pagF  = new JTextField(15);
    private final JTextField stF   = new JTextField(15);
    private final JTextField posF  = new JTextField(15);
    private final JTextField supF  = new JTextField(15);

    // right controls
    private final JComboBox<String> monthCB = new JComboBox<>();
    private final JTextField periodF   = new JTextField(25);
    private final JTextField earnedF   = new JTextField(25);
    private final JTextField riceF     = new JTextField(25);
    private final JTextField phAllF    = new JTextField(25);
    private final JTextField clAllF    = new JTextField(25);
    private final JTextField grossF    = new JTextField(25);
    private final JTextField sssDedF   = new JTextField(25);
    private final JTextField philDedF  = new JTextField(25);
    private final JTextField pagDedF   = new JTextField(25);
    private final JTextField taxDedF   = new JTextField(25);
    private final JTextField totDedF   = new JTextField(25);
    private final JTextField netF      = new JTextField(25);

    public PayslipSplitDialog(Frame owner, Employee e) {
        super(owner, "Employee Payslip", true);
        this.emp = e;

        // populate left
        idF .setText(e.getId());
        lnF .setText(e.getLastName());
        fnF .setText(e.getFirstName());
        bdF .setText(e.getBirthDate().format(
            DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addr.setText(e.getAddress());
        phF .setText(e.getPhone());
        sssF.setText(e.getSssNumber());
        philF.setText(e.getPhilHealthNumber());
        tinF.setText(e.getTinNumber());
        pagF.setText(e.getPagIbigNumber());
        stF .setText(e.getStatus());
        posF.setText(e.getPosition());
        supF.setText(e.getSupervisor());

        // configure address area
        addr.setLineWrap(true);
        addr.setWrapStyleWord(true);
        addr.setEditable(false);
        addr.setBackground(UIManager.getColor("TextField.background"));
        addr.setBorder(UIManager.getBorder("TextField.border"));

        // make left fields non-editable
        for (var f : new JComponent[]{idF, lnF, fnF, bdF, phF, sssF, philF, tinF, pagF, stF, posF, supF}) {
            if (f instanceof JTextField) ((JTextField)f).setEditable(false);
        }

        // months dropdown
        String[] m = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for (String mo : m) monthCB.addItem(mo);
        monthCB.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        // make outputs non-editable and sizable
        for (var f : new JTextField[]{periodF, earnedF, riceF, phAllF, clAllF, grossF, sssDedF, philDedF, pagDedF, taxDedF, totDedF, netF}) {
            f.setEditable(false);
            f.setPreferredSize(new Dimension(200,24));
        }

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            buildDetailPane(),
            buildCalculatorPane()
        );
        split.setResizeWeight(0.5);
        split.setDividerLocation(350);

        getContentPane().add(split);
        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel buildDetailPane() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.LINE_END;

        String[] labels = {
            "Employee No.:","Last Name:","First Name:","Birthday:",
            "Address:","Phone:","SSS #:","PhilHealth #:","TIN #:","Pag-IBIG #:",
            "Status:","Position:","Supervisor:"
        };
        Component[] fields = {
            idF, lnF, fnF, bdF,
            new JScrollPane(addr),
            phF, sssF, philF, tinF, pagF,
            stF, posF, supF
        };

        for (int i = 0; i < labels.length; i++) {
            c.gridy = i;
            c.gridx = 0;
            p.add(new JLabel(labels[i]), c);
            c.gridx = 1;
            p.add(fields[i], c);
        }
        return p;
    }

    private JPanel buildCalculatorPane() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        // month + compute
        c.gridy = 0; c.gridx = 0;
        p.add(new JLabel("Month:"), c);
        c.gridx = 1; p.add(monthCB, c);
        c.gridx = 2;
        JButton compute = new JButton("Compute");
        compute.setBackground(new Color(45,137,239));
        compute.setForeground(Color.WHITE);
        compute.setFocusPainted(false);
        compute.setBorder(new EmptyBorder(8,16,8,16));
        compute.addActionListener(_ -> doCompute());
        p.add(compute, c);

        String[] lbls = {
            "Salary Period:", "Salary Earned:", "Rice Allowance:",
            "Phone Allowance:", "Clothing Allowance:", "Gross:",
            "SSS Deduction:", "PhilHealth Deduction:", "Pag-IBIG Deduction:",
            "Withholding Tax:", "Total Deductions:", "Net Salary:"
        };
        JTextField[] flds = {periodF, earnedF, riceF, phAllF, clAllF, grossF, sssDedF, philDedF, pagDedF, taxDedF, totDedF, netF};

        for (int i = 0; i < lbls.length; i++) {
            c.gridy = i + 1;
            c.gridx = 0;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            p.add(new JLabel(lbls[i]), c);

            c.gridx = 1;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            p.add(flds[i], c);

            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
        }

        return p;
    }

    private void doCompute() {
        int m = monthCB.getSelectedIndex() + 1;
        YearMonth ym = YearMonth.of(LocalDate.now().getYear(), m);

        BigDecimal gross = emp.getGrossSemiMonthlyRate().multiply(BigDecimal.valueOf(2));
        grossF.setText(gross.toPlainString());
        
       
        BigDecimal earned = SalaryCalculator.computeMonthlyPay(emp, ym);
        earnedF.setText(earned.toPlainString());

        riceF.setText(emp.getRiceSubsidy().toPlainString());
        phAllF.setText(emp.getPhoneAllowance().toPlainString());
        clAllF.setText(emp.getClothingAllowance().toPlainString());

        BigDecimal sssDed  = SalaryCalculator.computeSssDeduction(gross);
        BigDecimal philDed = SalaryCalculator.computePhilHealthDeduction(gross);
        BigDecimal pagDed  = SalaryCalculator.computePagIbigDeduction(gross);
        sssDedF.setText(sssDed.toPlainString());
        philDedF.setText(philDed.toPlainString());
        pagDedF.setText(pagDed.toPlainString());

        BigDecimal tax = SalaryCalculator.computeWithholdingTax(earned, sssDed, philDed, pagDed);
        taxDedF.setText(tax.toPlainString());

        BigDecimal tot = sssDed.add(philDed).add(pagDed).add(tax);
        totDedF.setText(tot.toPlainString());
        netF.setText(earned.subtract(tot).toPlainString());

        String mon = ym.getMonth().name().substring(0,1)
                   + ym.getMonth().name().substring(1).toLowerCase();
        periodF.setText(mon + " " + ym.getYear());
    }
}
