package com.myproject.tasksystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.myproject.tasksystem.model.Comment;
import com.myproject.tasksystem.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"author", "taskPerformer", "commentList"})
@ToString(exclude = {"author", "taskPerformer", "commentList"})
@Builder
public class TaskDto {

    private Long taskId;

    @NotBlank(message = "Title may not be empty")
    @Size(min=2, max=50, message = "Title size must be between 2 and 50")
    private String title;

    @NotBlank(message = "Description may not be empty")
    @Size(min=2, max=50, message = "Description size must be between 2 and 5000")
    private String description;

    /**
     * Статус задачи. Должен быть одним из следующих значений:
     * <ul>
     *     <li>"в ожидании" ("pending")</li>
     *     <li>"в процессе" ("in progress")</li>
     *     <li>"завершено" ("completed")</li>
     * </ul>
     *
     * Поле не может быть пустым (должно содержать значение).
     * Длина значения должна составлять от 2 до 20 символов.
     */
    @NotBlank(message = "Status may not be empty")
    @Size(min=2, max=50, message = "Status size must be between 2 and 20")
    private String status;

    /**
     * Приоритет задачи. Должен быть одним из следующих значений:
     * <ul>
     *     <li>"высокий" ("high")</li>
     *     <li>"средний" ("medium")</li>
     *     <li>"низкий" ("low")</li>
     * </ul>
     *
     * Поле не может быть пустым (должно содержать значение).
     * Длина значения должна составлять от 2 до 50 символов.
     */
    @NotBlank(message = "Priority may not be empty")
    @Size(min=2, max=50, message = "Priority size must be between 2 and 10")
    private String priority;

    @JsonIgnore
    private User author;

    @JsonIgnore
    private User taskPerformer;

    // Для теста changeTaskPerformer_ShouldReturnUpdatedTask()
    @JsonProperty("taskPerformerId")
    public Long getTaskPerformerId() {
        return taskPerformer != null ? taskPerformer.getUserId() : null;
    }

    private List<Comment> commentList = new ArrayList<>();
}
