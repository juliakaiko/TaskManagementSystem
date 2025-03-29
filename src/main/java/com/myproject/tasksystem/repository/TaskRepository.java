package com.myproject.tasksystem.repository;

import com.myproject.tasksystem.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor_UserId (Long authorId);

    List<Task> findByTaskPerformer_UserId(Long performerId);

    @Query(value = "select * from tasks order by tasks.task_id asc", nativeQuery = true) //nativeQuery = true -> SQL
    Page<Task> findAllTasksNative(Pageable pageable);
}
