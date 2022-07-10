package com.myhealth.healthmanagermain.service;

import com.myhealth.healthmanagermain.domain.HealthExam;
import org.springframework.web.multipart.MultipartFile;

public interface FileHandlerService {

    HealthExam getFile(Long id);

    HealthExam storeFile(MultipartFile file);
}
