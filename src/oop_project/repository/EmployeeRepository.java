package com.motorph.employeeapp.repository;

import oop_project.model.Employee;
import java.io.IOException;
import java.util.List;

public interface EmployeeRepository {
    List<Employee> loadAll() throws IOException;
    void saveAll(List<Employee> employees) throws IOException;
}