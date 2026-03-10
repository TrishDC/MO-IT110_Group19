package repository;

import model.Attendance;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class CsvAttendanceRepository implements AttendanceRepository {
    private final Path csvPath;

    public CsvAttendanceRepository(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        try { if(!Files.exists(csvPath)) Files.createFile(csvPath); } 
        catch(IOException e){}
    }

    @Override
    public void save(Attendance attendance) throws IOException {
        List<Attendance> all = loadAll();
        all.add(attendance);
        saveAll(all);
    }

    @Override
    public void saveAll(List<Attendance> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(csvPath)) {
            for (Attendance a : records) {
                writer.write(String.join(",",
                    a.getEmployeeId(),
                    a.getDate().toString(),
                    a.getTimeIn().toString(),
                    a.getTimeOut() != null ? a.getTimeOut().toString() : ""
                ));
                writer.newLine();
            }
        }
    }

    @Override
    public List<Attendance> loadAll() throws IOException {
        List<Attendance> list = new ArrayList<>();
        if(!Files.exists(csvPath)) return list;

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length >= 3){
                    String empId = parts[0];
                    LocalDate date = LocalDate.parse(parts[1]);
                    LocalTime timeIn = LocalTime.parse(parts[2]);
                    LocalTime timeOut = parts.length > 3 && !parts[3].isEmpty() ? LocalTime.parse(parts[3]) : null;
                    list.add(new Attendance(empId, date, timeIn, timeOut));
                }
            }
        }
        return list;
    }
}