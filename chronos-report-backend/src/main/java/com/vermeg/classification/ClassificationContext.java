package com.vermeg.classification;

import com.vermeg.entity.employeebyactivitynature.EmployeeByActivityNature;
import com.vermeg.entity.employeebyproduct.EmployeeByProduct;
import com.vermeg.entity.employeetime.EmployeeTimeProjection;
import com.vermeg.entity.organizationalassignment.OrganizationalAssignment;
import com.vermeg.entity.organizationalunitmember.OrganizationalUnitMember;

import java.util.List;
import java.util.Map;

/**
 * Données de référence pré-chargées en mémoire pour un mois donné, indexées par employé.
 * Évite une requête SQL par employé lors de la classification (nécessaire pour un usage
 * interactif depuis une interface admin).
 */
public record ClassificationContext(
        Map<Long, List<OrganizationalAssignment>> assignmentsByEmployeeId,
        Map<Long, List<OrganizationalUnitMember>> organizationalUnitMembersByEmployeeId,
        Map<Long, List<EmployeeByProduct>> productsByEmployeeId,
        Map<Long, List<EmployeeByActivityNature>> activityNaturesByEmployeeId,
        Map<Long, List<EmployeeTimeProjection>> timesheetsByEmployeeId
) {
    public List<OrganizationalAssignment> assignmentsFor(Long employeeId) {
        return assignmentsByEmployeeId.getOrDefault(employeeId, List.of());
    }

    public List<OrganizationalUnitMember> organizationalUnitMembersFor(Long employeeId) {
        return organizationalUnitMembersByEmployeeId.getOrDefault(employeeId, List.of());
    }

    public List<EmployeeByProduct> productsFor(Long employeeId) {
        return productsByEmployeeId.getOrDefault(employeeId, List.of());
    }

    public List<EmployeeByActivityNature> activityNaturesFor(Long employeeId) {
        return activityNaturesByEmployeeId.getOrDefault(employeeId, List.of());
    }

    public List<EmployeeTimeProjection> timesheetsFor(Long employeeId) {
        return timesheetsByEmployeeId.getOrDefault(employeeId, List.of());
    }
}
