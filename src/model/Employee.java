package model;

import RBAC.Role;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.List;
import repository.EmployeeValidator;

/**
 * Represents an employee and their compensation/details.
 */
public abstract class Employee implements IEmployee {

    private final String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    // contact & government IDs
    private String address = "";
    private String Phone = "";
    private String SssNumber = "";
    private String PhilHealthNumber = "";

    private String TinNumber = "";
    private String PagIbigNumber = "";

    // employment info
    private String status = "";        // e.g. "Regular"
    private String position = "";
    private String supervisor = "";
    private Role role;

    // salary fields
    private BigDecimal basicSalary;
    private BigDecimal riceSubsidy;
    private BigDecimal phoneAllowance;
    private BigDecimal clothingAllowance;
    private BigDecimal grossSemiMonthlyRate;
    private BigDecimal hourlyRate;

    /**
     * Primary constructor.
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param birthDate
     * @param basicSalary
     * @param riceSubsidy
     * @param phoneAllowance
     * @param clothingAllowance
     * @param grossSemiMonthlyRate
     * @param hourlyRate
     */
    public Employee(
            String id,
            String firstName,
            String lastName,
            LocalDate birthDate,
            BigDecimal basicSalary,
            BigDecimal riceSubsidy,
            BigDecimal phoneAllowance,
            BigDecimal clothingAllowance,
            BigDecimal grossSemiMonthlyRate,
            BigDecimal hourlyRate
    ) {
        this.id = Objects.requireNonNull(id, "ID must not be null");
        setFirstName(firstName);
        setLastName(lastName);
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date must not be null");
        this.basicSalary = Objects.requireNonNull(basicSalary, "Basic salary must not be null");
        this.riceSubsidy = Objects.requireNonNull(riceSubsidy, "Rice subsidy must not be null");
        this.phoneAllowance = Objects.requireNonNull(phoneAllowance, "Phone allowance must not be null");
        this.clothingAllowance = Objects.requireNonNull(clothingAllowance, "Clothing allowance must not be null");
        this.grossSemiMonthlyRate = Objects.requireNonNull(grossSemiMonthlyRate, "Semi-monthly rate must not be null");
        this.hourlyRate = Objects.requireNonNull(hourlyRate, "Hourly rate must not be null");
    }

    public Employee(String id, String firstName, String lastName, LocalDate birthDate) {
        this(id, firstName, lastName, birthDate,
                BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public String validate(List<Employee> existingEmployees) {
        return EmployeeValidator.validateAll(this, existingEmployees);
    }

    // Getters
    public String getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
        public String getLastName() {
        return lastName;
    }
            public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public String getAddress() {
        return address;
    }
        
    public String getPhone() {
        return Phone;
    }
    
    public String getSssNumber() {
        return SssNumber;
    }
    
    public String getPhilHealthNumber() {
        return PhilHealthNumber;
    }
    
    
    public String getPagIbigNumber() {
        return PagIbigNumber;
    }
    
    public String getTinNumber() {
        return TinNumber;
    }
        
    public Role getRole() {
        return role;
    }
    
     public String getStatus() {
        return status;
    }
    public String getPosition() {
        return position;
    }
     
    public String getSupervisor() {
        return supervisor;
    }
    
    public BigDecimal getBasicSalary() {
        return basicSalary;
    }
    public BigDecimal getRiceSubsidy() {
        return riceSubsidy;
    }
    public BigDecimal getPhoneAllowance() {
        return phoneAllowance;
    }   
    
    public BigDecimal getClothingAllowance() {
        return clothingAllowance;
    }
    public BigDecimal getGrossSemiMonthlyRate() {
        return grossSemiMonthlyRate;
    }
    
    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
    
   public String getEmployeeType() {
        return "Employee";
    }

   
   ///setters
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        this.firstName = firstName.trim();
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        this.lastName = lastName.trim();
    }



    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date must not be null");
    }


    public void setAddress(String address) {
        this.address = address == null ? "" : address.trim();
    }



    public void setPhone(String Phone) {
        this.Phone = Phone == null ? "" : Phone.trim();
    }
  
 

    public void setSssNumber(String sssNumber) {
        this.SssNumber = sssNumber == null ? "" : sssNumber.trim();
    }



    public void setPhilHealthNumber(String philHealthNumber) {
        this.PhilHealthNumber = philHealthNumber == null ? "" : philHealthNumber.trim();
    }

  

    public void setTinNumber(String tinNumber) {
        this.TinNumber = tinNumber == null ? "" : tinNumber.trim();
    }

  

    public void setPagIbigNumber(String pagIbigNumber) {
        this.PagIbigNumber = pagIbigNumber == null ? "" : pagIbigNumber.trim();
    }

 

    public void setRole(Role role) {
        this.role = role;
    }



    public void setStatus(String status) {
        this.status = status == null ? "" : status.trim();
    }


    public void setPosition(String position) {
        this.position = position == null ? "" : position.trim();
    }


    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor == null ? "" : supervisor.trim();
    }

    

    
    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = Objects.requireNonNull(basicSalary, "Basic salary must not be null");
    }

  

    public void setRiceSubsidy(BigDecimal riceSubsidy) {
        this.riceSubsidy = Objects.requireNonNull(riceSubsidy, "Rice subsidy must not be null");
    }

  

    public void setPhoneAllowance(BigDecimal phoneAllowance) {
        this.phoneAllowance = Objects.requireNonNull(phoneAllowance, "Phone allowance must not be null");
    }



    public void setClothingAllowance(BigDecimal clothingAllowance) {
        this.clothingAllowance = Objects.requireNonNull(clothingAllowance, "Clothing allowance must not be null");
    }



    public void setGrossSemiMonthlyRate(BigDecimal rate) {
        this.grossSemiMonthlyRate = Objects.requireNonNull(rate, "Semi-monthly rate must not be null");
    }



    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = Objects.requireNonNull(hourlyRate, "Hourly rate must not be null");
    }

    
    //abstract class 
    public abstract BigDecimal calculateSalary();


    
    

    
    @Override
    public String toString() {
        return String.format("Employee[id=%s, name=%s %s, birthDate=%s]", id, firstName, lastName, birthDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        return id.equals(((Employee) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
