package com.vermeg.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "organizational_unit_member")
public class OrganizationalUnitMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "organizational_unit_id")
    private OrganizationalUnit organizationalUnit;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public OrganizationalUnit getOrganizationalUnit() { return organizationalUnit; }
    public void setOrganizationalUnit(OrganizationalUnit ou) { this.organizationalUnit = ou; }
}