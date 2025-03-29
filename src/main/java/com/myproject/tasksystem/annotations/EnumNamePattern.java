package com.myproject.tasksystem.annotations;

import com.myproject.tasksystem.util.EnumNamePatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Аннотация для валидации имён значений перечисления (enum) по регулярному выражению.
 * <p>
 * Позволяет гарантировать, что поле типа {@code enum} содержит только те значения,
 * имена которых соответствуют указанному регулярному выражению (например, "USER|ADMIN").
 * </p>
 */
@Documented
@Constraint(validatedBy = EnumNamePatternValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumNamePattern {
    /**
     * Регулярное выражение, которому должно соответствовать имя значения enum.
     * <p>
     * Например: {@code "USER|ADMIN"}.
     * </p>
     *
     * @return строка с регулярным выражением
     */
    String regexp();

    /**
     * Сообщение об ошибке, которое будет показано при невалидном значении.
     * @return сообщение об ошибке (по умолчанию: "must match \"{regexp}\"")
     */
    String message() default "must match \"{regexp}\"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
