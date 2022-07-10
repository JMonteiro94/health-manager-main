package com.myhealth.healthmanagermain.web.rest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.myhealth.healthmanagermain.domain.HealthExam;
import com.myhealth.healthmanagermain.service.FileHandlerService;
import com.myhealth.healthmanagermain.web.rest.model.FileResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@AllArgsConstructor
public class FileUploadController {

    private final FileHandlerService fileStorageService;

    @PostMapping("/uploadFile")
    public FileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        HealthExam fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName.getName())
                .toUriString();

        return FileResponse.builder()
                .fileName(fileName.getName())
                .fileDownloadUri(fileDownloadUri)
                .size(file.getSize())
                .fileType(file.getContentType())
                .build();
    }

    @PostMapping("/uploadMultipleFiles")
    public List<FileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }
}
