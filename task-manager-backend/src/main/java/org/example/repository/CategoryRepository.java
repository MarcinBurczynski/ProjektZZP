package org.example.repository;

import org.example.entity.Category;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUser(User user);
    List<Category> findAllByUserId(Long userId);
    Optional<Category> findByUserAndName(User user, String name);
    Optional<Category> findByUserIdAndName(Long userId, String name);
}

