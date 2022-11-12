package com.hli.projectmanagementsystem.project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class ProjectConfig {

    @Bean
    CommandLineRunner commandLineRunner(ProjectRepository repository) {
        return args -> {
            Project hailong = new Project(
                    "Hailong Li",
                    "hailong_li@gensler.com",
                    LocalDate.of(1989, 10, 7)
            );

            Project jie = new Project(
                    "Jie Chen",
                    "jie_chen@gensler.com",
                    LocalDate.of(1990, 3, 10)
            );

            repository.saveAll(List.of(hailong, jie));
        };
    }
}
