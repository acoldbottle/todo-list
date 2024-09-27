package com.acoldbottle.todolist.controller;


import com.acoldbottle.todolist.dto.CategoryDTO;
import com.acoldbottle.todolist.dto.UserDTO;
import com.acoldbottle.todolist.service.CategoryService;
import com.acoldbottle.todolist.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 할일 카테고리 관련 Controller(조회, 추가, 삭제)
 *
 * 할일 카테고리 조회 GET /api/todo-categories?dueDate=YYYY-MM-DD
 * 할일 카테고리 추가 POST /api/todo-categories?dueDate=YYYY-MM-DD
 * 카테고리 삭제  DELETE /api/todo-categories/{categoryId}
 */
@RestController
@RequestMapping("/api/todo-categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryApiController {

    private final UserService userService;
    private final CategoryService categoryService;


    /**
     * 할일 카테고리 조회.
     *
     * @param dueDate 조회할 날짜
     * @return 사용자 ID, 사용자 이름, 카테고리 목록을 포함한 TodoResponse 객체
     */
    @GetMapping
    public TodoResponse todoList(@RequestParam LocalDate dueDate) {

        Long userId = userService.getCurrentUserId();
        UserDTO userDTO = userService.findByUserId(userId);
        String username = userDTO.getUsername();

        List<CategoryDTO> categories = categoryService.getCategories(dueDate, userDTO);

        // CategoryDTO를 ResponseCategoryDTO로 변환
        List<ResponseCategoryDTO> responseCategories = categories.stream()
                .map(category -> new ResponseCategoryDTO(category.getCategoryId(), category.getTitle(), category.getDueDate()))
                .collect(Collectors.toList());

        return new TodoResponse(userId, username, responseCategories);
    }

    /**
     * 할일 카테고리 추가
     *
     * @param dueDate 조회할 날짜
     * @param request 카테고리 내용 -> ex) 운동, 공부와 같은 큰 범위
     * @return 사용자 ID, 사용자 이름, 추가한 카테고리를 포함한 AddCategoryResponse 객체
     */
    @PostMapping
    public AddCategoryResponse addCategory(@RequestParam LocalDate dueDate, @RequestBody @Valid CategoryRequest request) {

        Long userId = userService.getCurrentUserId();

        UserDTO userDTO = userService.findByUserId(userId);

        Long categoryId = categoryService.addCategory(dueDate, userId, request.getTitle());

        AddCategoryDTO addCategoryDTO = new AddCategoryDTO(categoryId, request.getTitle(), dueDate);

        return new AddCategoryResponse(userDTO.getUserId(), userDTO.getUsername(), addCategoryDTO);
    }

    /**
     * 할일 카테고리 삭제
     *
     * @param categoryId 삭제할 카테고리 ID
     * @return 카테고리 ID, 메시지(성공 or 실패)를 포함한 DeleteCategoryResponse 객체
     */
    @DeleteMapping("/{categoryId}")
    public DeleteCategoryResponse deleteCategory(@PathVariable Long categoryId) {
        boolean isDeleted = categoryService.deleteCategory(categoryId);
        if (isDeleted) {
            return new DeleteCategoryResponse("카테고리가 성공적으로 삭제되었습니다.", categoryId);
        } else {
            return new DeleteCategoryResponse("카테고리를 찾을 수 없습니다.", categoryId);
        }
    }


    @Data
    @AllArgsConstructor
    static class TodoResponse {
        private Long userId;
        private String username;
        private List<ResponseCategoryDTO> category;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class ResponseCategoryDTO {

        private Long categoryId;
        private String title;
        private LocalDate dueDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class CategoryRequest {
        private String title;
    }

    @Data
    @AllArgsConstructor
    static class AddCategoryResponse {
        private Long userId;
        private String username;
        private AddCategoryDTO category;
    }

    @Data
    @AllArgsConstructor
    static class AddCategoryDTO {
        private Long categoryId;
        private String title;
        private LocalDate dueDate;
    }


    @Data
    @AllArgsConstructor
    static class DeleteCategoryResponse {
        private String message;
        private Long categoryId;
    }
}
