package com.vermeg.entity.organizationalunit;

import jakarta.persistence.*;

@Entity
@Table(name = "organizational_unit")
public class OrganizationalUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private OrganizationalUnit parent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public OrganizationalUnit getParent() { return parent; }
    public void setParent(OrganizationalUnit parent) { this.parent = parent; }
}