package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Maoxin
 * @ClassName IFileService
 * @date 2/14/2019
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
