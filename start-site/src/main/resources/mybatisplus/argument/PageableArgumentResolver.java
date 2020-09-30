package packageName.argument;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

public class PageableArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String PARAM_PAGE = "page";
    private static final String PARAM_PAGE_SIZE = "size";

    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_PAGE_SIZE = "20";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return IPage.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public IPage<?> resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String pageString = Optional.ofNullable(webRequest.getParameter(PARAM_PAGE)).orElse(DEFAULT_PAGE);
        String pageSizeString = Optional.ofNullable(webRequest.getParameter(PARAM_PAGE_SIZE)).orElse(DEFAULT_PAGE_SIZE);
        return new Page<>(Integer.parseInt(pageString), Integer.parseInt(pageSizeString));
    }

}
