package com.vermeg.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "company_member")
public class CompanyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number")
    private Long registrationNumber;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(Long registrationNumber) { this.registrationNumber = registrationNumber; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
}