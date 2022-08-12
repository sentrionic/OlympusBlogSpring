package com.github.sentrionic.olympusblog.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.github.sentrionic.olympusblog.config.AppProperties;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final AmazonS3Client client;
    private final AppProperties appProperties;

    public String uploadArticleImage(MultipartFile file, String directory) throws IOException {
        var metadata = new ObjectMetadata();
        metadata.setContentType("image/jpg");
        var key = String.format("files/%s.jpg", directory);

        var image = ImageIO.read(file.getInputStream());
        var outputStream = new ByteArrayOutputStream();

        int dimMin = 320;
        if (image.getHeight() < dimMin || image.getWidth() < dimMin) {
            Thumbnails.of(image).size(dimMin, dimMin).keepAspectRatio(true).outputFormat("jpg")
                    .toOutputStream(outputStream);
        } else {
            int dimMax = 1080;
            Thumbnails.of(image).size(dimMax, dimMax).keepAspectRatio(true).outputFormat("jpg")
                    .toOutputStream(outputStream);
        }

        client.putObject(
                appProperties.getAwsStorageBucketName(),
                key,
                new ByteArrayInputStream(outputStream.toByteArray()),
                metadata
        );

        client.setObjectAcl(appProperties.getAwsStorageBucketName(), key, CannedAccessControlList.PublicRead);

        outputStream.close();

        return client.getResourceUrl(appProperties.getAwsStorageBucketName(), key);
    }

    public String uploadAvatarImage(MultipartFile file, String directory) throws IOException {
        var metadata = new ObjectMetadata();
        metadata.setContentType("image/jpg");
        var key = String.format("files/%s.jpg", directory);

        var outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream()).size(150, 150).keepAspectRatio(true)
                .toOutputStream(outputStream);

        client.putObject(
                appProperties.getAwsStorageBucketName(),
                key,
                new ByteArrayInputStream(outputStream.toByteArray()),
                metadata
        );

        client.setObjectAcl(appProperties.getAwsStorageBucketName(), key, CannedAccessControlList.PublicRead);

        outputStream.close();

        return client.getResourceUrl(appProperties.getAwsStorageBucketName(), key);
    }
}
