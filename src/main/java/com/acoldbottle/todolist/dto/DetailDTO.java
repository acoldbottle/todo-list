package com.acoldbottle.todolist.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailDTO {
    private Long detailId;
    private String description;
    @JsonProperty("is_completed")
    private Boolean isCompleted;
    @JsonIgnore
    private Long categoryId;

    public DetailDTO(String description, boolean isCompleted, Long categoryId) {
        this.description = description;
        this.isCompleted = isCompleted;
        this.categoryId = categoryId;
    }
}
