package packageName.typehandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
@MappedTypes(JSONObject.class)
public class JSONTypeHandler extends BaseTypeHandler<JSONObject> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject parameter,
                                    JdbcType jdbcType) throws SQLException {
        PGobject obj = new PGobject();
        obj.setType("json");
        obj.setValue(JSON.toJSONString(parameter, SerializerFeature.WriteMapNullValue));
        ps.setObject(i, obj);
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnStr = rs.getString(columnName);
        return JSONObject.parseObject(columnStr);
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSONObject.parseObject(rs.getString(columnIndex));
    }

    @Override
    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSONObject.parseObject(cs.getString(columnIndex));
    }
}
