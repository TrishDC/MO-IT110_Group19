package repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import model.Employee;

public class EmployeeValidator {

    private static final String SSS_PATTERN = "\\d{2}-\\d{7}-\\d";
    // Modified to strictly allow 9 or 12 digit formats (standard for PH TIN)
    private static final String TIN_PATTERN = "\\d{3}-\\d{3}-\\d{3}(-\\d{3})?";
    private static final String PH_PAGIBIG_PATTERN = "\\d{12}"; 
    private static final String PHONE_PATTERN = "\\d{3}-\\d{3}-\\d{3}";

    public static String validateAll(Employee emp, List<Employee> existingList) {
        if (emp == null) return "Employee data is null.";

        if (emp.getFirstName() == null || emp.getFirstName().isBlank()) return "First Name is required.";
        if (emp.getLastName() == null || emp.getLastName().isBlank()) return "Last Name is required.";
   

    
        if (emp.getPhone() == null || !emp.getPhone().matches(PHONE_PATTERN)) 
            return "Invalid Phone format (XXX-XXX-XXX).";
        
        if (emp.getSssNumber() == null || !emp.getSssNumber().matches(SSS_PATTERN)) 
            return "Invalid SSS format (XX-XXXXXXX-X).";
        
        if (emp.getTinNumber() == null || !emp.getTinNumber().matches(TIN_PATTERN)) 
            return "Invalid TIN format (XXX-XXX-XXX or XXX-XXX-XXX-XXX).";
        
        if (emp.getPhilHealthNumber() == null || !emp.getPhilHealthNumber().matches(PH_PAGIBIG_PATTERN)) 
            return "Invalid PhilHealth format (12 digits).";
        
        if (emp.getPagIbigNumber() == null || !emp.getPagIbigNumber().matches(PH_PAGIBIG_PATTERN)) 
            return "Invalid Pag-IBIG format (12 digits).";

//////////
        if (existingList != null) {
            for (Employee existing : existingList) {
       
                if (existing.getId().equals(emp.getId())) continue;


                if (Objects.equals(existing.getSssNumber(), emp.getSssNumber())) 
                    return "Duplicate SSS Number found: " + emp.getSssNumber();
                
                if (Objects.equals(existing.getTinNumber(), emp.getTinNumber())) 
                    return "Duplicate TIN Number found.";
                
                if (Objects.equals(existing.getPhilHealthNumber(), emp.getPhilHealthNumber())) 
                    return "Duplicate PhilHealth Number found.";
                
                if (Objects.equals(existing.getPagIbigNumber(), emp.getPagIbigNumber())) 
                    return "Duplicate Pag-IBIG Number found.";
                
                if (Objects.equals(existing.getPhone(), emp.getPhone())) 
                    return "Duplicate Phone Number found.";
            }
        }

        return validateFinancials(emp);
    }

    public static String validateFinancials(Employee emp) {
        if (!isValidAmount(emp.getBasicSalary())) return "Invalid Basic Salary (must be 0 or higher).";
        if (!isValidAmount(emp.getRiceSubsidy())) return "Invalid Rice Subsidy.";
        if (!isValidAmount(emp.getPhoneAllowance())) return "Invalid Phone Allowance.";
        if (!isValidAmount(emp.getClothingAllowance())) return "Invalid Clothing Allowance.";
        if (!isValidAmount(emp.getGrossSemiMonthlyRate())) return "Invalid Semi-Monthly Rate.";
        if (!isValidAmount(emp.getHourlyRate())) return "Invalid Hourly Rate.";
        
        return null; 
    }

    private static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }
}