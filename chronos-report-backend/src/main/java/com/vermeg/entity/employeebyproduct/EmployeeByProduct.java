package com.vermeg.entity.employeebyproduct;
import com.vermeg.entity.employee.Employee;
import com.vermeg.entity.product.Product;


import jakarta.persistence.*;

@Entity
@Table(name = "employee_by_product")
public class EmployeeByProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
