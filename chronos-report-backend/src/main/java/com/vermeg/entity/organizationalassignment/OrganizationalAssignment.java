package com.vermeg.entity.organizationalassignment;
import com.vermeg.entity.accountingcode.AccountingCode;
import com.vermeg.entity.employee.Employee;
import com.vermeg.entity.organizationalunit.OrganizationalUnit;
import com.vermeg.entity.product.Product;


import jakarta.persistence.*;

@Entity
@Table(name = "organizational_assignment")
public class OrganizationalAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "allocation_percentage")
    private Double allocationPercentage;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "organizational_unit_id")
    private OrganizationalUnit organizationalUnit;

    @ManyToOne
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getAllocationPercentage() { return allocationPercentage; }
    public void setAllocationPercentage(Double allocationPercentage) { this.allocationPercentage = allocationPercentage; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public OrganizationalUnit getOrganizationalUnit() { return organizationalUnit; }
    public void setOrganizationalUnit(OrganizationalUnit organizationalUnit) { this.organizationalUnit = organizationalUnit; }
    public AccountingCode getAccountingCode() { return accountingCode; }
    public void setAccountingCode(AccountingCode accountingCode) { this.accountingCode = accountingCode; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
