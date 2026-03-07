package repository;

import model.Leave;
import java.util.List;

public interface LeaveRepository {
    List<Leave> findAll();
    void add(Leave leave);
    void delete(int leaveId);
}




