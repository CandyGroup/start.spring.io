package packageName.argument;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class QueryWrapperArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String PARAM_SORT = "sort";
    private static final String PARAM_DIRECTION = "order";
    private static final String PARAM_FILTERING = "filtering";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Wrapper.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Wrapper<?> resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws UnsupportedEncodingException {
        List<FilteringParam> params = new ArrayList<>();

        QueryWrapperDecorator queryWrapperDecorator = methodParameter.getParameterAnnotation(QueryWrapperDecorator.class);
        if (queryWrapperDecorator != null) {
            FilteringParam filteringParam = new FilteringParam()
                .setOperator(queryWrapperDecorator.operator())
                .setLogic(queryWrapperDecorator.logic())
                .setField(queryWrapperDecorator.field());
            if (queryWrapperDecorator.booleans().length > 0) {
                Boolean[] booleans = new Boolean[queryWrapperDecorator.booleans().length];
                Arrays.setAll(booleans, i -> queryWrapperDecorator.booleans()[i]);
                filteringParam.setValues(booleans);
            } else if (queryWrapperDecorator.strings().length > 0) {
                filteringParam.setValues(queryWrapperDecorator.strings());
            } else if (queryWrapperDecorator.ints().length > 0) {
                Integer[] ints = new Integer[queryWrapperDecorator.ints().length];
                Arrays.setAll(ints, i -> queryWrapperDecorator.ints()[i]);
                filteringParam.setValues(ints);
            }
            params.add(filteringParam);
        }

        String parameter = webRequest.getParameter(PARAM_FILTERING);
        if (StringUtils.hasText(parameter)) {
            params.addAll(JSONArray.parseArray(URLDecoder.decode(parameter, "UTF-8"), FilteringParam.class));
        }
        QueryWrapper<?> queryWrapper = params.isEmpty() ? new QueryWrapper<>() : FilteringParam.toQueryWrapper(params);

        String sortParameter = webRequest.getParameter(PARAM_SORT);
        if (StringUtils.isEmpty(sortParameter)) {
            return queryWrapper;
        }

        String[] sorts = Arrays.stream(sortParameter.split(",")).toArray(String[]::new);

        Direction.valueOf(
            Optional.ofNullable(webRequest.getParameter(PARAM_DIRECTION)).orElse(Direction.desc.name()))
            .setSortProperties(queryWrapper, sorts);

        return queryWrapper;
    }

}
