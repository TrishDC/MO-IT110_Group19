/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rhynne Gracelle
 */

package service;

import java.util.List;
import model.EmployeeLeaveRequest;

public interface EmployeeLeaveUiService {
    List<EmployeeLeaveRequest> getLeavesByEmployee(String employeeId);
    void fileLeave(EmployeeLeaveRequest request);
    void updateLeave(EmployeeLeaveRequest request);
    void deleteLeave(int leaveId, String employeeId);
}