package com.myproject.tasksystem.repository;

import com.myproject.tasksystem.model.Comment;
import com.myproject.tasksystem.util.CommentGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@Slf4j
@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    private static Comment expectedComment;

    @BeforeClass
    public static void setUp(){
        expectedComment = CommentGenerator.generateComment();
    }

    @Test
    public void findById() {
        this.commentRepository.save(expectedComment);
        Optional<Comment> actualComment = commentRepository.findById(1L);
        log.info("Test to find the Comment with id = 1: "+actualComment.get());
        Assert.assertNotNull(actualComment.get());
        Assert.assertEquals(expectedComment, actualComment.get());
    }

    @Test
    public void save() {
        this.commentRepository.save(expectedComment);
        Optional<Comment> actualComment =  commentRepository.findById(expectedComment.getCommentId());
        log.info("Test to save the Comment: "+actualComment.get());
        Assertions.assertEquals(expectedComment, actualComment.get());
    }

    @Test
    public void delete() {
        this.commentRepository.save(expectedComment);
        Comment actualComment =  commentRepository.findById(expectedComment.getCommentId()).get();
        commentRepository.delete(actualComment);
        log.info("Test to delete the Comment with id = 1: "+actualComment);
        Optional<Comment> deletedComment =commentRepository.findById(expectedComment.getCommentId());
        Assertions.assertFalse(deletedComment.isPresent());
    }

    @Test
    public void findAll() {
        this.commentRepository.save(expectedComment);
        log.info("Test to find all the Comments: "+ this.commentRepository.findAll());
        Assertions.assertFalse(this.commentRepository.findAll().isEmpty(),() -> "List of Comments shouldn't be empty");
    }

    @Test
    public void findAllCommentsNative() {
        this.commentRepository.save(expectedComment);
        var pageable  = PageRequest.of(0,5, Sort.by("comment_id"));
        var slice = this.commentRepository.findAllCommentsNative(pageable);
        slice.forEach(Comment -> System.out.println(Comment));
        while (slice.hasNext()){
            slice = this.commentRepository.findAllCommentsNative(slice.nextPageable());
            slice.forEach(Comment -> System.out.println(Comment));
        }
    }
    
}
