package com.myproject.tasksystem.repository;

import com.myproject.tasksystem.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select * from comments order by comments.comment_id asc", nativeQuery = true) //
    Page<Comment> findAllCommentsNative(Pageable pageable);

}
