package com.hli.projectmanagementsystem.project;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Entity
@Table

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode

public class Project {
    @Id

    @SequenceGenerator(
            name = "project_sequence",
            sequenceName = "project_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_sequence"
    )

    private Long id;
    private String name;
    private String address;
    private LocalDate startDate;
    @Transient
    private Integer year;

    private String profileImageLink; // S3 Key


    public Project(String name,
                   String address,
                   LocalDate startDate) {
        this.name = name;
        this.address = address;
        this.startDate = startDate;
    }

    public Integer getYear() {
        return Period.between(this.startDate, LocalDate.now()).getYears();
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Optional<String> getProfileImageLink() {
        return Optional.ofNullable(profileImageLink);
    }
}
