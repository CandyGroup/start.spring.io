package packageName.argument;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Arrays;

public enum Operator {

    EQ {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.eq(param.getField(), param.getValues()[0]);
        }
    },
    NE {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.ne(param.getField(), param.getValues()[0]);
        }
    },
    GT {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.gt(param.getField(), param.getValues()[0]);
        }
    },
    GE {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.ge(param.getField(), param.getValues()[0]);
        }
    },
    LT {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.lt(param.getField(), param.getValues()[0]);
        }
    },
    LE {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.le(param.getField(), param.getValues()[0]);
        }
    },
    BETWEEN {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.between(param.getField(), param.getValues()[0], param.getValues()[1]);
        }

        @Override
        public boolean isValid(Object[] values) {
            return values != null && values.length == 2;
        }
    },
    START_WITH {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.likeRight(param.getField(), param.getValues()[0]);
        }
    },
    LIKE {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.like(param.getField(), param.getValues()[0]);
        }
    },
    IN {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.in(param.getField(), param.getValues());
        }
    },
    IS_NULL {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.isNull(param.getField());
        }

        @Override
        public boolean isValid(Object[] values) {
            return true;
        }
    },
    IS_NOT_NULL {
        @Override
        public void accept(QueryWrapper<?> qw, FilteringParam param) {
            qw.isNotNull(param.getField());
        }

        @Override
        public boolean isValid(Object[] values) {
            return true;
        }
    };

    public abstract void accept(QueryWrapper<?> qw, FilteringParam param);

    public boolean isValid(Object[] values) {
        return values != null
            && values.length > 0
            && Arrays.stream(values)
            .allMatch(v -> {
                if (v == null) {
                    return false;
                }
                if (v instanceof String && v.toString().isEmpty()) {
                    return false;
                }
                return true;
            });
    }

}
