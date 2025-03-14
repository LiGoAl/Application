package com.example.springbootguide.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "department")
@NoArgsConstructor
@Getter
public class Department {
    @Id
    @SequenceGenerator(
            name = "department_sequence",
            sequenceName = "department_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "department_sequence"
    )
    @Positive(message = "Id must be greater than 0")
    private Long id;
    @Setter
    @NotBlank(message = "Name can't be empty")
    private String name;
    @JsonManagedReference
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Setter
    private List<Employee> employees;

    public Department(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
