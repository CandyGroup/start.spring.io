package packageName.argument;

/**
 * @author jy
 */
public enum FilteringParamConverterType {

    LOCAL_DATE_TIME {
        @Override
        public FilteringParamConverter<?> getConverter() {
            return FilteringParamConverter.LocalDateTimeConverter.getInstance();
        }
    },

    ZONED_DATE_TIME {
        @Override
        public FilteringParamConverter<?> getConverter() {
            return FilteringParamConverter.ZonedDateTimeConverter.getInstance();
        }
    },

    INSTANT {
        @Override
        public FilteringParamConverter<?> getConverter() {
            return FilteringParamConverter.InstantConverter.getInstance();
        }
    };

    public abstract FilteringParamConverter<?> getConverter();
}
