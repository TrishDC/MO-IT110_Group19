package service;

import model.Leave;
import repository.LeaveRepository;
import java.util.List;

public class LeaveService {

    private final LeaveRepository repo;

    public LeaveService(LeaveRepository repo) {
        this.repo = repo;
    }

    public LeaveService() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Leave> getAll() {
        return repo.findAll();
    }

    public void add(Leave leave) {
        if (leave.getLeaveType() == null || leave.getLeaveType().trim().isEmpty())
            throw new IllegalArgumentException("Leave type is required.");

        if (leave.getStatus() == null || leave.getStatus().trim().isEmpty())
            throw new IllegalArgumentException("Status is required.");

        repo.add(leave);
    }

    public void delete(int leaveId) {
        repo.delete(leaveId);
    }
    
    public void requestLeave(Leave leave) {
        if (leave.getLeaveType() == null || leave.getLeaveType().trim().isEmpty())
            throw new IllegalArgumentException("Leave type is required.");
        
        leave.setStatus("Pending");
        
        repo.add(leave);
    }

    public void requestLeave(String text, String text0, String text1, String text2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
