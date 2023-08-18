package sobad.code.services;

import org.springframework.web.multipart.MultipartFile;
import sobad.code.dtos.PostDtoRequest;
import sobad.code.dtos.PostDtoResponse;

import java.io.IOException;

public interface PostService {
    PostDtoResponse createPost(PostDtoRequest postDtoRequest, MultipartFile file) throws IOException;
    PostDtoResponse getPostDtoResponse(Long id);
    PostDtoResponse updatePost(Long id, PostDtoRequest postDtoRequest, MultipartFile file) throws IOException;
    PostDtoResponse deletePost(Long id);
}
