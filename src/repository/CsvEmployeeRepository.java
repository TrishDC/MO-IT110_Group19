package repository;

import RBAC.RBACSetup;
import RBAC.Role;
import model.Employee;
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
import java.util.Map;

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
        Map<String, Role> roles = RBACSetup.setupRoles();

        try (CSVReader csv = new CSVReader(
                new FileReader(csvPath.toFile(), StandardCharsets.UTF_8))) {

            csv.readNext(); // Skip header

            String[] parts;

            while ((parts = csv.readNext()) != null) {

                if (parts.length < 20) {
                    continue;
                }

                String id = parts[0].trim();
                String last = parts[1].trim();
                String first = parts[2].trim();
                LocalDate birth = parseDateOrNow(parts[3]);

                BigDecimal basic = parseDecimalOrZero(cleanNumber(parts[13]));
                BigDecimal rice = parseDecimalOrZero(cleanNumber(parts[14]));
                BigDecimal phoneA = parseDecimalOrZero(cleanNumber(parts[15]));
                BigDecimal clothA = parseDecimalOrZero(cleanNumber(parts[16]));
                BigDecimal semi = parseDecimalOrZero(cleanNumber(parts[17]));
                BigDecimal hour = parseDecimalOrZero(cleanNumber(parts[18]));

                String roleName = parts[19] == null ? "" : parts[19].trim().toUpperCase();
                Role role = roles.get(roleName);

                Employee employee = new Employee(
                        id,
                        first,
                        last,
                        birth,
                        basic,
                        rice,
                        phoneA,
                        clothA,
                        semi,
                        hour
                ) {
                    @Override
                    public BigDecimal calculateSalary() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
                
                employee.setAddress(parts[4]);
                employee.setPhone(fixLongNumber(parts[5]));
                employee.setSssNumber(fixLongNumber(parts[6]));
                employee.setPhilHealthNumber(fixLongNumber(parts[7]));
                employee.setTinNumber(fixLongNumber(parts[8]));
                employee.setPagIbigNumber(fixLongNumber(parts[9]));
                employee.setStatus(parts[10]);
                employee.setPosition(parts[11]);
                employee.setSupervisor(parts[12]);
                employee.setRole(role);

                employees.add(employee);
            }

        } catch (CsvValidationException ex) {
            throw new IOException("Invalid CSV format", ex);
        }

        return employees;
    }

    @Override
    public void saveAll(List<Employee> employees) throws IOException {

        Path temp = csvPath.resolveSibling(csvPath.getFileName() + ".tmp");

        try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8);
             CSVWriter csv = new CSVWriter(writer)) {

            String[] header = {
                    "Employee",
                    "Last Name",
                    "First Name",
                    "Birthday",
                    "Address",
                    "Phone Number",
                    "SSS Number",
                    "PhilHealth Number",
                    "TIN Number",
                    "Pag-IBIG Number",
                    "Status",
                    "Position",
                    "Supervisor",
                    "Basic Salary",
                    "Rice Subsidy",
                    "Phone Allowance",
                    "Clothing Allowance",
                    "Semi-monthly Rate",
                    "Hourly Rate"
            };

            csv.writeNext(header);

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

        Files.deleteIfExists(csvPath);
        Files.move(temp, csvPath);
    }

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

    private String fixLongNumber(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        value = value.trim();

        if (value.contains("E") || value.contains("e")) {

            try {
                value = new BigDecimal(value).toPlainString();
            } catch (NumberFormatException ex) {
            }
        }
        
        

        return value;
    }
    
    private String cleanNumber(String value) {
        if (value == null) {
            return "0";
        }
        return value.replace(",", "").trim();
    }
}
    
