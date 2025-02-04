package org.example.store.product;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class FileUtil {

    @Value("${file.path}")
    private static String FOLDER_PATH;

    public static String saveAndRenameFile(MultipartFile file) {
        String onlyFileName
                = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
        String extension
                = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String writeTime = LocalDateTime.now().format(format);
        String renameFileName = onlyFileName + "_" + writeTime + extension;

        // 폴더는 하루 단위로 생성
        DateTimeFormatter folderFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        String folderName = LocalDateTime.now().format(folderFormat);

        // folderPath == 파일을 저장할 절대경로, 즉 서버 저장경로
        Path folderPath = Paths.get(FOLDER_PATH + "product/" + folderName);
        try {
            if (Files.notExists(folderPath)) {
                Files.createDirectory(folderPath);
            }

            // Paths.get() 의 파라미터는 스트링, 곧 파일이나 폴더의 경로 이름
            Path targetFile = Paths.get(folderPath + "/" + renameFileName);
            // transferTo 를 통해 설정한 경로로 파일을 옮김 !!
            file.transferTo(targetFile);
//
//            Thumbnails.of(targetFile.toFile())
//                    .size(300, 300)
//                    .toFile(targetFile.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return renameFileName;
    }
}
