package sobad.code.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.dtos.PostDtoRequest;
import sobad.code.dtos.PostDtoResponse;
import sobad.code.entities.Post;
import sobad.code.entities.User;
import sobad.code.exceptions.EntityNotFound;
import sobad.code.mappers.PostSerializer;
import sobad.code.repositories.PostRepository;
import sobad.code.services.PostService;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserServiceImpl userService;
    private final PostSerializer postSerializer;

    @Override
    @Transactional
    public PostDtoResponse createPost(PostDtoRequest postDtoRequest) {
        User user = userService.getCurrentUser();
        Post post = Post.builder()
                .title(postDtoRequest.getTitle())
                .description(postDtoRequest.getDescription())
                .imageSrc(postDtoRequest.getImageSrc())
                .createdAt(Instant.now())
                .build();

        postRepository.save(post);
        userService.updateUserPosts(user, post);

        return postSerializer.apply(post);
    }

    @Transactional
    @Override
    public PostDtoResponse getPostDtoResponse(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new EntityNotFound(String.format("Пост c таким ID: '%s' не найден", id)));
        return postSerializer.apply(post);
    }

    @Transactional
    @Override
    public List<PostDtoResponse> getFollowedUsersPost(Integer page, Integer limit, Boolean sorted) {
        Pageable pageable;
        if (Boolean.TRUE.equals(sorted)) {
            pageable = PageRequest.of(page - 1, limit, Sort.by("created_at"));
        } else {
            pageable = PageRequest.of(page - 1, limit);
        }

        List<Post> followUsersPosts =
                postRepository.findAllFollowUsersPosts(userService.getCurrentUser().getId(), pageable);

        return followUsersPosts.stream()
                .map(postSerializer)
                .toList();
    }

    @Transactional
    @Override
    public PostDtoResponse updatePost(Long id, PostDtoRequest postDtoRequest) {
        User user = userService.getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow();

        if (!user.getPosts().contains(post)) {
            throw new EntityNotFound(
                    String.format("Пост с таким ID: '%s' не найден у пользователя с ID: '%s'", id, user.getId())
            );
        }

        post.setId(id);
        post.setTitle(postDtoRequest.getTitle());
        post.setDescription(postDtoRequest.getDescription());
        post.setImageSrc(postDtoRequest.getImageSrc());

        postRepository.save(post);
        userService.updateUserPosts(user, post);

        return postSerializer.apply(post);
    }

    @Transactional
    @Override
    public PostDtoResponse deletePost(Long id) {
        User user = userService.getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow();

        if (!user.getPosts().contains(post)) {
            throw new EntityNotFound(
                    String.format("Пост с таким ID: '%s' не найден у пользователя с ID: '%s'", id, user.getId())
            );
        }
        postRepository.delete(post);
        return postSerializer.apply(post);
    }
}
