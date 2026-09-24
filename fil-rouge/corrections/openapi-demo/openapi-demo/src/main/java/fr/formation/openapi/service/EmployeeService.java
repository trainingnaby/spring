package fr.formation.openapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import fr.formation.openapi.dto.EmployeeRequest;
import fr.formation.openapi.dto.EmployeeResponse;
import fr.formation.openapi.exception.EmployeeNotFoundException;

@Service
public class EmployeeService {
    private final List<EmployeeResponse> employees = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(2);

    public EmployeeService() {
        employees.add(new EmployeeResponse(1L, "Sophie", "Martin", "sophie.martin@entreprise.fr", "Informatique"));
        employees.add(new EmployeeResponse(2L, "Karim", "Benali", "karim.benali@entreprise.fr", "Finance"));
    }

    public List<EmployeeResponse> findAll(String department) {
        if (department == null || department.isBlank()) return List.copyOf(employees);
        return employees.stream().filter(e -> e.department().equalsIgnoreCase(department)).toList();
    }

    public EmployeeResponse findById(Long id) {
        return employees.stream().filter(e -> e.id().equals(id)).findFirst().orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public EmployeeResponse create(EmployeeRequest request) {
        var employee = new EmployeeResponse(sequence.incrementAndGet(), request.firstName(), request.lastName(), request.email(), request.department());
        employees.add(employee); return employee;
    }

    public EmployeeResponse update(Long id, EmployeeRequest request) {
        findById(id);
        var updated = new EmployeeResponse(id, request.firstName(), request.lastName(), request.email(), request.department());
        employees.replaceAll(e -> e.id().equals(id) ? updated : e); return updated;
    }

    public void delete(Long id) { findById(id); employees.removeIf(e -> e.id().equals(id)); }
}
