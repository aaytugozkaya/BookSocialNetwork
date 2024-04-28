package com.aytug.book.file;

import com.aytug.book.book.Book;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${application.file.upload.photos-outpath-path}")
    private String fileUploadPath;
    public String saveFile(@NonNull MultipartFile sourceFile,@NonNull Integer userId) {
        final String fileUploadSubPath = "users" + File.separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(@NonNull MultipartFile sourceFile,@NonNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();
            if(!folderCreated){
                log.warn("Failed to create the target folder");
                throw new RuntimeException("Could not create folder");
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
         String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis()+ "." + fileExtension;
        Path targetPath = Path.of(targetFilePath);
        try {
            Files.write(targetPath,sourceFile.getBytes());
            log.info("File saved to {}",targetFilePath);
            return targetFilePath;}
        catch (Exception e){
            log.error("Failed to save file",e);
            throw new RuntimeException("Failed to save file");}

    }

    private String getFileExtension(String originalFilename) {
        if(originalFilename == null|| originalFilename.isEmpty())
            return "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if(lastDotIndex == -1){
            return "";
        }
        return originalFilename.substring(lastDotIndex+1).toLowerCase();
    }
}

