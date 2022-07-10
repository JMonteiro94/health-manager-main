package com.myhealth.healthmanagermain.service.impl;

import com.myhealth.healthmanagermain.domain.HealthExam;
import com.myhealth.healthmanagermain.exception.FileNotFoundException;
import com.myhealth.healthmanagermain.exception.FileStorageException;
import com.myhealth.healthmanagermain.repository.FileExamRepository;
import com.myhealth.healthmanagermain.service.FileHandlerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FileHandlerServiceImpl implements FileHandlerService {

    private final FileExamRepository fileExamRepository;

    @Override
    @Transactional(readOnly = true)
    public HealthExam getFile(Long id) {
        return fileExamRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found with id " + id));
    }

    @Override
    @Transactional
    public HealthExam storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            HealthExam dbFile = HealthExam.builder().name(fileName).fileType(file.getContentType()).file(file.getBytes()).build();

            return fileExamRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
