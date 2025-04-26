package org.example.repository;

import org.example.entity.Task;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUser(User user);

    Optional<Task> findByIdAndUser(Long id, User user);
}

