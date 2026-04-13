import com.fangxiong.common.JSONUtils;
import org.junit.jupiter.api.Test;

public class FunctionTest {
    @Test
    public void testJSONUtils() {
        TestEntity testEntity = new TestEntity(1,"FX",20,"male");
        String jsonString = JSONUtils.toJSONString(testEntity);
        System.out.println(jsonString);
    }
}
