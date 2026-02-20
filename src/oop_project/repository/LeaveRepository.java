package oop_project.repository;

import oop_project.model.Leave;
import java.util.List;

public interface LeaveRepository {
    List<Leave> findAll();
    void add(Leave leave);
    void delete(int leaveId);
}
