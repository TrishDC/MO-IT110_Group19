package com.motorph.employeeapp.repository;

import com.motorph.employeeapp.model.Employee;
import java.io.IOException;
import java.util.List;

public interface EmployeeRepository {
    List<Employee> loadAll() throws IOException;
    void saveAll(List<Employee> employees) throws IOException;
}