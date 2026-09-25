package fr.formation.springdata.projection;

import java.math.BigDecimal;

public interface EmployeeSummary {
    Long getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    BigDecimal getSalary();
    String getDepartmentName();
}
