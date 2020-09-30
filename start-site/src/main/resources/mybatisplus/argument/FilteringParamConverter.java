package packageName.argument;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface FilteringParamConverter<T> {

    T convert(String raw);

    class LocalDateTimeConverter implements FilteringParamConverter<LocalDateTime> {

        private static final LocalDateTimeConverter INSTANCE = new LocalDateTimeConverter();

        public static LocalDateTimeConverter getInstance() {
            return INSTANCE;
        }

        @Override
        public LocalDateTime convert(String raw) {
            return LocalDateTime.parse(raw);
        }
    }

    class ZonedDateTimeConverter implements FilteringParamConverter<ZonedDateTime> {

        private static final ZonedDateTimeConverter INSTANCE = new ZonedDateTimeConverter();

        public static ZonedDateTimeConverter getInstance() {
            return INSTANCE;
        }

        @Override
        public ZonedDateTime convert(String raw) {
            return Instant.parse(raw).atZone(ZoneId.systemDefault());
        }
    }

    class InstantConverter implements FilteringParamConverter<Instant> {

        private static final InstantConverter INSTANCE = new InstantConverter();

        public static FilteringParamConverter<Instant> getInstance() {
            return INSTANCE;
        }

        @Override
        public Instant convert(String raw) {
            return Instant.parse(raw);
        }
    }

}

