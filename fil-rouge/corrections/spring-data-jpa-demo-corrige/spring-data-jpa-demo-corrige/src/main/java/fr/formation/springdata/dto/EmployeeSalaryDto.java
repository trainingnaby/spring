package fr.formation.springdata.dto;

import java.math.BigDecimal;

//// Projection DTO : un record convient bien à un résultat de lecture immuable.
public record EmployeeSalaryDto(String firstName, String lastName, BigDecimal salary) {
}
