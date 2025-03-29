package com.myproject.tasksystem.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // @RequiredArgsConstructor (final fields)+@ToString+@EqualsAndHashCode+@Getter+@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorItem {

    private String message;
    private String timestamp;

}
