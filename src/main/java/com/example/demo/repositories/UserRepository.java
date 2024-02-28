package com.example.demo.repositories;

import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(nativeQuery = true, value="SELECT * FROM \"user\" WHERE email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query(nativeQuery = true, value="SELECT email FROM \"user\" WHERE role = CAST(:role AS \"userRole\")")
    String[] findEmailsByRole(@Param("role") String role);
}
