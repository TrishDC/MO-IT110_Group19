package oop_project.service;

import oop_project.model.Leave;
import oop_project.repository.LeaveRepository;
import java.util.List;

public class LeaveService {

    private final LeaveRepository repo;

    public LeaveService(LeaveRepository repo) {
        this.repo = repo;
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
}
