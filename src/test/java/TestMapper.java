import com.fangxiong.mysqlUtilsCore.annotations.ParamName;
import com.fangxiong.mysqlUtilsCore.annotations.Select;

import java.util.List;

public interface TestMapper {

    List<MysqlConvertTestEntity> selectAllUsers();
    @Select("Select * from student where id = #{id} and name = #{name}")
    MysqlConvertTestEntity selectOne(@ParamName("id") int id,@ParamName("name") String name);

}
