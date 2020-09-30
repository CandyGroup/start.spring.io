package packageName.typehandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(JSONArray.class)
public class JSONArrayTypeHandler extends BaseTypeHandler<JSONArray> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        PGobject obj = new PGobject();
        obj.setType("json");
        obj.setValue(JSON.toJSONString(parameter, SerializerFeature.WriteMapNullValue));
        ps.setObject(i, obj);
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return JSONArray.parseArray(rs.getString(columnName));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSONArray.parseArray(rs.getString(columnIndex));
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSONArray.parseArray(cs.getString(columnIndex));
    }

}
