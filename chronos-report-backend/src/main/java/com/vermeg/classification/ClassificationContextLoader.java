package com.vermeg.classification;

import com.vermeg.entity.employeebyactivitynature.EmployeeByActivityNatureRepository;
import com.vermeg.entity.employeebyproduct.EmployeeByProductRepository;
import com.vermeg.entity.employeetime.EmployeeTimeProjection;
import com.vermeg.entity.employeetime.EmployeeTimeRepository;
import com.vermeg.entity.organizationalassignment.OrganizationalAssignmentRepository;
import com.vermeg.entity.organizationalunitmember.OrganizationalUnitMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Charge en une poignée de requêtes toutes les données de référence nécessaires à la
 * classification d'un mois, au lieu d'une requête par employé.
 */
@Service
public class ClassificationContextLoader {

    @Autowired private OrganizationalAssignmentRepository organizationalAssignmentRepository;
    @Autowired private OrganizationalUnitMemberRepository organizationalUnitMemberRepository;
    @Autowired private EmployeeByProductRepository employeeByProductRepository;
    @Autowired private EmployeeByActivityNatureRepository employeeByActivityNatureRepository;
    @Autowired private EmployeeTimeRepository employeeTimeRepository;

    public ClassificationContext load(LocalDate monthStart, LocalDate monthEnd) {
        return new ClassificationContext(
                groupByEmployeeId(organizationalAssignmentRepository.findAllWithAssociations(), oa -> oa.getEmployee().getId()),
                groupByEmployeeId(organizationalUnitMemberRepository.findAllWithAssociations(), oum -> oum.getEmployee().getId()),
                groupByEmployeeId(employeeByProductRepository.findAllWithAssociations(), ebp -> ebp.getEmployee().getId()),
                groupByEmployeeId(employeeByActivityNatureRepository.findAllWithAssociations(), ean -> ean.getEmployee().getId()),
                groupByEmployeeId(employeeTimeRepository.findProjectionsByDateBetween(monthStart, monthEnd),
                        EmployeeTimeProjection::employeeId)
        );
    }

    private <T> Map<Long, List<T>> groupByEmployeeId(List<T> items, Function<T, Long> employeeIdExtractor) {
        return items.stream().collect(Collectors.groupingBy(employeeIdExtractor));
    }
}
