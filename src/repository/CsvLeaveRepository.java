package repository;

import model.Leave;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvLeaveRepository implements LeaveRepository {

    private final String filePath = "data/leaves.csv";

    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create leaves.csv: " + e.getMessage());
        }
    }

    @Override
    public List<Leave> findAll() {
        ensureFileExists();
        
        System.out.println("Reading from: " + new java.io.File(filePath).getAbsolutePath());
        List<Leave> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);

                // expected length 6
                if (p.length < 6) continue;

                Leave leave;
                leave = new Leave(
                        Integer.parseInt(p[0].trim()),
                        Integer.parseInt(p[1].trim()),
                        p[2].trim(),
                        p[3].trim(),
                        p[4].trim(),
                        p[5].trim()
                );
                list.add(leave);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading leaves.csv: " + e.getMessage());
        }

        return list;
    }

    @Override
    public void add(Leave leave) {
        ensureFileExists();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(leave.getLeaveId() + "," +
                    leave.getEmployeeId() + "," +
                    leave.getLeaveType() + "," +
                    leave.getStartDate() + "," +
                    leave.getEndDate() + "," +
                    leave.getStatus());
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing leaves.csv: " + e.getMessage());
        }
    }

    @Override
    public void delete(int leaveId) {
        ensureFileExists();
        List<Leave> all = findAll();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Leave l : all) {
                if (l.getLeaveId() == leaveId) continue; // skip
                bw.write(l.getLeaveId() + "," +
                        l.getEmployeeId() + "," +
                        l.getLeaveType() + "," +
                        l.getStartDate() + "," +
                        l.getEndDate() + "," +
                        l.getStatus());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting leave: " + e.getMessage());
        }
    }
}