package com.acoldbottle.todolist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상세 할일 클래스 -> ex) 유산소(운동), 웨이트(운동), 토익(공부), 영단어 외우기(공부) 와 같은 상세 내용. detail(category)
 *
 * description = 상세 할일 내용
 * isCompleted = 완료 여부
 * todoCategory = 카테고리
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String description;

    private boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private TodoCategory todoCategory;

    @Builder
    public TodoDetail(String description, TodoCategory todoCategory) {
        this.description = description;
        this.todoCategory = todoCategory;
    }

    public void addDescription(String description) {
        this.description = description;
    }

    public void updateCompleteStatus(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}

