import com.fangxiong.common.JSONUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;

import java.time.LocalDateTime;

public class FunctionTest {
    @Test
    public void testJSONUtils() {
        TestEntity testEntity = new TestEntity(1,"FX",20,"male",LocalDateTime.now());
        String jsonString = JSONUtils.toJSONString(testEntity);
        System.out.println(jsonString);
    }
    @Test
    public void testJSONtoBean() throws Exception {
        String json = """
                {
                  "id" : "1",
                  "name" : "FX",
                  "age" : "20",
                  "gender" : "male",
                  "date" : "2026-04-14 13:34:31"
                }""";
        TestEntity testEntity = JSONUtils.jsonToBean(json, TestEntity.class);
        System.out.println(testEntity);
    }
}
