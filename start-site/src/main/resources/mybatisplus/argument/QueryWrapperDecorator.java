package packageName.argument;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface QueryWrapperDecorator {

    String field();

    Operator operator() default Operator.EQ;

    Logic logic() default Logic.AND;

    boolean[] booleans() default {};

    String[] strings() default {};

    int[] ints() default {};

}
