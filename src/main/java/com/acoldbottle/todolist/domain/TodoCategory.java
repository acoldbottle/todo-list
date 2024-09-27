package com.acoldbottle.todolist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 할일의 카테고리 클래스 -> ex ) 운동, 공부, 휴식과 같은 큰 범위
 *
 * title = 운동, 공부, 휴식
 * user = 사용자
 * dueDate = 마감일
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate dueDate;


    @Builder
    public TodoCategory(String title, User user, LocalDate dueDate) {
        this.title = title;
        this.user = user;
        this.dueDate = dueDate;
    }
}
