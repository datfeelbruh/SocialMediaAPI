package sobad.code.services;

import sobad.code.dtos.PostDtoRequest;
import sobad.code.dtos.PostDtoResponse;

import java.util.List;

public interface PostService {
    PostDtoResponse createPost(PostDtoRequest postDtoRequest);
    PostDtoResponse getPostDtoResponse(Long id);
    PostDtoResponse updatePost(Long id, PostDtoRequest postDtoRequest);
    PostDtoResponse deletePost(Long id);
    List<PostDtoResponse> getFollowedUsersPost(Integer page, Integer limit, Boolean sorted);
}
