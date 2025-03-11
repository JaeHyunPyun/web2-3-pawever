package com.pawever.server.domain.post.service;


import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.post.dto.request.PostRequestDTO;
import com.pawever.server.domain.post.dto.response.PostResponseDTO;
import com.pawever.server.domain.post.entity.Post;
import com.pawever.server.domain.post.entity.PostImages;
import com.pawever.server.domain.post.repository.PostImagesRepository;
import com.pawever.server.domain.post.repository.PostRepository;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostImagesRepository postImagesRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;


    @Transactional
    public PostResponseDTO.PostResponse createPost(PostRequestDTO.CreatePostRequest request, Long userId, List<MultipartFile> images, MultipartFile thumbnail) {

        // 요청값 검증
        if (request == null) {
            throw new CustomException(ResponseCodeEnum.NO_REQUEST_ARGUMENT);
        }
        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new CustomException(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT);
        }
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new CustomException(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT);
        }

        try {
            // 이미지가 있는 경우 업로드
            List<String> imageUrls = null;
            String thumbnailUrl = null;
            if (images != null && !images.isEmpty()) {
                try {
                    imageUrls = imageService.uploadImages(images);
                } catch (Exception e) {
                    throw new CustomException(ResponseCodeEnum.UPLOAD_FAILED);
                }
            }
            // 썸네일 이미지 업로드
            if (thumbnail != null && !thumbnail.isEmpty()) {
                try {
                    thumbnailUrl = imageService.uploadImageToS3(thumbnail);
                } catch (Exception e) {
                    throw new CustomException(ResponseCodeEnum.UPLOAD_FAILED);
                }
            }

            //User 찾아오기
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ResponseCodeEnum.USER_NOT_FOUND));
            // 게시글 엔티티 생성
            Post post = Post.builder()
                    .user(user)
                    .title(request.title())
                    .content(request.content())
                    .thumbnailImageUrl(thumbnailUrl)
                    .build();

            // 저장
            postRepository.save(post);

            // 이미지 저장
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String image : imageUrls) {
                    try {
                        PostImages postImages = PostImages.builder()
                                .post(post)
                                .imageUrl(image)
                                .build();
                        postImagesRepository.save(postImages);
                    } catch (Exception e) {
                        throw new CustomException(ResponseCodeEnum.IMAGE_DELETE_FAILED);
                    }
                }
            }

            return new PostResponseDTO.PostResponse(post.getId(), user.getSocialLoginUuid(), user.getName(), user.getProfileImageUrl(), thumbnailUrl, post.getTitle(), post.getContent(), imageUrls, post.getCreatedAt(), post.getUpdatedAt());

    } catch (CustomException e) {
        throw e; // 이미 처리된 예외는 그대로 던짐
    } catch (Exception e) {
        throw new CustomException(ResponseCodeEnum.UNKNOWN_SERVER_ERROR);
    }


    }

    //게시글 단건 조회
    @Transactional(readOnly = true)
    public PostResponseDTO.PostResponse getPost(Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.POST_NOT_FOUND));
        // 유저
        User user = post.getUser();

        // 저장되어있는 이미지 조회
        List<String> imageUrls;
        try {
            imageUrls = postImagesRepository.findImageUrlsByPostId(postId);
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = Collections.emptyList(); // 빈 리스트로 초기화하여 NullPointerException 방지
            }
        } catch (DataAccessException e) {
            throw new CustomException(ResponseCodeEnum.FILE_READ_ERROR);
        }

        return new PostResponseDTO.PostResponse(post.getId(), user.getSocialLoginUuid(), user.getName(), user.getProfileImageUrl(), post.getThumbnailImageUrl(), post.getTitle(), post.getContent(), imageUrls, post.getCreatedAt(), post.getUpdatedAt());
    }

    //게시글 전체 조회
    @Transactional(readOnly = true)
    public List<PostResponseDTO.PostResponse> getAllPosts() {
        // 최신순으로 게시글 전체 조회
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        // 한 번의 쿼리로 여러 게시글의 이미지 URL 조회
        Map<Long, List<String>> postImageMap = new HashMap<>();
        List<Object[]> imageResults = postImagesRepository.findImageUrlsByPostIds(postIds);

        for (Object[] result : imageResults) {
            Long postId = (Long) result[0];
            String imageUrl = (String) result[1];

            postImageMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(imageUrl);
        }

        // 엔티티 → DTO 변환
        return posts.stream().map(post -> {
            User user = post.getUser();

            // 게시글 ID에 해당하는 이미지 리스트 가져오기 (없으면 빈 리스트)
            List<String> imageUrls = postImageMap.getOrDefault(post.getId(), Collections.emptyList());

            return new PostResponseDTO.PostResponse(post.getId(), user.getSocialLoginUuid() ,user.getName(), user.getProfileImageUrl(), post.getThumbnailImageUrl(), post.getTitle(), post.getContent(), imageUrls, post.getCreatedAt(), post.getUpdatedAt());
        }).toList();
    }

    //게시글 수정
    @Transactional
    public PostResponseDTO.PostResponse updatePost(Long postId, Long userId, PostRequestDTO.UpdatePostRequest request, List<MultipartFile> images, MultipartFile thumbnail) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.POST_NOT_FOUND));

        // 수정 권한 확인
        if (!post.getUser().getUserId().equals(userId)) {
            throw new CustomException(ResponseCodeEnum.UNAUTHORIZED_ACTION);
        }

        // 수정할 필드만 업데이트
        if (request.title() != null) {
            post.setTitle(request.title());
        }
        if (request.content() != null) {
            post.setContent(request.content());
        }

        //thumbnail 이미지 수정
        String thumbnailUrl = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            try {
                thumbnailUrl = imageService.uploadImageToS3(thumbnail);
            } catch (Exception e) {
                throw new CustomException(ResponseCodeEnum.UPLOAD_FAILED);
            }
        }
        // s3에서 이미지 삭제
        if (post.getThumbnailImageUrl() != null && !post.getThumbnailImageUrl().isEmpty()) {
            imageService.deleteImageFromS3(post.getThumbnailImageUrl());
        }
        post.setThumbnailImageUrl(thumbnailUrl);


        Post updatedPost = postRepository.save(post);


        //이미지 처리 1. 기존 이미지가 있는 경우 삭제 / 2. 새로운 이미지 업로드
        // 1.
        // 이미지 조회 -> images != null && !images.isEmpty()일 경우에만
        List<String> imageUrls;
        try {
            imageUrls = postImagesRepository.findImageUrlsByPostId(postId);
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = Collections.emptyList(); // 빈 리스트로 초기화하여 NullPointerException 방지
            }
        } catch (DataAccessException e) {
            throw new CustomException(ResponseCodeEnum.FILE_READ_ERROR);
        }

        // s3에서 이미지 삭제
        if (imageUrls != null && !imageUrls.isEmpty()) {
            if (imageUrls.size() > 1) {
                imageService.deleteMultipleImagesFromS3(imageUrls);
            } else {
                imageService.deleteImageFromS3(imageUrls.get(0));
            }
        }
        // post_images 테이블에서도 이미지 삭제
        try {
            postImagesRepository.deleteByPostId(postId);
        } catch (DataAccessException e) {
            throw new CustomException(ResponseCodeEnum.IMAGE_DELETE_FAILED);
        }

        // 2.
        // 이미지가 있는 경우 업로드
        imageUrls = Collections.emptyList(); //이미지 삭제에 사용되었던 imageUrls 초기화
        if (images != null && !images.isEmpty()) {
            imageUrls = imageService.uploadImages(images);
        }

        //이미지도 postImagesRepository 에 저장
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String image : imageUrls) {
                PostImages postImages = PostImages.builder()
                        .post(post)
                        .imageUrl(image)
                        .build();
                postImagesRepository.save(postImages);
            }
        } else {
            //반환용 imageUrls 초기화
            imageUrls = postImagesRepository.findImageUrlsByPostId(postId);
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = Collections.emptyList(); // 빈 리스트로 초기화하여 NullPointerException 방지
            }
        }
        User user = updatedPost.getUser();
        return new PostResponseDTO.PostResponse(updatedPost.getId(), user.getSocialLoginUuid(), user.getName(), user.getProfileImageUrl(), updatedPost.getThumbnailImageUrl(), updatedPost.getTitle(), updatedPost.getContent(), imageUrls, updatedPost.getCreatedAt(), post.getUpdatedAt());
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.POST_NOT_FOUND));

        // 삭제 권한 확인
        if (!post.getUser().getUserId().equals(userId)) {
            throw new CustomException(ResponseCodeEnum.UNAUTHORIZED_ACTION);
        }

        // 이미지 조회
        List<String> imageUrls;
        try {
            imageUrls = postImagesRepository.findImageUrlsByPostId(postId);
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = Collections.emptyList(); // 빈 리스트로 초기화하여 NullPointerException 방지
            }
        } catch (DataAccessException e) {
            throw new CustomException(ResponseCodeEnum.FILE_READ_ERROR);
        }

        // s3에서 이미지 삭제
        if (imageUrls != null && !imageUrls.isEmpty()) {
            if (imageUrls.size() > 1) {
                imageService.deleteMultipleImagesFromS3(imageUrls);
            } else {
                imageService.deleteImageFromS3(imageUrls.get(0));
            }
        }

        // post_images 테이블에서도 이미지 삭제
        try {
            postImagesRepository.deleteByPostId(postId);
        } catch (DataAccessException e) {
            throw new CustomException(ResponseCodeEnum.IMAGE_DELETE_FAILED);
        }

        //s3에서 썸네일 이미지 삭제
        if (post.getThumbnailImageUrl() != null && !post.getThumbnailImageUrl().isEmpty()) {
            imageService.deleteImageFromS3(post.getThumbnailImageUrl());
        }

        // 게시글 삭제
        postRepository.delete(post);
    }

    //로그인 한 회원이 작성한 게시글 전체 조회
    @Transactional(readOnly = true)
    public List<PostResponseDTO.PostResponse> getAllUsersPosts(Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.USER_NOT_FOUND));

        // 최신순으로 게시글 전체 조회
        List<Post> posts = postRepository.findAllByUserId(userId);
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        // 한 번의 쿼리로 여러 게시글의 이미지 URL 조회
        Map<Long, List<String>> postImageMap = new HashMap<>();
        List<Object[]> imageResults = postImagesRepository.findImageUrlsByPostIds(postIds);

        for (Object[] result : imageResults) {
            Long postId = (Long) result[0];
            String imageUrl = (String) result[1];

            postImageMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(imageUrl);
        }

        // 엔티티 → DTO 변환
        return posts.stream().map(post -> {
            // 게시글 ID에 해당하는 이미지 리스트 가져오기 (없으면 빈 리스트)
            List<String> imageUrls = postImageMap.getOrDefault(post.getId(), Collections.emptyList());

            return new PostResponseDTO.PostResponse(post.getId(), user.getSocialLoginUuid() ,user.getName(), user.getProfileImageUrl(), post.getThumbnailImageUrl(), post.getTitle(), post.getContent(), imageUrls, post.getCreatedAt(), post.getUpdatedAt());
        }).toList();
    }


    //게시글 검색 기능 추가
    public List<PostResponseDTO.PostResponse> searchPosts(String q) {

        Specification<Post> spec = (root, query, cb) -> cb.conjunction();

        if (q != null && !q.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("title"), "%" + q + "%")); // 제목으로 검색
        }

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "id"));

        Page<Post> posts = postRepository.findAll(spec, pageable);
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        // 한 번의 쿼리로 여러 게시글의 이미지 URL 조회
        Map<Long, List<String>> postImageMap = new HashMap<>();
        List<Object[]> imageResults = postImagesRepository.findImageUrlsByPostIds(postIds);

        for (Object[] result : imageResults) {
            Long postId = (Long) result[0];
            String imageUrl = (String) result[1];

            postImageMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(imageUrl);
        }

        // 엔티티 → DTO 변환
        return posts.stream().map(post -> {
            User user = post.getUser();

            // 게시글 ID에 해당하는 이미지 리스트 가져오기 (없으면 빈 리스트)
            List<String> imageUrls = postImageMap.getOrDefault(post.getId(), Collections.emptyList());

            return new PostResponseDTO.PostResponse(post.getId(), user.getSocialLoginUuid() ,user.getName(), user.getProfileImageUrl(), post.getThumbnailImageUrl(), post.getTitle(), post.getContent(), imageUrls, post.getCreatedAt(), post.getUpdatedAt());
        }).toList();
    }



}
