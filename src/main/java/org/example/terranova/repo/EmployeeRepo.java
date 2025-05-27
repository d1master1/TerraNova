package org.example.terranova.repo;

import org.example.terranova.model.Employee;
import org.example.terranova.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUser(User user);
    List<Employee> findAll(Sort sort);
    Employee findByUserId(Long userId);
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user")
    List<Employee> findAllWithUser();
}
