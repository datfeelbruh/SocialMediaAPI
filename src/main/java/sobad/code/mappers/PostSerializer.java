package sobad.code.mappers;

import org.springframework.stereotype.Component;
import sobad.code.dtos.PostDtoResponse;
import sobad.code.entities.Post;

import java.util.function.Function;

@Component
public class PostSerializer implements Function<Post, PostDtoResponse> {

    @Override
    public PostDtoResponse apply(Post post) {
        return PostDtoResponse.builder()
                .title(post.getTitle())
                .description(post.getDescription())
                .createdAt(post.getCreatedAt())
                .imageSrc(post.getImageSrc())
                .build();
    }
}
