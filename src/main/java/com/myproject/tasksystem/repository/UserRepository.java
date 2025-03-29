package com.myproject.tasksystem.repository;

import com.myproject.tasksystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = "select * from users order by users.user_id asc", nativeQuery = true)
    Page<User> findAllUsersNative(Pageable pageable);
}
