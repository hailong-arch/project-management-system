package com.hli.projectmanagementsystem.project;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/project")

@AllArgsConstructor

@CrossOrigin("*")

public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<Project> getProjects() {
        return projectService.getProjects();
    }

    @PostMapping
    public void registerNewProject(@RequestBody Project project) {
        projectService.addNewProject(project);
    }

    @DeleteMapping(path = "{projectId}")
    public void deleteProject(@PathVariable("projectId") Long projectId) {
        projectService.deleteProject(projectId);
    }

    @PutMapping(path = "{projectId}")
    public void updateProject(@PathVariable("projectId") Long projectId,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) String address) {
        projectService.updateProject(projectId, name, address);
    }


    @PostMapping(
            path = "{projectId}/image/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadUserProfileImage(@PathVariable("projectId") Long projectId,
                                       @RequestParam("file") MultipartFile file) {
        projectService.uploadUserProfileImage(projectId, file);
    }

    @GetMapping("{projectId}/image/download")
    public byte[] downloadUserProfileImage(@PathVariable("projectId") Long projectId) {
        return projectService.downloadUserProfileImage(projectId);
    }
}
