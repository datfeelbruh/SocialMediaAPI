package sobad.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@Slf4j
public class ImageUtil {
    private final String imageDirectory;

    public ImageUtil(@Value("${image-directory}") String imageDirectory) {
        this.imageDirectory = imageDirectory;
    }

    public void deleteImage(String filename) {
        Optional<File> file = Optional.empty();
        String path = imageDirectory + filename.substring(filename.indexOf("=") + 1);
        try (Stream<Path> entries = Files.walk(Path.of(path))) {
            file = entries
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .findFirst();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        file.ifPresent(File::delete);
    }

    public Optional<File> getImage(String filename) {
        Optional<File> file = Optional.empty();
        String path = imageDirectory + filename;
        try (Stream<Path> entries = Files.walk(Path.of(path))) {
            file = entries
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .findFirst();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return file;
    }

    public String buildFileSrc(String contentType, String username) {
        String type = contentType.substring(6);
        return imageDirectory
                + "/"
                + username
                + UUID.randomUUID()
                + "."
                + type;
    }

    public String buildFileLink(String filepath) {
        return "/api/v1/image?filename=" + filepath.substring(filepath.lastIndexOf("/") + 1);
    }

    public static boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}
