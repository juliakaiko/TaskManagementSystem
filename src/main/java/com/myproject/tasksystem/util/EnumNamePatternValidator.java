package com.myproject.tasksystem.util;

import com.myproject.tasksystem.annotations.EnumNamePattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Валидатор для аннотации {@link EnumNamePattern}. Проверяет, соответствует ли имя перечисления (enum)
 * заданному регулярному выражению.
 * <p>
 * Используется для проверки, что значение enum (например, {@code Role}) соответствует указанному шаблону
 * (например, "USER|ADMIN").
 * </p>
 *
 * @see EnumNamePattern
 */
public class EnumNamePatternValidator implements ConstraintValidator<EnumNamePattern, Enum<?>> {
    private Pattern pattern;

    /**
     * Инициализирует валидатор, компилируя регулярное выражение из аннотации.
     *
     * @param annotation аннотация {@link EnumNamePattern}, содержащая регулярное выражение
     */
    @Override
    public void initialize(EnumNamePattern annotation) {
        pattern = Pattern.compile(annotation.regexp());
    }

    /**
     * Проверяет, соответствует ли имя значения enum заданному регулярному выражению.
     *
     * @param value   проверяемое значение enum (может быть {@code null})
     * @param context контекст валидации (не используется в этой реализации)
     * @return {@code true}, если значение соответствует шаблону, иначе {@code false}
     */
    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return pattern.matcher(value.name()).matches();
    }
}
