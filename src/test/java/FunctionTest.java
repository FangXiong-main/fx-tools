import com.fangxiong.common.JSONUtils;
import com.fangxiong.common.StrUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
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
                  "date" : "2026-04-14T13:34:31"
                }""";
        TestEntity testEntity = JSONUtils.jsonToBean(json, TestEntity.class);
        System.out.println(testEntity);
    }

    @Test
    public void testStrUtils(){
        String s1 = "    ";
        String s2 = " abc";
        String s3 = "a b c";
        String s4 = "";
        String s5 = "             a                  b                   c";
        String s6 = null;
        String s7 = "\t";
        String s8 = "\n";
        String s9 = "\r\n";
        String s10 = " \t\n\r ";
        String s11 = "x";
        String s12 = " 你好 ";
        String s13 = "😀";
        String s14 = "123";
        String s15 = " 1 2 3 ";
        String s16 = "~!@#$%^&*()";
        String s17 = "   ~   ";

        assertEquals(false, StrUtils.strIsNotBlank(s1));
        assertEquals(true, StrUtils.strIsNotBlank(s2));
        assertEquals(true, StrUtils.strIsNotBlank(s3));
        assertEquals(false, StrUtils.strIsNotBlank(s4));
        assertEquals(true, StrUtils.strIsNotBlank(s5));
        assertEquals(false, StrUtils.strIsNotBlank(s6));
        assertEquals(false, StrUtils.strIsNotBlank(s7));
        assertEquals(false, StrUtils.strIsNotBlank(s8));
        assertEquals(false, StrUtils.strIsNotBlank(s9));
        assertEquals(false, StrUtils.strIsNotBlank(s10));
        assertEquals(true, StrUtils.strIsNotBlank(s11));
        assertEquals(true, StrUtils.strIsNotBlank(s12));
        assertEquals(true, StrUtils.strIsNotBlank(s13));
        assertEquals(true, StrUtils.strIsNotBlank(s14));
        assertEquals(true, StrUtils.strIsNotBlank(s15));
        assertEquals(true, StrUtils.strIsNotBlank(s16));
        assertEquals(true, StrUtils.strIsNotBlank(s17));
    }
}
