package com.acoldbottle.todolist.service;

import com.acoldbottle.todolist.domain.User;
import com.acoldbottle.todolist.dto.UserDTO;
import com.acoldbottle.todolist.exception.AuthException;
import com.acoldbottle.todolist.exception.UserNotFoundException;
import com.acoldbottle.todolist.oauth2.CustomOauth2User;
import com.acoldbottle.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * USER 서비스
 * ===================================================
 * saveEntity() => 사용자를 DB에 저장
 * findByUserId() => DB에서 사용자 조회
 * getCurrentUserId() => 현재 사용하고 있는 사용자 아이디 조회
 * ===================================================
 * convertDtoToEntity() => 사용자 DTO를 사용자 엔티티로 변환
 * convertEntityToDto() => 사용자 엔티티를 사용자 DTO로 변환
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void saveEntity(UserDTO userDTO) {
        userRepository.save(convertDtoToEntity(userDTO));
    }

    public UserDTO findByUserId(Long userId) {

        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> {

                    log.warn("User Not Found, [USER ID]={}", userId);
                    return new UserNotFoundException("해당 유저를 찾을 수 없습니다.");
                });

        return convertEntityToDto(userEntity);
    }

    public Long getCurrentUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {

            CustomOauth2User currentUser = (CustomOauth2User) authentication.getPrincipal();
            return currentUser.getId();

        } else {

            log.error("인증되지 않은 사용자 요청 : authentication = {}", authentication);
            throw new AuthException("인증된 사용자가 아닙니다");
        }
    }

    public User convertDtoToEntity(UserDTO userDTO) {

        return User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .provider(userDTO.getProvider())
                .build();
    }

    public UserDTO convertEntityToDto(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getProvider());
    }


}

