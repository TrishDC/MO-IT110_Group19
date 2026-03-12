/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */

package gui;

import javax.swing.*;
import java.awt.*;

public class PayrollPanel extends JPanel {
    public PayrollPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel label = new JLabel("Payroll Page", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));

        add(label, BorderLayout.CENTER);
    }
}