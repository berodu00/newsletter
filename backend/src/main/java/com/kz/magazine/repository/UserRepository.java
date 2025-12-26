package com.kz.magazine.repository;

import com.kz.magazine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT DISTINCT u.department FROM User u WHERE u.department IS NOT NULL")
    List<String> findDistinctDepartments();

    long countByDepartmentAndDeletedAtIsNull(String department);
}
