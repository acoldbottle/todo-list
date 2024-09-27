package com.acoldbottle.todolist.repository;

import com.acoldbottle.todolist.domain.TodoCategory;
import com.acoldbottle.todolist.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * TodoCategory 리포지토리
 */
public interface CategoryRepository extends JpaRepository<TodoCategory, Long> {

    List<TodoCategory> findByUserAndDueDate(User user, LocalDate dueDate);

    Optional<TodoCategory> findByUserAndDueDateAndTitle(User user, LocalDate dueDate, String title);

}

