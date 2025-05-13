package com.example.springbootguide.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springbootguide.models.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM public.\"user\" WHERE username = :username", nativeQuery = true)
    Optional<User> findByUsername(String username);
}
