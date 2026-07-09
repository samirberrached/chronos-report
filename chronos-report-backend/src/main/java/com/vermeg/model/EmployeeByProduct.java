package com.vermeg.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee_by_product")
public class EmployeeByProduct {

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
    @JoinColumn(name = "product_id")
    private Product product;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}