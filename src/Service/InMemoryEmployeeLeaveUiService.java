/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author Rhynne Gracelle
 */

package service;

import model.EmployeeLeaveRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryEmployeeLeaveUiService implements EmployeeLeaveUiService {

    private final List<EmployeeLeaveRequest> data = new ArrayList<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public List<EmployeeLeaveRequest> getLeavesByEmployee(String employeeId) {
        return data.stream()
                .filter(r -> r.getEmployeeId().equals(employeeId))
                .sorted(Comparator.comparingInt(EmployeeLeaveRequest::getLeaveId))
                .collect(Collectors.toList());
    }

    @Override
    public void fileLeave(EmployeeLeaveRequest request) {
        LeaveRequestValidator.validate(request);
        request.setLeaveId(nextId.getAndIncrement());
        request.setStatus("Pending");
        data.add(copy(request));
    }

    @Override
    public void updateLeave(EmployeeLeaveRequest request) {
        LeaveRequestValidator.validate(request);

        for (int i = 0; i < data.size(); i++) {
            EmployeeLeaveRequest existing = data.get(i);
            if (existing.getLeaveId() == request.getLeaveId()
                    && existing.getEmployeeId().equals(request.getEmployeeId())) {

                if (!"Pending".equalsIgnoreCase(existing.getStatus())) {
                    throw new IllegalArgumentException("Only pending leave requests can be updated.");
                }

                request.setStatus(existing.getStatus());
                data.set(i, copy(request));
                return;
            }
        }

        throw new IllegalArgumentException("Leave request not found.");
    }

    @Override
    public void deleteLeave(int leaveId, String employeeId) {
        EmployeeLeaveRequest target = null;

        for (EmployeeLeaveRequest r : data) {
            if (r.getLeaveId() == leaveId && r.getEmployeeId().equals(employeeId)) {
                target = r;
                break;
            }
        }

        if (target == null) {
            throw new IllegalArgumentException("Leave request not found.");
        }

        if (!"Pending".equalsIgnoreCase(target.getStatus())) {
            throw new IllegalArgumentException("Only pending leave requests can be deleted.");
        }

        data.remove(target);
    }

    private EmployeeLeaveRequest copy(EmployeeLeaveRequest src) {
        return new EmployeeLeaveRequest(
                src.getLeaveId(),
                src.getEmployeeId(),
                src.getEmployeeName(),
                src.getDepartment(),
                src.getStartDate(),
                src.getEndDate(),
                src.getReason(),
                src.getNotes(),
                src.getStatus()
        );
    }
}