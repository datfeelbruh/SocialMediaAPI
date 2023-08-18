package sobad.code.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {
    private final ImageUtil imageUtil;

    @GetMapping("")
    public ResponseEntity<byte[]> getPostImage(@RequestParam(value = "filename") String filename) throws IOException {
        File file = imageUtil.getImage(filename).orElseThrow();
        byte[] bytes = StreamUtils.copyToByteArray(file.toURI().toURL().openStream());
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes);
    }
}
