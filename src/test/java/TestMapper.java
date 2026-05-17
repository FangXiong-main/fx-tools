import com.fangxiong.mysqlUtilsCore.annotations.*;

import java.util.List;
import java.util.Map;

public interface TestMapper {
    @Select("Select * from student")
    List<Map<String,Object>> selectAllUsers();
    @Select("Select * from student where id = #{id} and name = #{name}")
    MysqlConvertTestEntity selectOne(@ParamName("id") int id,@ParamName("name") String name);
    @Insert("insert into student values(#{id},#{name},#{age},#{score},#{favoriteSubject})")
    void addNewStu(MysqlConvertTestEntity mysqlConvertTestEntity);
    @Update("update student set name = #{name},age=#{age},score=#{score},favorite_subject=#{favoriteSubject} where id = #{id}")
    void updateStu(MysqlConvertTestEntity mysqlConvertTestEntity);
    @Delete("delete from student where id = #{id}")
    void delete(Integer id);
}
