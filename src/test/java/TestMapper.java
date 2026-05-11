import java.util.List;

public interface TestMapper {

    List<MysqlConvertTestEntity> selectAllUsers();

    MysqlConvertTestEntity selectOne(int id);

}
