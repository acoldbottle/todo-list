package com.acoldbottle.todolist.service;

import com.acoldbottle.todolist.domain.TodoCategory;
import com.acoldbottle.todolist.domain.User;
import com.acoldbottle.todolist.dto.CategoryDTO;
import com.acoldbottle.todolist.dto.UserDTO;
import com.acoldbottle.todolist.exception.CategoryNotFoundException;
import com.acoldbottle.todolist.exception.UserNotFoundException;
import com.acoldbottle.todolist.repository.CategoryRepository;
import com.acoldbottle.todolist.repository.DetailRepository;
import com.acoldbottle.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Category 서비스
 * ====================================================
 * saveCategory() => 카테고리 저장
 * addCategory() => 카테고리 추가
 * deleteCategory() => 카테고리 삭제
 * getCategories() => 카테고리 목록 조회
 * findCategory() => 카테고리 조회
 * ====================================================
 * convertDtoToEntity() => 카테고리 DTO를 카테고리 엔티티로 변환
 * convertEntityToDto() => 카테고리 엔티티를 카테고리 DTO로 변환
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DetailRepository detailRepository;



    @Transactional
    public void saveCategory(CategoryDTO categoryDTO) {

        categoryDTO.setDetailDTOList(null);
        categoryRepository.save(convertDtoToEntity(categoryDTO));
    }

    @Transactional
    public Long addCategory(LocalDate dueDate, Long userId, String title) {

        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User Not Found, [USER ID]={}", userId);
                    return new UserNotFoundException("해당 유저를 찾을 수 없습니다.");
                });

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setUserId(userId);
        categoryDTO.setTitle(title);
        categoryDTO.setDueDate(dueDate);

        TodoCategory todoCategory = convertDtoToEntity(categoryDTO);
        TodoCategory savedCategory = categoryRepository.save(todoCategory);

        return savedCategory.getId();
    }

    @Transactional
    public boolean deleteCategory(Long categoryId) {

        try {

            TodoCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> {

                        log.warn("Category Not Found. [CATEGORY ID]={}", categoryId);
                        return new CategoryNotFoundException("삭제하려는 카테고리를 찾을 수 없습니다.");
                    });

            detailRepository.deleteByTodoCategory(category);
            categoryRepository.deleteById(categoryId);
            return true;

        } catch (CategoryNotFoundException e) {

            return false;

        } catch (Exception e) {

            log.error("[deleteCategory Error] {}", e.getMessage());
            return false;
        }

    }

    public List<CategoryDTO> getCategories(LocalDate dueDate, UserDTO userDTO) {
        List<TodoCategory> categories = categoryRepository.findByUserAndDueDate(userRepository.findByUsername(userDTO.getUsername()), dueDate);
        return categories.stream()
                .map(this::convertEntityToDto)
                .toList();
    }

    public CategoryDTO findCategory(Long categoryId) {

        TodoCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category Not Found. [CATEGORY ID]={}", categoryId);
                    return new CategoryNotFoundException("삭제하려는 카테고리를 찾을 수 없습니다.");
                });
        return convertEntityToDto(category);
    }

    public TodoCategory convertDtoToEntity(CategoryDTO categoryDTO) {

        return TodoCategory.builder()
                .title(categoryDTO.getTitle())
                .dueDate(categoryDTO.getDueDate())
                .user(userRepository.findById(categoryDTO.getUserId()).orElseThrow(() -> {
                    log.warn("User Not Found, [USER ID]={}", categoryDTO.getUserId());
                    return new UserNotFoundException("해당 유저를 찾을 수 없습니다.");
                })).build();
    }

    public CategoryDTO convertEntityToDto(TodoCategory category) {

        User user = category.getUser();
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getProvider());

        return new CategoryDTO(category.getId(), category.getTitle(), userDTO.getUserId(), category.getDueDate(), null);
    }
}
