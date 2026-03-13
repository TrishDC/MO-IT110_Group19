package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HRDepartment extends RegularEmployee {

    public HRDepartment(String id, String firstName, String lastName, LocalDate birthDate, 
                        BigDecimal basicSalary, BigDecimal riceSubsidy, BigDecimal phoneAllowance, 
                        BigDecimal clothingAllowance, BigDecimal grossSemiMonthlyRate, BigDecimal hourlyRate) {
        
        super(id, firstName, lastName, birthDate, basicSalary, riceSubsidy, phoneAllowance, 
              clothingAllowance, grossSemiMonthlyRate, hourlyRate);
    }


    public void approveLeaveRequest(EmployeeLeaveRequest request) {
        if (request == null) return;
        request.setStatus("Approved");
        request.setNotes("Approved by HR: " + this.getFirstName());
        System.out.println("Leave " + request.getLeaveId() + " approved.");
    }

    public void approveLeaveRequest(EmployeeLeaveRequest request, String adminNotes) {
        if (request == null) return;
        request.setStatus("Approved");
        request.setNotes(adminNotes);
        System.out.println("Leave " + request.getLeaveId() + " approved with notes.");
    }


    public void rejectLeaveRequest(EmployeeLeaveRequest request) {
        if (request == null) return;
        request.setStatus("Rejected");
        request.setNotes("Rejected by HR: " + this.getFirstName());
        System.out.println("Leave " + request.getLeaveId() + " rejected.");
    }


    public void rejectLeaveRequest(EmployeeLeaveRequest request, String reason) {
        if (request == null) return;
        request.setStatus("Rejected");
        request.setReason(reason);
        request.setNotes("Rejected by HR: " + this.getFirstName());
        System.out.println("Leave " + request.getLeaveId() + " rejected. Reason: " + reason);
    }

  

    @Override
    public String getEmployeeType() {
        return "HR Department";
    }

    @Override
    public String toString() {
        return "HR Department " + getFirstName() + " " + getLastName();
    }
}