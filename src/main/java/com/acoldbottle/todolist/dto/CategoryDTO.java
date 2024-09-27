package com.acoldbottle.todolist.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class CategoryDTO {
    private Long categoryId;
    private String title;
    @JsonIgnore
    private Long userId; // 카테고리를 생성한 사용자 ID
    private LocalDate dueDate;
    @JsonProperty("detail")
    private List<DetailDTO> detailDTOList;

    public CategoryDTO() {

    }
}
