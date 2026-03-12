package repository;

import model.Leave;
import java.util.List;

public interface LeaveRepository {
    List<Leave> findAll();
    List<Leave> findByEmployeeId(String employeeId);
    void add(Leave leave);
    void update(Leave leave);
    void delete(int leaveId);
}