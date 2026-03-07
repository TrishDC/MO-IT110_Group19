package model;

import java.time.LocalDate;

public class Leave {

    private int leaveId;
    private int employeeId;
    private String leaveType;   // Sick, Vacation, etc.
    private String startDate;
    private String endDate;
    private String status;      // Pending, Approved, Rejected

    public Leave(int leaveId, int employeeId, String leaveType, String start, String endDate, String status) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = (status == null || status.trim().isEmpty()) ? "Pending" : status;
    }

    public int getLeaveId() {return leaveId;}
    public int getEmployeeId() {return employeeId;}
    public String getLeaveType() {return leaveType;}
    public String getStartDate() {return startDate;}
    public String getEndDate() {return endDate;}
    public String getStatus() {return status;}
    
    public void setStatus(String status) {this.status = status;}
}