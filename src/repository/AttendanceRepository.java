/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

/**
 *
 * @author Rhynne Gracelle
 */

import model.AttendanceRecord;
import java.util.List;

public interface AttendanceRepository {
    List<AttendanceRecord> findAll();
    List<AttendanceRecord> findByEmployeeId(String employeeId);
    AttendanceRecord findByEmployeeIdAndDate(String employeeId, String date);
    void add(AttendanceRecord record);
    void update(AttendanceRecord record);
}
