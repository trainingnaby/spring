package fr.formation.exceptions.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import fr.formation.exceptions.domain.Customer;
import fr.formation.exceptions.dto.CreateCustomerRequest;
import fr.formation.exceptions.exception.CustomerNotFoundException;
import fr.formation.exceptions.exception.EmailAlreadyUsedException;

@Service
public class CustomerService {
    private final List<Customer> customers = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(3);

    public CustomerService() {
        customers.add(new Customer(1L, "Sophie Martin", "sophie.martin@entreprise.fr"));
        customers.add(new Customer(2L, "Karim Benali", "karim.benali@entreprise.fr"));
        customers.add(new Customer(3L, "Claire Dubois", "claire.dubois@entreprise.fr"));
    }

    public List<Customer> findAll() {
        return List.copyOf(customers);
    }

    public Customer findById(Long id) {
        return customers.stream()
                .filter(customer -> customer.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer create(CreateCustomerRequest request) {
        boolean exists = customers.stream()
                .anyMatch(customer -> customer.email().equalsIgnoreCase(request.email()));
        if (exists) {
            throw new EmailAlreadyUsedException(request.email());
        }
        Customer customer = new Customer(sequence.incrementAndGet(), request.name(), request.email());
        customers.add(customer);
        return customer;
    }
}
