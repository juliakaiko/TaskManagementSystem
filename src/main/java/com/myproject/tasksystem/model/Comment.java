package com.myproject.tasksystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"task", "user"})
@ToString(exclude = {"task", "user"})
@Table(name = "comments") // говорим Hibernate, с какой именно таблицей необходимо связать (map) данный класс.
@Entity(name = "Comment")// на этот объект будет мапиться SQL
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_id")
    private Long commentId;

    @Column(name="text")
    private String text;

    //@JsonIgnore - чтобы API возвращало задачи с комментариями без бесконечной вложенности
    @ManyToOne
    @JoinColumn(name = "task_id")
    @JsonIgnore
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
