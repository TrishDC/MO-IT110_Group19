package com.motorph.employeeapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an employee and their compensation/details.
 */
public class Employee {
    private final String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    // contact & government IDs
    private String address = "";
    private String phone = "";
    private String sssNumber = "";
    private String philHealthNumber = "";
    private String tinNumber = "";
    private String pagIbigNumber = "";

    // employment info
    private String status = "";        // e.g. "Regular"
    private String position = "";
    private String supervisor = "";

    // salary fields
    private BigDecimal basicSalary;
    private BigDecimal riceSubsidy;
    private BigDecimal phoneAllowance;
    private BigDecimal clothingAllowance;
    private BigDecimal grossSemiMonthlyRate;
    private BigDecimal hourlyRate;

    /**
     * Primary constructor (used by repository for allowances-only).
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
        this.id             = Objects.requireNonNull(id,         "ID must not be null");
        setFirstName(firstName);
        setLastName(lastName);
        this.birthDate      = Objects.requireNonNull(birthDate,  "Birth date must not be null");
        this.basicSalary    = Objects.requireNonNull(basicSalary,"Basic salary must not be null");
        this.riceSubsidy    = Objects.requireNonNull(riceSubsidy,"Rice subsidy must not be null");
        this.phoneAllowance = Objects.requireNonNull(phoneAllowance,"Phone allowance must not be null");
        this.clothingAllowance = Objects.requireNonNull(clothingAllowance,"Clothing allowance must not be null");
        this.grossSemiMonthlyRate = Objects.requireNonNull(grossSemiMonthlyRate,"Semi-monthly rate must not be null");
        this.hourlyRate     = Objects.requireNonNull(hourlyRate, "Hourly rate must not be null");
    }

    /**
     * Convenience for when only personal info is known.
     */
    public Employee(
        String id,
        String firstName,
        String lastName,
        LocalDate birthDate
    ) {
        this(id, firstName, lastName, birthDate,
             BigDecimal.ZERO, BigDecimal.ZERO,
             BigDecimal.ZERO, BigDecimal.ZERO,
             BigDecimal.ZERO, BigDecimal.ZERO);
    }

    // --- ID & Name & Date ---
    public String getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First name cannot be empty");
        this.firstName = firstName.trim();
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Last name cannot be empty");
        this.lastName = lastName.trim();
    }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date must not be null");
    }

    // --- Contact & IDs ---
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address == null ? "" : address.trim(); }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone == null ? "" : phone.trim(); }

    public String getSssNumber() { return sssNumber; }
    public void setSssNumber(String sssNumber) { this.sssNumber = sssNumber == null ? "" : sssNumber.trim(); }

    public String getPhilHealthNumber() { return philHealthNumber; }
    public void setPhilHealthNumber(String philHealthNumber) {
        this.philHealthNumber = philHealthNumber == null ? "" : philHealthNumber.trim();
    }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber == null ? "" : tinNumber.trim(); }

    public String getPagIbigNumber() { return pagIbigNumber; }
    public void setPagIbigNumber(String pagIbigNumber) {
        this.pagIbigNumber = pagIbigNumber == null ? "" : pagIbigNumber.trim();
    }

    // --- Employment info ---
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status == null ? "" : status.trim(); }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position == null ? "" : position.trim(); }

    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor == null ? "" : supervisor.trim();
    }

    // --- Salary fields ---
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = Objects.requireNonNull(basicSalary, "Basic salary must not be null");
    }

    public BigDecimal getRiceSubsidy() { return riceSubsidy; }
    public void setRiceSubsidy(BigDecimal riceSubsidy) {
        this.riceSubsidy = Objects.requireNonNull(riceSubsidy, "Rice subsidy must not be null");
    }

    public BigDecimal getPhoneAllowance() { return phoneAllowance; }
    public void setPhoneAllowance(BigDecimal phoneAllowance) {
        this.phoneAllowance = Objects.requireNonNull(phoneAllowance, "Phone allowance must not be null");
    }

    public BigDecimal getClothingAllowance() { return clothingAllowance; }
    public void setClothingAllowance(BigDecimal clothingAllowance) {
        this.clothingAllowance = Objects.requireNonNull(clothingAllowance, "Clothing allowance must not be null");
    }

    public BigDecimal getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public void setGrossSemiMonthlyRate(BigDecimal rate) {
        this.grossSemiMonthlyRate = Objects.requireNonNull(rate, "Semi-monthly rate must not be null");
    }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = Objects.requireNonNull(hourlyRate, "Hourly rate must not be null");
    }

    @Override
    public String toString() {
        return String.format(
            "Employee[id=%s, name=%s %s, birthDate=%s]",
            id, firstName, lastName, birthDate
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        return id.equals(((Employee) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
