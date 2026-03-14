package model;

import RBAC.Role;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public abstract class Employee {

    private final String id;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String department = "";

    private String address = "";
    private String phone = "";
    private String sssNumber = "";
    private String philHealthNumber = "";
    private String tinNumber = "";
    private String pagIbigNumber = "";

    private String status = "";
    private String position = "";
    private String supervisor = "";

    private Role role;

    private BigDecimal basicSalary;
    private BigDecimal riceSubsidy;
    private BigDecimal phoneAllowance;
    private BigDecimal clothingAllowance;
    private BigDecimal grossSemiMonthlyRate;
    private BigDecimal hourlyRate;

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
        this.id = requireNotBlank(id, "Employee ID is required.");
        setFirstName(firstName);
        setLastName(lastName);
        setBirthDate(birthDate);
        setBasicSalary(basicSalary);
        setRiceSubsidy(riceSubsidy);
        setPhoneAllowance(phoneAllowance);
        setClothingAllowance(clothingAllowance);
        setGrossSemiMonthlyRate(grossSemiMonthlyRate);
        setHourlyRate(hourlyRate);
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = requireNotBlank(firstName, "First name is required.");
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = requireNotBlank(lastName, "Last name is required.");
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date is required.");
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = safeTrim(department);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = safeTrim(address);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        String value = requireNotBlank(phone, "Phone number is required.");
        if (!value.matches("\\d{3}-\\d{3}-\\d{4}|\\d{11}|\\d{3}-\\d{3}-\\d{3}")) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }
        this.phone = value;
    }

    public String getSssNumber() {
        return sssNumber;
    }

    public void setSssNumber(String sssNumber) {
        String value = requireNotBlank(sssNumber, "SSS number is required.");
        if (!value.matches("\\d{2}-\\d{7}-\\d")) {
            throw new IllegalArgumentException("Invalid SSS number format.");
        }
        this.sssNumber = value;
    }

    public String getPhilHealthNumber() {
        return philHealthNumber;
    }

    public void setPhilHealthNumber(String philHealthNumber) {
        String value = requireNotBlank(philHealthNumber, "PhilHealth number is required.");
        if (!value.matches("\\d{12}")) {
            throw new IllegalArgumentException("Invalid PhilHealth number format.");
        }
        this.philHealthNumber = value;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        String value = requireNotBlank(tinNumber, "TIN number is required.");
        if (!value.matches("\\d{3}-\\d{3}-\\d{3}-\\d{1,4}")) {
            throw new IllegalArgumentException("Invalid TIN number format.");
        }
        this.tinNumber = value;
    }

    public String getPagIbigNumber() {
        return pagIbigNumber;
    }

    public void setPagIbigNumber(String pagIbigNumber) {
        String value = requireNotBlank(pagIbigNumber, "Pag-IBIG number is required.");
        if (!value.matches("\\d{12}")) {
            throw new IllegalArgumentException("Invalid Pag-IBIG number format.");
        }
        this.pagIbigNumber = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = requireNotBlank(status, "Status is required.");
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = requireNotBlank(position, "Position is required.");
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = requireNotBlank(supervisor, "Supervisor is required.");
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "Role is required.");
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = requireNonNegative(basicSalary, "Basic salary");
    }

    public BigDecimal getRiceSubsidy() {
        return riceSubsidy;
    }

    public void setRiceSubsidy(BigDecimal riceSubsidy) {
        this.riceSubsidy = requireNonNegative(riceSubsidy, "Rice subsidy");
    }

    public BigDecimal getPhoneAllowance() {
        return phoneAllowance;
    }

    public void setPhoneAllowance(BigDecimal phoneAllowance) {
        this.phoneAllowance = requireNonNegative(phoneAllowance, "Phone allowance");
    }

    public BigDecimal getClothingAllowance() {
        return clothingAllowance;
    }

    public void setClothingAllowance(BigDecimal clothingAllowance) {
        this.clothingAllowance = requireNonNegative(clothingAllowance, "Clothing allowance");
    }

    public BigDecimal getGrossSemiMonthlyRate() {
        return grossSemiMonthlyRate;
    }

    public void setGrossSemiMonthlyRate(BigDecimal grossSemiMonthlyRate) {
        this.grossSemiMonthlyRate = requireNonNegative(grossSemiMonthlyRate, "Gross semi-monthly rate");
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = requireNonNegative(hourlyRate, "Hourly rate");
    }

    public abstract BigDecimal calculateSalary();

    public String getEmployeeType() {
        return "Employee";
    }

    @Override
    public String toString() {
        return "Employee[id=" + id + ", name=" + firstName + " " + lastName + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private String requireNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal requireNonNegative(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required.");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
        return value;
    }
}