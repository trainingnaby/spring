package fr.formation.springdata.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.formation.springdata.domain.Employee;
import fr.formation.springdata.dto.EmployeeSalaryDto;
import fr.formation.springdata.projection.EmployeeSummary;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	// Les entités restent internes au repository. Les contrôleurs utilisent des
	// projections/DTO.

	// Projection interface + requêtes dérivées à partir du nom de méthode.
	List<EmployeeSummary> findAllProjectedBy();

	List<EmployeeSummary> findByLastNameIgnoreCase(String lastName);

	List<EmployeeSummary> findByActiveTrueOrderByLastNameAsc();

	List<EmployeeSummary> findBySalaryGreaterThanEqual(BigDecimal minimumSalary);

	List<EmployeeSummary> findByDepartmentName(String departmentName);

	// Pagination : le contenu de la Page est lui aussi une projection.
	Page<EmployeeSummary> findByActive(boolean active, Pageable pageable);

	// JPQL + projection interface. Les alias correspondent aux propriétés de la
	// projection.
	@Query("""
			select e.id as id,
			       e.firstName as firstName,
			       e.lastName as lastName,
			       e.email as email,
			       e.salary as salary,
			       e.department.name as departmentName
			from Employee e
			where e.department.name = :department
			  and e.salary >= :minimumSalary
			order by e.lastName, e.firstName
			""")
	List<EmployeeSummary> findWellPaidEmployees(@Param("department") String department,
			@Param("minimumSalary") BigDecimal minimumSalary);

	// Projection DTO (record) avec expression constructeur JPQL.
	@Query("""
			select new fr.formation.springdata.dto.EmployeeSalaryDto(
			    e.firstName, e.lastName, e.salary)
			from Employee e
			where e.active = true
			order by e.salary desc
			""")
	List<EmployeeSalaryDto> findActiveEmployeeSalaries();

	// SQL natif + projection interface : on sélectionne explicitement les colonnes
	// utiles.
	@Query(value = """
			select e.id as id,
			       e.first_name as firstName,
			       e.last_name as lastName,
			       e.email as email,
			       e.salary as salary,
			       d.name as departmentName
			from employee e
			join department d on d.id = e.department_id
			where lower(e.email) like lower(concat('%', :domain))
			order by e.last_name
			""", nativeQuery = true)
	List<EmployeeSummary> findByEmailDomainNative(@Param("domain") String domain);

	// Tri dynamique tout en gardant une projection en sortie.
	List<EmployeeSummary> findAllProjectedBy(Sort sort);
}
