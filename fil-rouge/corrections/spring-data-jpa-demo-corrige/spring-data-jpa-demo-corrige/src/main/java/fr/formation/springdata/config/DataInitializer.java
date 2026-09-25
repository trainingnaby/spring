package fr.formation.springdata.config;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import fr.formation.springdata.domain.Department;
import fr.formation.springdata.domain.Employee;
import fr.formation.springdata.repository.DepartmentRepository;
import fr.formation.springdata.repository.EmployeeRepository;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner loadData(DepartmentRepository departments, EmployeeRepository employees) {
        return args -> {
            Department it = departments.save(new Department("Informatique"));
            Department finance = departments.save(new Department("Finance"));
            Department sales = departments.save(new Department("Commercial"));

            employees.save(new Employee("Nadia", "Benali", "nadia.benali@entreprise.fr", new BigDecimal("4200"), true, it));
            employees.save(new Employee("Thomas", "Martin", "thomas.martin@entreprise.fr", new BigDecimal("3600"), true, it));
            employees.save(new Employee("Claire", "Robert", "claire.robert@entreprise.fr", new BigDecimal("5100"), true, finance));
            employees.save(new Employee("Julien", "Petit", "julien.petit@prestataire.com", new BigDecimal("3300"), false, finance));
            employees.save(new Employee("Sofia", "Durand", "sofia.durand@entreprise.fr", new BigDecimal("3900"), true, sales));
            employees.save(new Employee("Marc", "Bernard", "marc.bernard@entreprise.fr", new BigDecimal("4500"), true, sales));
        };
    }
}
