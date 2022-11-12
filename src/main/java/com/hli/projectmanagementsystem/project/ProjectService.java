package com.hli.projectmanagementsystem.project;

import com.amazonaws.services.opsworks.model.UserProfile;
import com.hli.projectmanagementsystem.bucket.BucketName;
import com.hli.projectmanagementsystem.fileStore.FileStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FileStore fileStore;


    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    public void addNewProject(Project project) {
        Optional<Project> projectOptional = projectRepository.findProjectByAddress(project.getAddress());
        if (projectOptional.isPresent()) {
            throw new IllegalStateException("address taken");
        }
        
        projectRepository.save(project);
    }

    public void deleteProject(Long projectId) {
        boolean exists = projectRepository.existsById(projectId);
        if (!exists) {
            throw new IllegalStateException(
                    "project with id " + projectId + " does not exist"
            );
        }
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public void updateProject(Long projectId, String name, String address) {
        Project target = getProjectById(projectId);

        if (name != null && name.length() > 0 && !Objects.equals(target.getName(), name))
            target.setName(name);

        if (address != null && address.length() > 0 && !Objects.equals(target.getAddress(), address)) {
            Optional<Project> projectOptional = projectRepository.findProjectByAddress(address);
            if (projectOptional.isPresent()) {
                throw new IllegalStateException("address taken");
            }
            target.setAddress(address);
        }
    }


    public void uploadUserProfileImage(Long projectId, MultipartFile file) {
        // 1. Check if image is not empty
        isFileEmpty(file);
        // 2. If file is an image
        isImage(file);

        // 3. The project exists in our database
        Project target = getProjectById(projectId);

        // 4. Grab some metadata from file if any
        Map<String, String> metadata = extractMetadata(file);

        // 5. Store the image in s3 and update database (projectProfileImageLink) with s3 image link
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), target.getId());
        String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

        try {
            fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
            target.setProfileImageLink(filename);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] downloadUserProfileImage(Long projectId) {
        Project target = getProjectById(projectId);

        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                target.getId());

        return target.getProfileImageLink()
                .map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);
    }

    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private Project getProjectById(Long projectId) {
        boolean exists = projectRepository.existsById(projectId);
        if (!exists) {
            throw new IllegalStateException(
                    "project with id " + projectId + " does not exist"
            );
        }
        return projectRepository.getReferenceById(projectId);
    }

    private void isImage(MultipartFile file) {
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [ " + file.getSize() + "]");
        }
    }
}
