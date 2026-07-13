package com.vermeg.entity.employeebyactivitynature;
import com.vermeg.entity.activitynature.ActivityNature;
import com.vermeg.entity.employee.Employee;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee_by_activity_nature")
public class EmployeeByActivityNature {

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
    @JoinColumn(name = "activity_nature_id")
    private ActivityNature activityNature;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public ActivityNature getActivityNature() { return activityNature; }
    public void setActivityNature(ActivityNature activityNature) { this.activityNature = activityNature; }
}
