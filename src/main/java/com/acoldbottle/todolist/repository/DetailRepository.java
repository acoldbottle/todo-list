package com.acoldbottle.todolist.repository;

import com.acoldbottle.todolist.domain.TodoCategory;
import com.acoldbottle.todolist.domain.TodoDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * TodoDetail 리포지토리
 */
public interface DetailRepository extends JpaRepository<TodoDetail, Long> {

    List<TodoDetail> findByTodoCategory(TodoCategory todoCategory);

    void deleteByTodoCategory(TodoCategory todoCategory);
}
