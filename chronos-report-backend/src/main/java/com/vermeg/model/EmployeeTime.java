package com.vermeg.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime; // Ajout de l'import pour gérer l'heure du CSV

@Entity
@Table(name = "employee_time")
public class EmployeeTime {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "elapsed_time")
    private Double elapsedTime;

    @Column(name = "man_day")
    private Double manDay;

    @Column(name = "site")
    private String site;

    @Column(name = "status")
    private String status;

    @Column(name = "validator_id")
    private String validatorId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "price_increase_reason")
    private String priceIncreaseReason;

    @Column(name = "creation_date")
    private LocalDateTime creationDate; // Modifié en LocalDateTime

    @Column(name = "creator_user_id")
    private String creatorUserId;

    @Column(name = "update_date")
    private LocalDateTime updateDate; // Modifié en LocalDateTime

    @Column(name = "updator_user_id")
    private String updatorUserId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "organizational_unit_id")
    private OrganizationalUnit organizationalUnit;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getElapsedTime() { return elapsedTime; }
    public void setElapsedTime(Double elapsedTime) { this.elapsedTime = elapsedTime; }

    public Double getManDay() { return manDay; }
    public void setManDay(Double manDay) { this.manDay = manDay; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getValidatorId() { return validatorId; }
    public void setValidatorId(String validatorId) { this.validatorId = validatorId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getPriceIncreaseReason() { return priceIncreaseReason; }
    public void setPriceIncreaseReason(String priceIncreaseReason) { this.priceIncreaseReason = priceIncreaseReason; }

    public LocalDateTime getCreationDate() { return creationDate; } // Mis à jour
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; } // Mis à jour

    public String getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(String creatorUserId) { this.creatorUserId = creatorUserId; }

    public LocalDateTime getUpdateDate() { return updateDate; } // Mis à jour
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; } // Mis à jour

    public String getUpdatorUserId() { return updatorUserId; }
    public void setUpdatorUserId(String updatorUserId) { this.updatorUserId = updatorUserId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    public OrganizationalUnit getOrganizationalUnit() { return organizationalUnit; }
    public void setOrganizationalUnit(OrganizationalUnit organizationalUnit) { this.organizationalUnit = organizationalUnit; }
}