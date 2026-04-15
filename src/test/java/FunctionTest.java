import com.fangxiong.common.CustomizeClazzDetector;
import com.fangxiong.common.JSONUtils;
import com.fangxiong.common.StrUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionTest {

    @Test
    public void testListToJSON() {
        // 1. List<String>
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("c");
        System.out.println("// List<String>");
        System.out.println(JSONUtils.toJSONStr(list1));
        System.out.println();

        // 2. List<Integer>
        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(10);
        list2.add(20);
        list2.add(30);
        System.out.println("// List<Integer>");
        System.out.println(JSONUtils.toJSONStr(list2));
        System.out.println();

        // 3. List<Long>
        ArrayList<Long> list3 = new ArrayList<>();
        list3.add(1000L);
        list3.add(2000L);
        list3.add(3000L);
        System.out.println("// List<Long>");
        System.out.println(JSONUtils.toJSONStr(list3));
        System.out.println();

        // 4. List<Double>
        ArrayList<Double> list4 = new ArrayList<>();
        list4.add(1.1);
        list4.add(2.2);
        list4.add(3.3);
        System.out.println("// List<Double>");
        System.out.println(JSONUtils.toJSONStr(list4));
        System.out.println();

        // 5. List<Boolean>
        ArrayList<Boolean> list5 = new ArrayList<>();
        list5.add(true);
        list5.add(false);
        list5.add(true);
        System.out.println("// List<Boolean>");
        System.out.println(JSONUtils.toJSONStr(list5));
        System.out.println();

        // 6. List<Date>
        ArrayList<LocalDateTime> list6 = new ArrayList<>();
        list6.add(LocalDateTime.now());
        System.out.println("// List<LocalDateTime>");
        System.out.println(JSONUtils.toJSONStr(list6));
        System.out.println();

        // 7. 空 List
        ArrayList<String> list7 = new ArrayList<>();
        System.out.println("// 空 List");
        System.out.println(JSONUtils.toJSONStr(list7));
        System.out.println();

        // 8. List<Object> 混合类型
        ArrayList<Object> list8 = new ArrayList<>();
        list8.add("测试");
        list8.add(100);
        list8.add(99.9);
        list8.add(true);
        list8.add(null);
        System.out.println("// List<Object> 混合");
        System.out.println(JSONUtils.toJSONStr(list8));
    }

    @Test
    public void testSingleNonCustomizeClazzJSONUtils(){
        Integer i = 1;
        Long l = 2L;
        System.out.println(JSONUtils.toJSONStr(i));
        System.out.println(JSONUtils.toJSONStr(l));
    }

    @Test
    public void testNonCustomizeClazzJSONUtils() {
        // 1. String -> String
        Map<String, String> map1 = new HashMap<>();
        map1.put("test", "test");
        map1.put("test2", "test2");
        map1.put("test3", "test3");
        System.out.println("// Map<String,String>");
        System.out.println(JSONUtils.toJSONStr(map1));
        System.out.println();

        // 2. String -> Integer
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("id", 1001);
        map2.put("age", 25);
        map2.put("code", 8888);
        System.out.println("// Map<String,Integer>");
        System.out.println(JSONUtils.toJSONStr(map2));
        System.out.println();

        // 3. String -> Boolean
        Map<String, Boolean> map3 = new HashMap<>();
        map3.put("success", true);
        map3.put("deleted", false);
        map3.put("enabled", true);
        System.out.println("// Map<String,Boolean>");
        System.out.println(JSONUtils.toJSONStr(map3));
        System.out.println();

        // 4. String -> Long
        Map<String, Long> map4 = new HashMap<>();
        map4.put("userId", 123456789L);
        map4.put("orderId", 987654321L);
        map4.put("money", 100000L);
        System.out.println("// Map<String,Long>");
        System.out.println(JSONUtils.toJSONStr(map4));
        System.out.println();

        // 5. String -> Double
        Map<String, Double> map5 = new HashMap<>();
        map5.put("price", 19.99);
        map5.put("discount", 0.88);
        map5.put("total", 9999.99);
        System.out.println("// Map<String,Double>");
        System.out.println(JSONUtils.toJSONStr(map5));
        System.out.println();

        // 6. 混合基础类型 Map<String,Object>
        Map<String, Object> map6 = new HashMap<>();
        map6.put("name", "测试数据");
        map6.put("age", 30);
        map6.put("isVip", false);
        map6.put("score", 95.5);
        map6.put("total", 10000L);
        System.out.println("// Map<String,Object> 混合基础类型");
        System.out.println(JSONUtils.toJSONStr(map6));
        System.out.println();

        // 7. 包含 null 值
        Map<String, Object> map7 = new HashMap<>();
        map7.put("data", null);
        map7.put("msg", "操作成功");
        map7.put("code", 200);
        System.out.println("// Map 包含 null");
        System.out.println(JSONUtils.toJSONStr(map7));
        System.out.println();

        // 8. 空 Map
        Map<String, String> map8 = new HashMap<>();
        System.out.println("// 空 Map");
        System.out.println(JSONUtils.toJSONStr(map8));
    }

    @Test
    public void testIsCustomizeClazz(){
        TestEntity testEntity = new TestEntity();
        Integer id = 1;
        assertEquals(true,CustomizeClazzDetector.isCustomizeClazz(testEntity.getClass()));
        assertEquals(false,CustomizeClazzDetector.isCustomizeClazz(id.getClass()));
    }

    @Test
    public void testJSONUtils() {
        TestEntity testEntity = new TestEntity(1,"FX",20,"male",LocalDateTime.now());
        TestEntity testEntity2 = new TestEntity(2,null,21,"male",LocalDateTime.now());
        String jsonString = JSONUtils.toJSONStr(testEntity);
        System.out.println(jsonString);
        System.out.println(JSONUtils.toJSONStr(testEntity2));
    }

    @Test
    public void testJSONUtils2() {
        TestEntity testEntity = new TestEntity(1,"FX",20,"male",LocalDateTime.now());
        Map<String,TestEntity> map = new HashMap<>();
        map.put("Test1",new TestEntity(20,"fx",20,"male",LocalDateTime.now()));
        ArrayList<Object> list = new ArrayList<>();
        list.add("测试");
        list.add(100);
        list.add(99.9);
        list.add(true);
        list.add(null);
        TestEntity2 testEntity2 = new TestEntity2(10,"Su",22,testEntity,map,list);
        String jsonString = JSONUtils.toJSONStr(testEntity2);
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
