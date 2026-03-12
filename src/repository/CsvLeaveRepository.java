package repository;

import model.Leave;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvLeaveRepository implements LeaveRepository {

    private final String filePath;

    public CsvLeaveRepository() {
        this("data/leaves.csv");
    }

    public CsvLeaveRepository(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
                try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                    pw.println("leaveId,employeeId,leaveType,startDate,endDate,reason,notes,status");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize leave CSV file.", e);
        }
    }

    @Override
    public List<Leave> findAll() {
        List<Leave> leaves = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    if (line.toLowerCase().startsWith("leaveid,")) {
                        continue;
                    }
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 8) {
                    continue;
                }

                Leave leave = new Leave();
                leave.setLeaveId(parseInt(parts[0]));
                leave.setEmployeeId(parts[1]);
                leave.setLeaveType(parts[2]);
                leave.setStartDate(parts[3]);
                leave.setEndDate(parts[4]);
                leave.setReason("");
                leave.setNotes(parts[6]);
                leave.setStatus(parts[7]);

                leaves.add(leave);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read leave records.", e);
        }

        return leaves;
    }

    @Override
    public List<Leave> findByEmployeeId(String employeeId) {
        List<Leave> result = new ArrayList<>();
        for (Leave leave : findAll()) {
            if (safe(leave.getEmployeeId()).equals(safe(employeeId))) {
                result.add(leave);
            }
        }
        return result;
    }

    @Override
    public void add(Leave leave) {
        List<Leave> leaves = findAll();
        leave.setLeaveId(generateNextId(leaves));
        leaves.add(leave);
        saveAll(leaves);
    }

    @Override
    public void update(Leave leave) {
        List<Leave> leaves = findAll();

        for (int i = 0; i < leaves.size(); i++) {
            if (leaves.get(i).getLeaveId() == leave.getLeaveId()) {
                leaves.set(i, leave);
                saveAll(leaves);
                return;
            }
        }

        throw new IllegalArgumentException("Leave record not found.");
    }

    @Override
    public void delete(int leaveId) {
        List<Leave> leaves = findAll();
        leaves.removeIf(l -> l.getLeaveId() == leaveId);
        saveAll(leaves);
    }

    private void saveAll(List<Leave> leaves) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("leaveId,employeeId,leaveType,startDate,endDate,reason,notes,status");

            for (Leave leave : leaves) {
                pw.println(
                        leave.getLeaveId() + "," +
                        esc(leave.getEmployeeId()) + "," +
                        esc(leave.getLeaveType()) + "," +
                        esc(leave.getStartDate()) + "," +
                        esc(leave.getEndDate()) + "," +
                        esc(leave.getReason()) + "," +
                        esc(leave.getNotes()) + "," +
                        esc(leave.getStatus())
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save leave records.", e);
        }
    }

    private int generateNextId(List<Leave> leaves) {
        int max = 0;
        for (Leave leave : leaves) {
            if (leave.getLeaveId() > max) {
                max = leave.getLeaveId();
            }
        }
        return max + 1;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String esc(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(",", " ");
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}