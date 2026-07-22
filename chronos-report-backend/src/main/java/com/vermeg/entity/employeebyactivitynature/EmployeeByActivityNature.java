package com.vermeg.entity.employeebyactivitynature;
import com.vermeg.entity.activitynature.ActivityNature;
import com.vermeg.entity.employee.Employee;


import jakarta.persistence.*;

@Entity
@Table(name = "employee_by_activity_nature")
public class EmployeeByActivityNature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "activity_nature_id")
    private ActivityNature activityNature;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public ActivityNature getActivityNature() { return activityNature; }
    public void setActivityNature(ActivityNature activityNature) { this.activityNature = activityNature; }
}
