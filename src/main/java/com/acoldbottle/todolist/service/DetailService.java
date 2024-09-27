package com.acoldbottle.todolist.service;

import com.acoldbottle.todolist.domain.TodoCategory;
import com.acoldbottle.todolist.domain.TodoDetail;
import com.acoldbottle.todolist.dto.DetailDTO;
import com.acoldbottle.todolist.exception.CategoryNotFoundException;
import com.acoldbottle.todolist.exception.DetailNotFoundException;
import com.acoldbottle.todolist.repository.CategoryRepository;
import com.acoldbottle.todolist.repository.DetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Detail 서비스
 * =====================================================
 * saveDetail() => 할일 저장
 * getDetailsByCategory() => 해당 카테고리에 있는 할일 목록 조회
 * addDetail() => 할일 추가
 * updateDetail() => 할일 수정 -> ex) 할일 내용 or 완료 여부
 * deleteDetail() => 할일 삭제
 * =====================================================
 * convertEntityToDto() => 할일 엔티티를 할일 DTO로 변환
 * convertDtoToEntity() => 할일 DTO를 할일 엔티티로 변환
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetailService {

    private final DetailRepository detailRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Long saveDetail(DetailDTO detailDTO) {

        TodoDetail savedDetail = detailRepository.save(convertDtoToEntity(detailDTO));
        return savedDetail.getId();
    }

    public List<DetailDTO> getDetailsByCategory(Long categoryId) {

        TodoCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {

                    log.warn("Category Not Found, [category ID] ={}", categoryId);
                    return new CategoryNotFoundException("해당 카테고리를 찾을 수 없습니다. 카테고리 아이디를 다시 확인해주세요");
                });

        return detailRepository.findByTodoCategory(category).stream()
                .map(this::convertEntityToDto)
                .toList();
    }

    @Transactional
    public DetailDTO addDetail(Long categoryId, String description) {

        DetailDTO detailDTO = new DetailDTO(description, false, categoryId);
        Long savedDetailId = saveDetail(detailDTO);
        detailDTO.setDetailId(savedDetailId);
        return detailDTO;
    }

    @Transactional
    public DetailDTO updateDetail(Long detailId, String description, Boolean isCompleted) {

        TodoDetail todoDetail = detailRepository.findById(detailId)
                .orElseThrow(() -> {
                    log.warn("Detail Not Found, [detail ID]={}", detailId);
                    return new DetailNotFoundException("해당 할 일을 찾을 수 없습니다. 할 일의 아이디를 확인해주세요.");
                });

        if (description != null) {
            todoDetail.addDescription(description);
        }
        if (isCompleted != null) {
            todoDetail.updateCompleteStatus(isCompleted);
        }

        detailRepository.save(todoDetail);
        return convertEntityToDto(todoDetail);
    }
    @Transactional
    public boolean deleteDetail(Long detailId) {

        try {

            detailRepository.findById(detailId).orElseThrow(() -> {
                        log.warn("Detail Not Found, [detail ID]={}", detailId);
                        return new DetailNotFoundException("해당 할 일을 찾을 수 없습니다. 할 일의 아이디를 확인해주세요.");
                    }
            );

            detailRepository.deleteById(detailId);
            return true;

        } catch (DetailNotFoundException e) {

            return false;
        } catch (Exception e) {

            log.error("[deleteDetail Error] {}", e.getMessage());
            return false;
        }
    }

    public DetailDTO convertEntityToDto(TodoDetail todoDetail) {
        return new DetailDTO(todoDetail.getId(), todoDetail.getDescription(), todoDetail.isCompleted(), todoDetail.getTodoCategory().getId());
    }

    public TodoDetail convertDtoToEntity(DetailDTO detailDTO) {
        return TodoDetail.builder()
                .todoCategory(categoryRepository.findById(detailDTO.getCategoryId()).orElseThrow(() -> {
                    log.warn("Category Not Found, [Category ID]={}", detailDTO.getCategoryId());
                    return new CategoryNotFoundException("해당 카테고리를 찾을 수 업습니다.");
                }))
                .description(detailDTO.getDescription())
                .build();
    }
}
