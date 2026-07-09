package com.vermeg.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "project_manager")
    private String projectManager;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "billing_entity_id")
    private BillingEntity billingEntity;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProjectManager() { return projectManager; }
    public void setProjectManager(String projectManager) { this.projectManager = projectManager; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public BillingEntity getBillingEntity() { return billingEntity; }
    public void setBillingEntity(BillingEntity billingEntity) { this.billingEntity = billingEntity; }
}