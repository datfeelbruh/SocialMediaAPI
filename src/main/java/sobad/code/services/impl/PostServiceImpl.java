package sobad.code.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sobad.code.ImageUtil;
import sobad.code.dtos.PostDtoRequest;
import sobad.code.dtos.PostDtoResponse;
import sobad.code.entities.Post;
import sobad.code.entities.User;
import sobad.code.mappers.PostSerializer;
import sobad.code.repositories.PostRepository;
import sobad.code.services.PostService;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ImageUtil imageUtil;
    private final UserServiceImpl userService;
    private final PostSerializer postSerializer;

    @Override
    public PostDtoResponse createPost(PostDtoRequest postDtoRequest, MultipartFile file) throws IOException {
        User user = userService.getCurrentUser();
        Post post = Post.builder()
                .title(postDtoRequest.getTitle())
                .description(postDtoRequest.getDescription())
                .createdAt(Instant.now())
                .build();

        String filepath = imageUtil.buildFileSrc(Objects.requireNonNull(file.getContentType()), user.getUsername());
        file.transferTo(new File(filepath));
        post.setImageSrc(imageUtil.buildFileLink(filepath));
        postRepository.save(post);
        userService.updateUserPosts(user, post);

        return postSerializer.apply(post);
    }

    @Override
    public PostDtoResponse getPostDtoResponse(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Пост не найден"));
        return postSerializer.apply(post);
    }

    @Override
    public PostDtoResponse updatePost(Long id, PostDtoRequest postDtoRequest, MultipartFile file) throws IOException {
        User user = userService.getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow();

        if (!user.getPosts().contains(post)) {
            throw new RuntimeException("");
        }

        if (!file.isEmpty()) {
            String filepath = imageUtil.buildFileSrc(Objects.requireNonNull(file.getContentType()), user.getUsername());
            file.transferTo(new File(filepath));
            post.setImageSrc(imageUtil.buildFileLink(filepath));
        }
        post.setId(id);
        post.setTitle(postDtoRequest.getTitle());
        post.setDescription(postDtoRequest.getDescription());
        postRepository.save(post);
        userService.updateUserPosts(user, post);

        return postSerializer.apply(post);
    }

    @Override
    public PostDtoResponse deletePost(Long id) {
        User user = userService.getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow();

        if (!user.getPosts().contains(post)) {
            throw new RuntimeException("");
        }
        imageUtil.deleteImage(post.getImageSrc());
        postRepository.delete(post);
        return postSerializer.apply(post);
    }
}
