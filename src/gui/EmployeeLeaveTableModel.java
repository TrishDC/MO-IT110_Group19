/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author Rhynne Gracelle
 */

package gui;

import model.EmployeeLeaveRequest;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EmployeeLeaveTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Name", "Department", "Start Date", "End Date", "Reason", "Notes", "Status"
    };

    private List<EmployeeLeaveRequest> requests = new ArrayList<>();

    public void setRequests(List<EmployeeLeaveRequest> requests) {
        this.requests = requests != null ? requests : new ArrayList<>();
        fireTableDataChanged();
    }

    public EmployeeLeaveRequest getRequestAt(int row) {
        if (row < 0 || row >= requests.size()) {
            return null;
        }
        return requests.get(row);
    }

    @Override
    public int getRowCount() {
        return requests.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EmployeeLeaveRequest r = requests.get(rowIndex);

        switch (columnIndex) {
            case 0: return r.getEmployeeName();
            case 1: return r.getDepartment();
            case 2: return r.getStartDate();
            case 3: return r.getEndDate();
            case 4: return r.getReason();
            case 5: return r.getNotes();
            case 6: return r.getStatus();
            default: return "";
        }
    }
}