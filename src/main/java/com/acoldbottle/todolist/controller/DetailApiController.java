package com.acoldbottle.todolist.controller;

import com.acoldbottle.todolist.dto.CategoryDTO;
import com.acoldbottle.todolist.dto.DetailDTO;
import com.acoldbottle.todolist.dto.UserDTO;
import com.acoldbottle.todolist.service.CategoryService;
import com.acoldbottle.todolist.service.DetailService;
import com.acoldbottle.todolist.service.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 카테고리의 할일 관련 Controller(조회, 추가, 수정, 삭제)
 *
 * 특정 카테고리의 할일 조회 GET http://<서버주소>/api/todo-categories/{categoryId}/details
 * 특정 카테고리에 할일 추가 POST http://<서버주소>/api/todo-categories/{categoryId}/details
 * 특정 카테고리에 할일 수정 PATCH http://<서버주소>/api/todo-categories/{categoryId}/details/{detailId}
 * 특정 카테고리에 할일 삭제 DELETE http://<서버주소>/api/todo-categories/{categoryId}/details/{detailId}
 */
@RestController
@RequestMapping("/api/todo-categories")
@RequiredArgsConstructor
public class DetailApiController {

    private final CategoryService categoryService;
    private final DetailService detailService;
    private final UserService userService;

    /**
     * 할일 조회
     *
     * @param categoryId 카테고리 ID
     * @return 사용자 ID, 사용자 이름, 카테고리와 해당 카테고리 안에 있는 할일 목록을 포함한 AllDetailResponse 객체
     */
    @GetMapping("/{categoryId}/details")
    public AllDetailResponse getDetailsByCategory(@PathVariable Long categoryId) {

        Long userId = userService.getCurrentUserId();
        UserDTO userDTO = userService.findByUserId(userId);

        CategoryDTO categoryDTO = categoryService.findCategory(categoryId);

        List<DetailDTO> details = detailService.getDetailsByCategory(categoryId);
        categoryDTO.setDetailDTOList(details);

        return new AllDetailResponse(userDTO.getUserId(), userDTO.getUsername(), categoryDTO);
    }

    /**
     * 할일 추가
     *
     * @param categoryId 카테고리 ID
     * @param detailDTO 할일 내용 -> ex) 유산소, 웨이트, 코딩 알고리즘, 독서와 같은 상세 할일.
     * @return 사용자 ID, 사용자 이름, 카테고리와 해당 카테고리 안에 있는 할일 목록을 포함한 AllDetailResponse 객체
     */
    @PostMapping("/{categoryId}/details")
    public AllDetailResponse addDetail(@PathVariable Long categoryId, @RequestBody @Valid DetailDTO detailDTO) {

        Long userId = userService.getCurrentUserId();
        UserDTO userDTO = userService.findByUserId(userId);

        CategoryDTO categoryDTO = categoryService.findCategory(categoryId);
        DetailDTO addDetailDTO = detailService.addDetail(categoryId, detailDTO.getDescription());
        List<DetailDTO> detailDTOList = new ArrayList<>();
        detailDTOList.add(addDetailDTO);
        categoryDTO.setDetailDTOList(detailDTOList);

        return new AllDetailResponse(userDTO.getUserId(), userDTO.getUsername(), categoryDTO);
    }

    /**
     * 할일 수정
     *
     * @param categoryId 카테고리 ID
     * @param detailId 할일 ID
     * @param detailDTO 할일 내용, 완료 여부 -> ex) [유산소, 웨이트, 코딩 알고리즘, 독서와 같은 상세 할일] [true or false] 둘 중에 하나만 요청해도 적용 -> Patch
     * @return 사용자 ID, 사용자 이름, 카테고리와 해당 카테고리 안에 있는 할일 목록을 포함한 AllDetailResponse 객체
     */
    @PatchMapping("/{categoryId}/details/{detailId}")
    public AllDetailResponse updateDetail(@PathVariable Long categoryId, @PathVariable Long detailId, @RequestBody @Valid DetailDTO detailDTO) {

        Long userId = userService.getCurrentUserId();
        UserDTO userDTO = userService.findByUserId(userId);

        CategoryDTO categoryDTO = categoryService.findCategory(categoryId);

        DetailDTO updateDetail = detailService.updateDetail(detailId, detailDTO.getDescription(), detailDTO.getIsCompleted());
        List<DetailDTO> detailDTOList = new ArrayList<>();
        detailDTOList.add(updateDetail);
        categoryDTO.setDetailDTOList(detailDTOList);

        return new AllDetailResponse(userDTO.getUserId(), userDTO.getUsername(), categoryDTO);
    }

    /**
     * 할일 삭제
     *
     * @param categoryId 카테고리 ID
     * @param detailId 할일 ID
     * @return 할일 ID, 메시지(성공 or 실패)를 포함한 DeleteDetailResponse 객체
     */
    @DeleteMapping("/{categoryId}/details/{detailId}")
    public DeleteDetailResponse deleteDetail(@PathVariable Long categoryId, @PathVariable Long detailId) {
        boolean isDeleted = detailService.deleteDetail(detailId);
        if (isDeleted) {
            return new DeleteDetailResponse("할일이 성공적으로 삭제되었습니다.", detailId);
        } else {
            return new DeleteDetailResponse("할일을 찾을 수 없습니다.", detailId);
        }
    }





    @Data
    @AllArgsConstructor
    static class AllDetailResponse {
        private Long userId;
        private String username;
        @JsonProperty("category")
        private CategoryDTO categoryDTO;
    }


    @Data
    @AllArgsConstructor
    static class DeleteDetailResponse {
        private String message;
        private Long detailId;
    }

}
