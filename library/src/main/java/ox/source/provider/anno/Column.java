package ox.source.provider.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author FengPeng
 * @date 2017/2/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    String name();

    FieldType type() default FieldType.TEXT;

    boolean primary() default false;

    boolean autoIncrement() default false;

    boolean notNull() default false;

    boolean unique() default false;

    int since() default 1;

    enum FieldType {NULL, INTEGER, TEXT, BLOB, REAL}
}
