/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package repository;

import java.io.IOException;
import java.util.List;
import model.Attendance;

/**
 *
 * @author trisha
 */
public interface AttendanceRepository {
    void save(Attendance attendance) throws IOException;
    void saveAll(List<Attendance> records) throws IOException;
    List<Attendance> loadAll() throws IOException;
}

