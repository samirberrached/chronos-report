package com.vermeg.entity.phase;
import com.vermeg.entity.accountingcode.AccountingCode;
import com.vermeg.entity.iteration.Iteration;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "phase")
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "delivrable_name")
    private String delivrableName;

    @Column(name = "is_capitalizable")
    private Boolean isCapitalizable;

    @Column(name = "capitalizable_date")
    private LocalDate capitalizableDate;

    @Column(name = "is_capitalizable_by")
    private String isCapitalizableBy;

    @ManyToOne
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    @ManyToOne
    @JoinColumn(name = "iteration_id")
    private Iteration iteration;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDelivrableName() { return delivrableName; }
    public void setDelivrableName(String delivrableName) { this.delivrableName = delivrableName; }
    public Boolean getIsCapitalizable() { return isCapitalizable; }
    public void setIsCapitalizable(Boolean isCapitalizable) { this.isCapitalizable = isCapitalizable; }
    public LocalDate getCapitalizableDate() { return capitalizableDate; }
    public void setCapitalizableDate(LocalDate capitalizableDate) { this.capitalizableDate = capitalizableDate; }
    public String getIsCapitalizableBy() { return isCapitalizableBy; }
    public void setIsCapitalizableBy(String isCapitalizableBy) { this.isCapitalizableBy = isCapitalizableBy; }
    public AccountingCode getAccountingCode() { return accountingCode; }
    public void setAccountingCode(AccountingCode accountingCode) { this.accountingCode = accountingCode; }
    public Iteration getIteration() { return iteration; }
    public void setIteration(Iteration iteration) { this.iteration = iteration; }
}
