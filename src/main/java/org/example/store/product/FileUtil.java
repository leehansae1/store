package org.example.store.product;

import lombok.extern.slf4j.Slf4j;
import org.example.store.product.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class FileUtil {


    public static String saveAndRenameFile(MultipartFile file, String folderPathStr) {

        String originalFileName = file.getOriginalFilename(); //원본파일의 이름
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 비어 있습니다.");
        }

        String onlyFileName //파일 확장자 분리
                = file.getOriginalFilename().substring(0, originalFileName.lastIndexOf("."));
        String extension
                = file.getOriginalFilename().substring(originalFileName.lastIndexOf("."));

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss"); //파일 이름 변경
        String writeTime = LocalDateTime.now().format(format);
        String renameFileName = onlyFileName + "_" + writeTime + extension;

        DateTimeFormatter folderFormat = DateTimeFormatter.ofPattern("yyyyMMdd");  //폴더는 하루 단위로 생성
        String folderName = LocalDateTime.now().format(folderFormat);

        //파일 저장 경로 설정 (upload/product/날짜)
        Path timeFolder = Paths.get(folderPathStr, "product", folderName);
        try {
            if (Files.notExists(timeFolder)) { //날짜별 폴더가 없으면 생성 >> 하루에 한 번만 생성됨
                Files.createDirectories(timeFolder);
                System.out.println("새로운 날짜 폴더 생성: " + timeFolder);
            }

            Path targetFile = timeFolder.resolve(renameFileName); //경로설정
            file.transferTo(targetFile); //transferTo를 통해 설정한 경로로 파일을 옮김

            System.gc(); //파일 잠김 해제 (Windows 대응)

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생" + e);
        }
        return "/upload/product/" + folderName + "/" + renameFileName;
    }

    public static void deleteFile(Image image, String thumbnailPath) {
        log.info("thumbnailPath: {}", thumbnailPath);
        List<String> imageStrList = Image.toStrList(image);
        log.info("imageStrList: {}", imageStrList);
        try {
            Files.deleteIfExists(Path.of("C:\\" + thumbnailPath));
            for (String imageStr : imageStrList) {
                Files.deleteIfExists(Path.of("C:\\" + imageStr)); //나머지도
                log.info("이미지 파일 삭제");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

