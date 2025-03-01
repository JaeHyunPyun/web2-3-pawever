package com.pawever.server.domain.user.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.request.AuthRequestDto;
import com.pawever.server.domain.user.dto.response.UserResponseDto;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.enums.Role;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getUserInfoByUuid(String socialLoginUuid){

        User foundUser = userRepository.findUuid(socialLoginUuid);
        if(foundUser != null){
            return UserResponseDto.builder()
                .userId(foundUser.getUserId())
                .socialLoginUuid(foundUser.getSocialLoginUuid())
                .name(foundUser.getName())
                .role(foundUser.getRole())
                .build();
        }

        return null;
    }

    public User createNewUser(AuthRequestDto authRequestDto) {
        return User.builder()
            .name(authRequestDto.getName())
            .email(authRequestDto.getEmail())
            .profileImageUrl(authRequestDto.getProfileImageUrl())
            .socialLoginUuid(authRequestDto.getSocialLoginUuid())
            .socialLoginProvider(authRequestDto.getSocialLoginProvider())
            .latitude(authRequestDto.getLatitude())
            .longitude(authRequestDto.getLongitude())
            .role(Role.ROLE_USER)
            .isDeleted(false)
            .build();
    }

    @Transactional
    public UserResponseDto saveNewUser(User user) {

        try {
            User savedUser = userRepository.save(user);

            log.info("회원가입 완료 : 회원ID - {}", user.getUserId());

            return UserResponseDto.builder()
                .userId(savedUser.getUserId())
                .socialLoginUuid(savedUser.getSocialLoginUuid())
                .name(savedUser.getName())
                .role(savedUser.getRole())
                .build();
        } catch (DataIntegrityViolationException e) {
            log.error("회원가입실패 : 회원ID - {}, 이유 - {}", user.getUserId(), e.getMessage());
            throw new CustomException(ResponseCodeEnum.MISSING_REQUIRED_FIELDS);
        }
    }
}
