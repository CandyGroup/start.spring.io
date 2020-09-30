package packageName.argument;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class FilteringParam {

    private String field;

    private Operator operator;

    private Object[] values;

    private Logic logic = Logic.AND;

    private FilteringParamConverterType converterType;

    public static <T> QueryWrapper<T> toQueryWrapper(List<FilteringParam> params) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        if (params != null && !params.isEmpty()) {
            Map<Logic, List<FilteringParam>> filteringParamMap = params.stream()
                .filter(p -> p.getField() != null && p.getOperator().isValid(p.getValues()))
                .peek(p -> {
                    if (p.getConverterType() != null) {
                        p.setValues(Arrays.stream(p.getValues())
                            .map(a -> p.getConverterType().getConverter().convert(a.toString()))
                            .toArray());
                    }
                })
                .collect(Collectors.groupingBy(FilteringParam::getLogic));

            List<FilteringParam> andFilteringPrams = filteringParamMap.get(Logic.AND);
            List<FilteringParam> orFilteringPrams = filteringParamMap.get(Logic.OR);
            boolean andFilteringParamsExists = andFilteringPrams != null && !andFilteringPrams.isEmpty();
            boolean orFilteringParamsExists = orFilteringPrams != null && !orFilteringPrams.isEmpty();

            if (andFilteringParamsExists) {
                andFilteringPrams.forEach(p -> p.getOperator().accept(queryWrapper, p));
            }
            if (orFilteringParamsExists) {
                if (andFilteringParamsExists) {
                    queryWrapper.and(qw -> orFilteringPrams.forEach(p -> {
                        qw.or();
                        p.getOperator().accept(qw, p);
                    }));
                } else {
                    orFilteringPrams.forEach(p -> {
                        queryWrapper.or();
                        p.getOperator().accept(queryWrapper, p);
                    });
                }
            }
        }
        return queryWrapper;
    }

}
