package com.myproject.tasksystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (exclude = {"createdTasks", "assignedTasks"})
@ToString(exclude = {"createdTasks", "assignedTasks"})
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")})
@Entity(name = "User")
public class User implements UserDetails { //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Enumerated(EnumType.STRING) // указывает, что поле типа enum должно быть сохранено в базе данных в виде строки
    @Column(name="role")
    private Role role;

    //При удалении пользователя задачи, где он был автором, удалятся
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)// orphanRemoval = true
    @JsonIgnore
    private List<Task> createdTasks = new ArrayList<>();

    //При удалении пользователя задачи, где он был исполнителем, НЕ удалятся
    @OneToMany(mappedBy = "taskPerformer", fetch = FetchType.LAZY,cascade = CascadeType.MERGE, orphanRemoval = false)
    @JsonIgnore
    private List<Task> assignedTasks = new ArrayList<>();

    //При удалении исполнителя задачи вместо ссылки id появится null
    @PreRemove
    private void preRemove() {
        if (this.assignedTasks != null)
            assignedTasks.forEach(assignedTask -> assignedTask.setTaskPerformer(null));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
