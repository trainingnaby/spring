package fr.formation.springdata.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.formation.springdata.dto.EmployeeSalaryDto;
import fr.formation.springdata.projection.EmployeeSummary;
import fr.formation.springdata.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EmployeeSummary> all() {
        return repository.findAllProjectedBy();
    }

    @GetMapping("/by-last-name")
    public List<EmployeeSummary> byLastName(@RequestParam String name) {
        return repository.findByLastNameIgnoreCase(name);
    }

    @GetMapping("/active")
    public List<EmployeeSummary> active() {
        return repository.findByActiveTrueOrderByLastNameAsc();
    }

    @GetMapping("/salaries")
    public List<EmployeeSalaryDto> salaries() {
        return repository.findActiveEmployeeSalaries();
    }

    @GetMapping("/jpql")
    public List<EmployeeSummary> jpql(@RequestParam String department,
                                      @RequestParam BigDecimal min) {
        return repository.findWellPaidEmployees(department, min);
    }

    @GetMapping("/native")
    public List<EmployeeSummary> nativeSql(@RequestParam String domain) {
        return repository.findByEmailDomainNative(domain);
    }

    @GetMapping("/page")
    public Page<EmployeeSummary> page(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "2") int size,
                                      @RequestParam(defaultValue = "lastName") String sort) {
        return repository.findByActive(
                true,
                PageRequest.of(page, size, Sort.by(sort).ascending()));
    }

    @GetMapping("/sorted")
    public List<EmployeeSummary> sorted() {
        return repository.findAllProjectedBy(
                Sort.by(
                    Sort.Order.asc("department.name"),
                    Sort.Order.desc("salary")));
    }
}
