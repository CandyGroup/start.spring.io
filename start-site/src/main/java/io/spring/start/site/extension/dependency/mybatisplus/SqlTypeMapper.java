package io.spring.start.site.extension.dependency.mybatisplus;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jy
 */
public class SqlTypeMapper {

    private static final Map<String, Class<?>> TYPE_MAPPER = new HashMap<>(1 << 8);

    private static final Map<Class<?>, String> TYPE_HANDLER_MAPPER = new HashMap<>();

    static {
        TYPE_MAPPER.put("int4", Integer.class);
        TYPE_MAPPER.put("integer", Integer.class);
        TYPE_MAPPER.put("serial", Integer.class);
        TYPE_MAPPER.put("int8", Long.class);
        TYPE_MAPPER.put("bigint", Long.class);
        TYPE_MAPPER.put("bigserial", Long.class);

        TYPE_MAPPER.put("varchar", String.class);
        TYPE_MAPPER.put("text", String.class);

        TYPE_MAPPER.put("timestamp", Instant.class);
        TYPE_MAPPER.put("date", LocalDate.class);

        TYPE_MAPPER.put("numeric", BigDecimal.class);
        TYPE_MAPPER.put("float8", BigDecimal.class);

        TYPE_MAPPER.put("boolean", Boolean.class);

        TYPE_MAPPER.put("json", JSONObject.class);
        TYPE_MAPPER.put("jsonb", JSONObject.class);


        TYPE_HANDLER_MAPPER.put(JSONObject.class, "JSONTypeHandler");
        TYPE_HANDLER_MAPPER.put(JSONArray.class, "JSONArrayTypeHandler");
        TYPE_HANDLER_MAPPER.put(Long[].class, "LongArrayTypeHandler");
        TYPE_HANDLER_MAPPER.put(Integer[].class, "IntegerArrayTypeHandler");
        TYPE_HANDLER_MAPPER.put(String[].class, "StringArrayTypeHandler");
    }

    public static Class<?> getType(String sqlType) {
        return TYPE_MAPPER.get(sqlType);
    }

    public static String getTypeHandler(Class<?> type) {
        return TYPE_HANDLER_MAPPER.get(type);
    }
}
