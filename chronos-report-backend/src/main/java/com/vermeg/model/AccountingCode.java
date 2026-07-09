package com.vermeg.model;

import jakarta.persistence.*;

@Entity
@Table(name = "accounting_code")
public class AccountingCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operational_identifier", unique = true, nullable = false)
    private String operationalIdentifier;

    @Column(name = "billing_mode")
    private String billingMode;

    @Column(name = "billable")
    private Boolean billable;

    @ManyToOne
    @JoinColumn(name = "organizational_unit_id")
    private OrganizationalUnit organizationalUnit;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "activity_nature_id")
    private ActivityNature activityNature;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOperationalIdentifier() { return operationalIdentifier; }
    public void setOperationalIdentifier(String operationalIdentifier) { this.operationalIdentifier = operationalIdentifier; }
    public String getBillingMode() { return billingMode; }
    public void setBillingMode(String billingMode) { this.billingMode = billingMode; }
    public Boolean getBillable() { return billable; }
    public void setBillable(Boolean billable) { this.billable = billable; }
    public OrganizationalUnit getOrganizationalUnit() { return organizationalUnit; }
    public void setOrganizationalUnit(OrganizationalUnit organizationalUnit) { this.organizationalUnit = organizationalUnit; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public ActivityNature getActivityNature() { return activityNature; }
    public void setActivityNature(ActivityNature activityNature) { this.activityNature = activityNature; }
}