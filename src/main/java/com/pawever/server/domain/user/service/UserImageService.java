package com.pawever.server.domain.user.service;

import com.pawever.server.domain.post.service.ImageService;
import com.pawever.server.domain.user.constant.UserConstants;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserImageService {

    private final ImageService imageService;

    @Transactional
    public void deleteUserOldProfileImage(User user){
        String oldProfileImageUrl = user.getProfileImageUrl();
        if (oldProfileImageUrl != null
            && !oldProfileImageUrl.equals(UserConstants.DEFAULT_PROFILE_IMAGE_URL)
            && oldProfileImageUrl.contains("s3.ap-northeast-2.amazonaws.com")) {

            // 기존 이미지 S3에서 삭제
            imageService.deleteImageFromS3(oldProfileImageUrl);
            log.info("[기존 이미지 aws s3에서 제거 완료] 유저명 - {}, 이미지명 - {}", user.getName(), user.getProfileImageUrl());
        }

    }
}
