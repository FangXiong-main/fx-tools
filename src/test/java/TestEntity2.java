import com.fangxiong.annotations.GenericType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestEntity2 {
    private int id;
    private String name;
    private int age;
    private TestEntity Header;
    @GenericType(value = TestEntity.class)
    private Map<String,TestEntity> map;
    private List<Object> list;
}
