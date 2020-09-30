package packageName.argument;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public enum Direction {
    asc {
        @Override
        public void setSortProperties(QueryWrapper<?> wrapper, String[] sort) {
            wrapper.orderByAsc(sort);
        }
    },
    desc {
        @Override
        public void setSortProperties(QueryWrapper<?> wrapper, String[] sort) {
            wrapper.orderByDesc(sort);
        }
    };

    public abstract void setSortProperties(QueryWrapper<?> wrapper, String[] sort);

}
