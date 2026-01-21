package com.motorph.employeeapp.repository;

import com.motorph.employeeapp.model.Employee;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-based Employee repository with full saveAll implementation.
 */
public class CsvEmployeeRepository implements EmployeeRepository {
    private final Path csvPath;
    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("M/d/yyyy");

    public CsvEmployeeRepository(String path) {
        this.csvPath = Paths.get(path);
    }

   @Override
public List<Employee> loadAll() throws IOException {
    List<Employee> employees = new ArrayList<>();
    try (CSVReader csv = new CSVReader(
             new FileReader(csvPath.toFile(), StandardCharsets.UTF_8))) {

        // 1) Skip header
        csv.readNext();

        // 2) Read every row
        String[] parts;
        while ((parts = csv.readNext()) != null) {
            // if blank line, skip
            if (parts.length == 0) continue;

            // build Employee from exactly 19 columns:
            //   0: ID, 1: Last, 2: First, 3: Birthday,
            //   4: Address, 5: Phone, 6: SSS, 7: PhilHealth,
            //   8: TIN, 9: PagIbig, 10: Status, 11: Position,
            //   12: Supervisor, 13: Basic, 14: Rice, 15: PhoneAllowance,
            //   16: ClothingAllowance, 17: SemiMonthly, 18: Hourly
            String id       = parts[0];
            String last     = parts[1];
            String first    = parts[2];
            LocalDate birth = parseDateOrNow(parts[3]);

            BigDecimal basic  = parseDecimalOrZero(parts[13]);
            BigDecimal rice   = parseDecimalOrZero(parts[14]);
            BigDecimal phoneA = parseDecimalOrZero(parts[15]);
            BigDecimal clothA = parseDecimalOrZero(parts[16]);
            BigDecimal semi   = parseDecimalOrZero(parts[17]);
            BigDecimal hour   = parseDecimalOrZero(parts[18]);

            Employee e = new Employee(
              id, first, last, birth,
              basic, rice, phoneA, clothA, semi, hour
            );

            // now set all the other text fields
            e.setAddress(           parts[4] );
            e.setPhone(             parts[5] );
            e.setSssNumber(         parts[6] );
            e.setPhilHealthNumber(  parts[7] );
            e.setTinNumber(         parts[8] );
            e.setPagIbigNumber(     parts[9] );
            e.setStatus(            parts[10]);
            e.setPosition(          parts[11]);
            e.setSupervisor(        parts[12]);

            employees.add(e);
        }
    } catch (CsvValidationException ex) {
        throw new IOException("Invalid CSV format", ex);
    }
    return employees;
}

    @Override
    public void saveAll(List<Employee> employees) throws IOException {
        // Write to a temp file first
        Path temp = csvPath.resolveSibling(csvPath.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8);
             CSVWriter csv = new CSVWriter(writer)) {
            // 1) Header
            String[] header = {
                "Employee", "Last Name", "First Name", "Birthday",
                "Address", "Phone Number", "SSS Number", "PhilHealth Number",
                "TIN Number", "Pag-IBIG Number", "Status", "Position",
                "Supervisor", "Basic Salary", "Rice Subsidy",
                "Phone Allowance", "Clothing Allowance",
                "Semi-monthly Rate", "Hourly Rate"
            };
            csv.writeNext(header);

            // 2) Rows
            for (Employee e : employees) {
                String[] row = {
                    e.getId(),
                    e.getLastName(),
                    e.getFirstName(),
                    e.getBirthDate().format(DATE_FMT),
                    e.getAddress(),
                    e.getPhone(),
                    e.getSssNumber(),
                    e.getPhilHealthNumber(),
                    e.getTinNumber(),
                    e.getPagIbigNumber(),
                    e.getStatus(),
                    e.getPosition(),
                    e.getSupervisor(),
                    e.getBasicSalary().toPlainString(),
                    e.getRiceSubsidy().toPlainString(),
                    e.getPhoneAllowance().toPlainString(),
                    e.getClothingAllowance().toPlainString(),
                    e.getGrossSemiMonthlyRate().toPlainString(),
                    e.getHourlyRate().toPlainString()
                };
                csv.writeNext(row);
            }
        }
        // Atomically replace
        Files.delete(csvPath);
        Files.move(temp, csvPath);
    }

    // Helpers
    private LocalDate parseDateOrNow(String txt) {
        try {
            return LocalDate.parse(txt, DATE_FMT);
        } catch (Exception ex) {
            return LocalDate.now();
        }
    }

    private BigDecimal parseDecimalOrZero(String txt) {
        try {
            return new BigDecimal(txt);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }
}
