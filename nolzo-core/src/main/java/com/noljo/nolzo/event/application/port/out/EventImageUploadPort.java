package com.noljo.nolzo.event.application.port.out;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface EventImageUploadPort {

    String upload(MultipartFile multipartFile, String dirName) throws IOException;
}
