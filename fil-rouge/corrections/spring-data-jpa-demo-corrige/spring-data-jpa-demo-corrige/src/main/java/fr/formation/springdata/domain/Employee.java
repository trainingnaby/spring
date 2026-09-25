package fr.formation.springdata.domain;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String firstName;
	private String lastName;
	@Column(unique = true, nullable = false)
	private String email;
	private BigDecimal salary;
	private boolean active;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Department department;

	protected Employee() {
	}

	public Employee(String firstName, String lastName, String email, BigDecimal salary, boolean active,
			Department department) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.salary = salary;
		this.active = active;
		this.department = department;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public boolean isActive() {
		return active;
	}

	public Department getDepartment() {
		return department;
	}
}
