package service;

import model.Leave;
import repository.LeaveRepository;
import java.util.ArrayList;
import java.util.List;

public class LeaveService {

    private final LeaveRepository repo;

    public LeaveService(LeaveRepository repo) {
        this.repo = repo;
    }

    public List<Leave> getAll() {
        return repo.findAll();
    }

 
    public List<Leave> getByEmployeeId(String employeeId) {
        List<Leave> all = repo.findAll();
        List<Leave> filtered = new ArrayList<>();
        for (Leave l : all) {
            if (l.getEmployeeId().equalsIgnoreCase(employeeId)) {
                filtered.add(l);
            }
        }
        return filtered;
    }


    public void requestLeave(Leave leave) {
        validateLeave(leave);
        repo.add(leave);
    }

  
    public void updateLeave(Leave leave) {
        repo.update(leave);
    }

    public void updateOwnPendingLeave(Leave leave, String employeeId) {
        if (!leave.getEmployeeId().equals(employeeId)) {
            throw new IllegalArgumentException("You can only update your own leave.");
        }
        // Ensure it's still pending in the records before updating
        repo.update(leave);
    }

    public void deleteOwnPendingLeave(int leaveId, String employeeId) {

        repo.delete(leaveId);
    }

    private void validateLeave(Leave leave) {
        if (leave.getLeaveType() == null || leave.getLeaveType().trim().isEmpty())
            throw new IllegalArgumentException("Leave type is required.");
        
        if (leave.getStartDate() == null || leave.getEndDate() == null)
            throw new IllegalArgumentException("Start and End dates are required.");
        
        if (leave.getStatus() == null || leave.getStatus().trim().isEmpty())
            throw new IllegalArgumentException("Status is required.");
    }
}

