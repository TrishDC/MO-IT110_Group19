/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author Rhynne Gracelle
 */

package gui;

import service.EmployeeLeaveUiService;
import service.InMemoryEmployeeLeaveUiService;

import javax.swing.SwingUtilities;

public class EmployeeLeavesLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeLeaveUiService service = new InMemoryEmployeeLeaveUiService();

            EmployeeLeavesFrame frame = new EmployeeLeavesFrame(
                    service,
                    "10001",
                    "Name",
                    "IT",
                    "Position"
            );

            frame.setVisible(true);
        });
    }
}