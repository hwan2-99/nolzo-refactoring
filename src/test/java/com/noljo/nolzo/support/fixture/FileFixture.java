package com.noljo.nolzo.support.fixture;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

public class FileFixture {

    public static MultipartFile dummyImage() {
        try {
            ClassPathResource resource = new ClassPathResource("images/test.jpg");
            byte[] content = Files.readAllBytes(resource.getFile().toPath());
            return new MockMultipartFile(
                    "image",
                    "test.jpg",               
                    "image/jpeg",
                    content
            );
        } catch (IOException e) {
            throw new RuntimeException("테스트용 이미지 로드 실패", e);
        }
    }
}