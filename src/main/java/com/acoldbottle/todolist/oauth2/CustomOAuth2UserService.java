package com.acoldbottle.todolist.oauth2;

import com.acoldbottle.todolist.domain.User;
import com.acoldbottle.todolist.dto.UserDTO;
import com.acoldbottle.todolist.exception.UserRegistrationException;
import com.acoldbottle.todolist.oauth2.provider.FacebookUser;
import com.acoldbottle.todolist.oauth2.provider.GoogleUser;
import com.acoldbottle.todolist.oauth2.provider.NaverUser;
import com.acoldbottle.todolist.oauth2.provider.Oauth2UserResponse;
import com.acoldbottle.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * OAuth2 사용자 정보를 로드하고, 사용자가 처음 로그인할 경우 등록하는 서비스 클래스
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 기본 OAuth2 사용자 정보 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Oauth2UserResponse oauth2UserResponse = null;

        // 로그인한 소셜에 따라 사용자 정보 처리
        switch (registrationId) {

            case "google" :

                oauth2UserResponse = new GoogleUser(oAuth2User.getAttributes());
                break;

            case "facebook":

                oauth2UserResponse = new FacebookUser(oAuth2User.getAttributes());
                break;

            case "naver":

                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                oauth2UserResponse = new NaverUser(response);
                break;
            default:

                throw new OAuth2AuthenticationException("지원하지 않는 소셜입니다");
        }

        String provider = oauth2UserResponse.getProvider();
        String username = oauth2UserResponse.getProvider() + "_" + oauth2UserResponse.getProviderId();
        String email = oauth2UserResponse.getEmail();

        User user = userRepository.findByUsername(username);

        // 사용자가 존재하지 않는 경우에 새로 등록
        if (user == null) {

            user = User.builder()
                    .username(username)
                    .provider(provider)
                    .email(email)
                    .build();

            try {

                userRepository.save(user);
                log.info("새로운 사용자 등록, username={}", user.getUsername());

            } catch (Exception e) {
                log.error("사용자 등록 중 오류 발생: {}", e.getMessage());
                throw new UserRegistrationException("사용자 등록 실패");
            }

        } else {

            log.info("기존 사용자 로그인, username={}", user.getUsername());
        }

        // 사용자 정보를 DTO로 변환
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setEmail(email);
        userDTO.setProvider(provider);
        userDTO.setUserId(user.getId());

        // CustomOauth2User 반환
        return new CustomOauth2User(userDTO, oAuth2User.getAttributes());
    }
}
