package sobad.code.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sobad.code.dtos.PostDtoRequest;
import sobad.code.dtos.PostDtoResponse;
import sobad.code.services.impl.PostServiceImpl;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostServiceImpl postService;

    @PostMapping("")
    public ResponseEntity<PostDtoResponse> createPost(@RequestPart(name = "image") MultipartFile file,
                                                      @RequestPart(name = "post") PostDtoRequest postDtoRequest) throws IOException {
        return new ResponseEntity<>(postService.createPost(postDtoRequest, file), CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDtoResponse> getPost(@PathVariable(value = "postId") Long postId) {
        return new ResponseEntity<>(postService.getPostDtoResponse(postId), OK);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDtoResponse> updatePost(@PathVariable(value = "postId") Long postId,
                                                      @RequestPart(name = "image") MultipartFile file,
                                                      @RequestPart(name = "post") PostDtoRequest postDtoRequest) throws IOException {
        return new ResponseEntity<>(postService.updatePost(postId, postDtoRequest, file), OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDtoResponse> deletePost(@PathVariable(value = "postId") Long postId) {
        return new ResponseEntity<>(postService.deletePost(postId), OK);
    }
}
