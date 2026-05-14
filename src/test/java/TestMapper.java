import com.fangxiong.mysqlUtilsCore.annotations.ParamName;
import com.fangxiong.mysqlUtilsCore.annotations.Select;

import java.util.List;
import java.util.Map;

public interface TestMapper {
    @Select("Select * from student")
    List<Map<String,Object>> selectAllUsers();
    @Select("Select * from student where id = #{id} and name = #{name}")
    MysqlConvertTestEntity selectOne(@ParamName("id") int id,@ParamName("name") String name);

}
