package com.aytug.book.file;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {
    public static byte[] readFileFromLocation(String fileUrl) {
        if(StringUtils.isBlank(fileUrl)){
            return null;
        }
        try {
            Path filePath = new File(fileUrl).toPath();
        } catch (Exception e) {
            log.warn("Failed to read file",e);

        }
        return null;
    }
}
