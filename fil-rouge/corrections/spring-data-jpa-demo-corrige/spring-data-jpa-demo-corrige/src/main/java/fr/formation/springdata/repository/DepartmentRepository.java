package fr.formation.springdata.repository;

import org.springframework.data.repository.CrudRepository;
import fr.formation.springdata.domain.Department;

// CrudRepository suffit ici : CRUD simple, pas de pagination nécessaire.
public interface DepartmentRepository extends CrudRepository<Department, Long> {
}
