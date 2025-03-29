package com.myproject.tasksystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"commentList", "author", "taskPerformer"})
@ToString(exclude = {"commentList", "author", "taskPerformer"})
@Table(name = "tasks")
@Entity(name = "Task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "priority")
    private String priority;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "performer_id")
    private User taskPerformer;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

}
