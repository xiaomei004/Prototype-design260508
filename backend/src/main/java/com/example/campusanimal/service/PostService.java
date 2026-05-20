package com.example.campusanimal.service;

import com.example.campusanimal.common.PageResult;
import com.example.campusanimal.dto.PostCreateRequest;
import com.example.campusanimal.dto.PostUpdateRequest;
import com.example.campusanimal.entity.Post;
import com.example.campusanimal.entity.User;
import com.example.campusanimal.exception.BusinessException;
import com.example.campusanimal.repository.PostRepository;
import com.example.campusanimal.repository.UserRepository;
import com.example.campusanimal.vo.PostVO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AnimalService animalService;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       AnimalService animalService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.animalService = animalService;
    }

    public PageResult<PostVO> list(String type, Long animalId, int pageNum, int pageSize) {
        List<PostVO> filtered = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime")).stream()
                .filter(post -> type == null || type.isBlank() || type.equals(post.getType()))
                .filter(post -> animalId == null || animalId.equals(post.getAnimalId()))
                .map(this::toVO)
                .toList();

        int fromIndex = Math.max((pageNum - 1) * pageSize, 0);
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        List<PostVO> pageList = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return new PageResult<>(pageList, filtered.size(), pageNum, pageSize);
    }

    public PostVO create(PostCreateRequest request, User currentUser) {
        Post post = postRepository.save(Post.builder()
                .userId(currentUser.getId())
                .animalId(request.getAnimalId())
                .type(request.getType())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .location(request.getLocation())
                .createTime(LocalDateTime.now())
                .build());
        return toVO(post);
    }

    public PostVO update(Long id, PostUpdateRequest request, User currentUser) {
        Post post = getEntity(id);
        ensureCanManagePost(post, currentUser);

        if (request.getAnimalId() != null || post.getAnimalId() != null) {
            post.setAnimalId(request.getAnimalId());
        }
        if (request.getType() != null && !request.getType().isBlank()) {
            post.setType(request.getType());
        }
        if (request.getContent() != null && !request.getContent().isBlank()) {
            post.setContent(request.getContent());
        }
        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            post.setLocation(request.getLocation());
        }
        if (request.getImageUrl() != null) {
            post.setImageUrl(request.getImageUrl());
        }

        return toVO(postRepository.save(post));
    }

    public boolean delete(Long id, User currentUser) {
        Post post = getEntity(id);
        ensureCanManagePost(post, currentUser);
        postRepository.deleteById(id);
        return true;
    }

    public List<PostVO> findByUserId(Long userId) {
        return postRepository.findByUserIdOrderByCreateTimeDesc(userId).stream()
                .map(this::toVO)
                .toList();
    }

    private PostVO toVO(Post post) {
        User user = userRepository.findById(post.getUserId()).orElse(null);
        String animalName = post.getAnimalId() == null ? null : animalService.getEntity(post.getAnimalId()).getName();
        return PostVO.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .nickname(user == null ? "未知用户" : user.getNickname())
                .animalId(post.getAnimalId())
                .animalName(animalName)
                .type(post.getType())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .location(post.getLocation())
                .createTime(post.getCreateTime())
                .build();
    }

    private Post getEntity(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "动态不存在"));
    }

    private void ensureCanManagePost(Post post, User currentUser) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        boolean isOwner = post.getUserId() != null && post.getUserId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new BusinessException(403, "无权修改其他用户发布的动态");
        }
    }
}
