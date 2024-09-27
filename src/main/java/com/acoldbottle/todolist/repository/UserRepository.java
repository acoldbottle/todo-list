package com.acoldbottle.todolist.repository;

import com.acoldbottle.todolist.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 리포지토리
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
