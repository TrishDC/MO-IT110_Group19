/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Rhynne Gracelle
 */

public class EmployeeLeaveRequest {
    private int leaveId;
    private String employeeId;
    private String employeeName;
    private String department;
    private String startDate;   // yyyy-MM-dd
    private String endDate;     // yyyy-MM-dd
    private String reason;
    private String notes;
    private String status;      // Pending, Approved, Rejected

    public EmployeeLeaveRequest() {
    }

    public EmployeeLeaveRequest(int leaveId, String employeeId, String employeeName, String department,
                                String startDate, String endDate, String reason, String notes, String status) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.notes = notes;
        this.status = status;
    }

    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}