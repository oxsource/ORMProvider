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
@Target(ElementType.TYPE)
public @interface DataBase {
    String name();

    String authority();

    Class<?>[] tables() default {};

    int since() default 1;
}
