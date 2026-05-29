import com.fangxiong.globalUtils.GlobalCustomizeClazzDetector;
import com.fangxiong.jsonUtilsCore.coreUtil.CustomizeGenericTypes;
import com.fangxiong.jsonUtilsCore.enums.DecorateJson;
import com.fangxiong.utils.json.JsonUtils;
import com.fangxiong.jsonUtilsCore.coreUtil.JsonOperationUtil;
import com.fangxiong.utils.mysql.MysqlUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FunctionTest {
    //TODO add Parser for Set and more tye;
    @Test
    public void testSetType(){
        String json = "[\"java\", \"json\", \"java\", \"tool\", \"tool\", \"set\", \"list\"]";
        Object o = JsonUtils.jsonToBean(json, new CustomizeGenericTypes("Set<String>"));
        System.out.println(o.getClass());
        System.out.println(JsonUtils.beanToJson(o));
    }

    @Test
    public void testString(){
        String json = "\"hello world\"";
        String o = JsonUtils.jsonToBean(json, String.class);
        System.out.println(JsonUtils.beanToJson(o));
    }

    @Test
    public void testMysqlUtils(){
        String url = "jdbc:mysql://localhost:3306/db1?serverTimezone=UTC&useSSL=false";
        String user = "root";
        String pwd = "abc200519P";
        try {
            // 1. 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2. 获取连接
            Connection conn1 = DriverManager.getConnection(url, user, pwd);
            MysqlUtils.useMapper(conn1, TestMapper.class).selectOne(18,"Sniper' or '1'='1");
            Connection conn2 = DriverManager.getConnection(url, user, pwd);
            MysqlConvertTestEntity mysqlConvertTestEntity = new MysqlConvertTestEntity(18, "Sniper", 40, 98.0, "射击");
            long start = System.nanoTime();
            List<Map<String, Object>> maps = MysqlUtils.useMapper(conn2, TestMapper.class).selectAllUsers();
            long end = System.nanoTime();
            for (Map<String, Object> map : maps) {
                System.out.println(map);
            }
            double ms = (end - start) / 1_000_000.0;
            System.out.println("Spent : " + ms + " ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMysqlConverter(){
//        // 连接信息
//        String url = "jdbc:mysql://localhost:3306/db1?serverTimezone=UTC&useSSL=false";
//        String user = "root";
//        String pwd = "abc200519P";
//
//        try {
//            // 1. 加载驱动
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            // 2. 获取连接
//            Connection conn = DriverManager.getConnection(url, user, pwd);
//            // 3. 创建Statement
//            Statement stmt = conn.createStatement();
//            // 4. 执行查询，直接拿到 ResultSet
//            String sql = "select id,name,age,score,favorite_subject from student";
//            ResultSet rs = stmt.executeQuery(sql);
//            MysqlConvertTestEntity entityFromResultSet = MysqlUtils.getEntityFromResultSet(rs, MysqlConvertTestEntity.class, EnableCamelCaseToUnderscore.ENABLE);
//            System.out.println(entityFromResultSet);
//            // 关闭资源
//            rs.close();
//            stmt.close();
//            conn.close();
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Test
    public void testMysqlSplit(){
        System.out.println(int.class.getName());
    }


    @Test
    public void testNotNull(){
        String json = "{\n" +
                "  \"id\": 1001,\n" +
                "  \"name\": \"fx\",\n" +
                "  \"isActive\": true,\n" +
                "  \"score\": 98.5,\n" +
                "  \"createTime\": \"2025:01:01T12:00:00.123\",\n" +
                "  \"ultimateTest\": [\n" +
                "    {\n" +
                "      \"level1\": [\n" +
                "        {\n" +
                "          \"level2\": 100\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        TestConvertNestingEntity testConvertNestingEntity = JsonUtils.jsonToBean(json, TestConvertNestingEntity.class);
        System.out.println(JsonUtils.beanToJson(testConvertNestingEntity, DecorateJson.YES));
    }



    @Test
    public void optimizeGetKeysAndValuesMapWithJsonStr(){
        //List<List<String>>
        String json2 = "{\n" +
                "      \"emptyTest\": [\n" +
                "        {},\n" +
                "        [],\n" +
                "        null,\n" +
                "        {\n" +
                "          \"key\": null,\n" +
                "          \"value\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    }";
        String json = "[\n" +
                "  [\n" +
                "    [\"Java\", \"C++\", \"Go\"],\n" +
                "    [\"序列化\", \"反序列化\", \"泛型解析\"]\n" +
                "  ],\n" +
                "  [\n" +
                "    [\"局部变量类型\", \"成员变量类型\"],\n" +
                "    [\"三层集合嵌套\", \"多层递归测试\"]\n" +
                "  ],\n" +
                "  [\n" +
                "    [\"高性能\", \"轻量自研\", \"纯手写实现\"]\n" +
                "  ]\n" +
                "]";
        StringBuilder sb = new StringBuilder();
        String undecoratedJSONStr = JsonOperationUtil.getUndecoratedJSONStr(json2);
        System.out.println(undecoratedJSONStr);
        Map<String, String> splitMainJsonToPartlyMap = JsonOperationUtil.getSplitMainJsonToPartlyMap(sb, undecoratedJSONStr);
        Map<String, String> keysAndValuesMapWithJsonStr = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(undecoratedJSONStr);
        System.out.println(keysAndValuesMapWithJsonStr);
        System.out.println(sb.toString());
        System.out.println(splitMainJsonToPartlyMap);
    }

    @Test
    public void testCustomGenericTypes(){
        List<Map<String, List<Map<String, Object>>>> localJsonData;
        CustomizeGenericTypes c =new CustomizeGenericTypes("Map<String,String>");
//        System.out.println(c.getRawType());
//        System.out.println(c.getActualTypeArguments()[0]);
//        System.out.println(c.getActualTypeArguments().length);
//        Type actualTypeArgument = c.getActualTypeArguments()[1];
//        ParameterizedType t1 =(ParameterizedType) actualTypeArgument;
//        System.out.println(t1.getRawType());
//        Type actualTypeArgument1 = t1.getActualTypeArguments()[1];
//        System.out.println(t1.getActualTypeArguments()[0]);
//        ParameterizedType t2 =(ParameterizedType) actualTypeArgument1;
//        System.out.println(t2.getRawType());
//        System.out.println(t2.getActualTypeArguments()[0]);
    }

    @Test
    public void testJsonToLocalVariable(){
        String json="[\n" +
                "  {\n" +
                "    \"group1\": [\n" +
                "      {\n" +
                "        \"id\": 1001,\n" +
                "        \"name\": \"测试数据1\",\n" +
                "        \"enable\": true,\n" +
                "        \"price\": 19.99,\n" +
                "        \"info\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1002,\n" +
                "        \"name\": \"测试数据2\",\n" +
                "        \"enable\": false,\n" +
                "        \"price\": 29.99\n" +
                "      }\n" +
                "    ],\n" +
                "    \"group2\": [\n" +
                "      {\n" +
                "        \"code\": \"SUCCESS\",\n" +
                "        \"msg\": \"操作成功\",\n" +
                "        \"data\": {\n" +
                "          \"key1\": \"value1\",\n" +
                "          \"key2\": 123\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"groupA\": [\n" +
                "      {\n" +
                "        \"level\": 1,\n" +
                "        \"type\": \"A\",\n" +
                "        \"children\": [1, 2, 3]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"groupB\": [\n" +
                "      {\n" +
                "        \"valid\": true,\n" +
                "        \"ext\": {}\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";
        Map<String, String> keysAndValuesMapWithJsonStr = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(JsonOperationUtil.getUndecoratedJSONStr(json));
        System.out.println(keysAndValuesMapWithJsonStr);
        List<Map<String, List<Map<String, Object>>>> localNestedData;
        List<Map<String, List<Map<String, Object>>>> o = (List<Map<String, List<Map<String, Object>>>>)JsonUtils.jsonToBean(json, new CustomizeGenericTypes("List<Map<String, List<Map<String, Object>>>>"));
        System.out.println(o.getClass().getTypeName());
        System.out.println(o);
        System.out.println(JsonUtils.beanToJson(o));
    }

    //optimizeMapConverterToSupportObjectType
    @Test
    public void finalTestConvertMapWithObject(){
        String json = "{\n" +
                "  \"id\": 100000,\n" +
                "  \"name\": \"真·终极最终测试\",\n" +
                "  \"isActive\": true,\n" +
                "  \"score\": 99.999,\n" +
                "  \"ultimateTest\": [\n" +
                "    {\n" +
                "      \"finalGroupA\": [\n" +
                "        {\n" +
                "          \"normalStr\": \"最后一轮\",\n" +
                "          \"normalInt\": 999999,\n" +
                "          \"normalBool\": false,\n" +
                "          \"normalDouble\": 12345.6789,\n" +
                "          \"nullValue\": null,\n" +
                "          \"deepMap\": {\n" +
                "            \"a\": {\n" +
                "              \"b\": {\n" +
                "                \"c\": \"最深层结束\",\n" +
                "                \"d\": 100\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"arrayAllType\": [0, false, \"\", null, 9.9, { \"key\": \"数组内MAP\" }],\n" +
                "          \"multiLayerArray\": [[1, true, null], [\"字符串\", 2.2, false]]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"finalGroupB\": [\n" +
                "        {\n" +
                "          \"lastObj\": {\n" +
                "            \"finalField\": \"完美收官\",\n" +
                "            \"finalCode\": 200,\n" +
                "            \"finalFlag\": true\n" +
                "          },\n" +
                "          \"endNull\": null\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        FinalTestEntity finalTestEntity = JsonUtils.jsonToBean(json, FinalTestEntity.class);
        System.out.println(finalTestEntity);
        System.out.println(JsonUtils.beanToJson(finalTestEntity,DecorateJson.YES));
    }


//    @Test
//    public void testConvertMapWithObject(){
//        String json = "{\n" +
//                "  \"id\": 9999,\n" +
//                "  \"name\": \"超深度嵌套Object测试\",\n" +
//                "  \"isActive\": true,\n" +
//                "  \"score\": 99.9,\n" +
//                "  \"deepTest\": [\n" +
//                "    {\n" +
//                "      \"level1\": [\n" +
//                "        {\n" +
//                "          \"level2\": [\n" +
//                "            {\n" +
//                "              \"level3\": \"字符串\",\n" +
//                "              \"level4\": 666,\n" +
//                "              \"level5\": true,\n" +
//                "              \"level6\": 3.14,\n" +
//                "              \"level7\": null\n" +
//                "            }\n" +
//                "          ]\n" +
//                "        }\n" +
//                "      ]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"A\": [\n" +
//                "        {\n" +
//                "          \"B\": [\n" +
//                "            {\n" +
//                "              \"C\": \"测试\",\n" +
//                "              \"D\": 123,\n" +
//                "              \"E\": false,\n" +
//                "              \"F\": 9.9\n" +
//                "            }\n" +
//                "          ]\n" +
//                "        }\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
//        TestConvertNestingEntity testConvertNestingEntity = JsonUtils.jsonToBean(json, TestConvertNestingEntity.class);
//        System.out.println(testConvertNestingEntity);
//        System.out.println(JsonUtils.BeanToJson(testConvertNestingEntity));
//    }

    @Test
    public void testGetKeyAndValueFromJson(){
        String s = "\"testArray\": [\n" +
                "            {\n" +
                "              \"key\": \"数组内Map\"\n" +
                "            }\n" +
                "          ]";
        System.out.println(JsonOperationUtil.getUndecoratedJSONStr(s));
        String json = "[{\"key\":\"数组内Map\"}]";
        String undecoratedJSONStr = JsonOperationUtil.getUndecoratedJSONStr(json);
        Map<String, String> jsonKeysAndValuesWithPartlyMap = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(undecoratedJSONStr);
        System.out.println(jsonKeysAndValuesWithPartlyMap);
    }

    @Test
    public void testTwoDifferentTool(){
        String json1 = "[[\n" +
                "    {\n" +
                "      \"finalTest1\": [\n" +
                "        {\n" +
                "          \"field1\": \"最后测试\",\n" +
                "          \"field2\": 88888,\n" +
                "          \"field3\": true,\n" +
                "          \"field4\": 1234.567,\n" +
                "          \"field5\": null,\n" +
                "          \"deepNested\": {\n" +
                "            \"levelA\": {\n" +
                "              \"levelB\": {\n" +
                "                \"levelC\": \"究极深层\",\n" +
                "                \"levelD\": 666\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"complexArray\": [ null, false, \"文本\", 9.9, { \"key\": \"数组内对象\" } ],\n" +
                "          \"arrayInArray\": [ [1,2,3], [true, false, null] ]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"finalTest2\": [\n" +
                "        {\n" +
                "          \"lastObj\": {\n" +
                "            \"name\": \"结束\",\n" +
                "            \"value\": 100,\n" +
                "            \"flag\": true,\n" +
                "            \"decimal\": 0.0001\n" +
                "          },\n" +
                "          \"finalNull\": null\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "]";
        String json = JsonOperationUtil.getUndecoratedJSONStr(json1);
        System.out.println(json);
        Map<String, String> m1 = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(json);
        System.out.println(m1);
        StringBuilder sb = new StringBuilder();
        System.out.println(JsonOperationUtil.getSplitMainJsonToPartlyMap(sb, json));

    }

    @Test
    public void testConvertJsonValuesToList(){
        String json = "[\n" +
                "            null,\n" +
                "            false,\n" +
                "            \"文本\",\n" +
                "            9.9,\n" +
                "            {\n" +
                "              \"key\": \"数组内对象\"\n" +
                "            }\n" +
                "          ]";
        ArrayList<String> convertJsonValueListToArr = JsonOperationUtil.getConvertJsonValueListToArr(JsonOperationUtil.getUndecoratedJSONStr(json));
        System.out.println(convertJsonValueListToArr.size());
        System.out.println(convertJsonValueListToArr);
    }

    @Test
    public void testSplitJson(){
        String json = "[\n" +
                "  [\"Java\", \"Python\", \"C++\"],\n" +
                "  [\"足球\", \"篮球\", \"乒乓球\"],\n" +
                "  [\"电影\", \"音乐\", \"阅读\"]\n" +
                "]";
        StringBuilder sb = new StringBuilder();
        String undecoratedJSONStr = JsonOperationUtil.getUndecoratedJSONStr(json);
        Map<String, String> splitMainJsonToPartlyMap = JsonOperationUtil.getSplitMainJsonToPartlyMap(sb, undecoratedJSONStr);
        System.out.println(splitMainJsonToPartlyMap);
    }

    @Test
    public void nestingGenericConvertTest(){
        String json = "{\n" +
                "  \"id\": 88888,\n" +
                "  \"name\": \"强类型终极测试\",\n" +
                "  \"isStudent\": true,\n" +
                "  \"score\": 98.5,\n" +
                "  \"ultimateTest\": [\n" +
                "    {\n" +
                "      \"分类A\": [\n" +
                "        {\n" +
                "          \"项目1\": [\"Java\", \"JSON\", \"序列化\"],\n" +
                "          \"项目2\": [\"递归\", \"泛型\", \"反射\"]\n" +
                "        },\n" +
                "        {\n" +
                "          \"项目3\": [\"性能\", \"嵌套\", \"工具类\"]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"分类B\": [\n" +
                "        {\n" +
                "          \"项目4\": [\"终极测试\", \"强类型\", \"无Object\"]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        TestConvertNestingNonObjEntity testConvertNestingNonObjEntity = JsonUtils.jsonToBean(json, TestConvertNestingNonObjEntity.class);
        System.out.println(testConvertNestingNonObjEntity);
        System.out.println(JsonUtils.beanToJson(testConvertNestingNonObjEntity));
    }

//    @Test
//    public void testConvertList(){
//        String json = "{\n" +
//                "  \"id\": 1001,\n" +
//                "  \"name\": \"张三\",\n" +
//                "  \"hobby\": [\n" +
//                "    [\"篮球\", \"编程\"],\n" +
//                "    [\"阅读\", \"音乐\"],\n" +
//                "    [\"旅行\", \"电影\"]\n" +
//                "  ]\n" +
//                "}";
//        TestConvertNestingEntity testConvertNestingEntity = JsonUtils.jsonToBean(json, TestConvertNestingEntity.class);
//        System.out.println(testConvertNestingEntity);
//        String jsonStr = JsonUtils.BeanToJson(testConvertNestingEntity);
//        System.out.println(jsonStr);
//    }

    @Test
    public void testSplitPart2(){
        String json = "{\n" +
                "  \"hobby\": [\n" +
                "    [\"跑步\", \"游泳\"],\n" +
                "    [\"看书\", \"写字\"]\n" +
                "  ]\n" +
                "}";
        StringBuilder sb = new StringBuilder();
        Map<String, String> splitMainJsonToPartlyMap = JsonOperationUtil.getSplitMainJsonToPartlyMap(sb, JsonOperationUtil.getUndecoratedJSONStr(json));
        System.out.println(sb);
        System.out.println(splitMainJsonToPartlyMap);
    }
    private Map<String,Map<String,String>> test;
    @Test
    public void testSplitPart3(){
        String json = "{\n" +
                "  \"test\": {\n" +
                "    \"group1\": {\n" +
                "      \"key1\": \"value1\",\n" +
                "      \"key2\": \"value2\"\n" +
                "    },\n" +
                "    \"group2\": {\n" +
                "      \"name\": \"张三\",\n" +
                "      \"age\": \"20\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        StringBuilder sb = new StringBuilder();
        Map<String, String> splitMainJsonToPartlyMap = JsonOperationUtil.getSplitMainJsonToPartlyMap(sb, JsonOperationUtil.getUndecoratedJSONStr(json));
        System.out.println(sb);
        System.out.println(splitMainJsonToPartlyMap);
    }

    @Test
    public void testConvertMap(){
        String json = "{\n" +
                "  \"id\": 1001,\n" +
                "  \"name\": \"七层等量复杂测试\",\n" +
                "  \"record\": {\n" +
                "    \"L1_A\": {\n" +
                "      \"L2_X\": {\n" +
                "        \"L3_1\": {\n" +
                "          \"L4_ONE\": {\n" +
                "            \"L5_TOP\": {\n" +
                "              \"L6_SUN\": {\n" +
                "                \"L7_MSG1\": \"七层测试内容1\",\n" +
                "                \"L7_MSG2\": \"七层测试内容2\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"L1_B\": {\n" +
                "      \"L2_Y\": {\n" +
                "        \"L3_2\": {\n" +
                "          \"L4_TWO\": {\n" +
                "            \"L5_BOTTOM\": {\n" +
                "              \"L6_MOON\": {\n" +
                "                \"L7_NOTE\": \"七层多节点压力测试\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        TestConvertEntity testConvertEntity = JsonUtils.jsonToBean(json, TestConvertEntity.class);
        System.out.println(testConvertEntity);
        System.out.println(JsonUtils.beanToJson(testConvertEntity,DecorateJson.YES));
//        StringBuilder sb= new StringBuilder();
//        Map<String, String> splitMainJsonToPartlyMap = StrUtils.getSplitMainJsonToPartlyMap(sb, NonGenericTypeConverterFactory.getUndecoratedJSONStr(json));

//        StringBuilder sb = new StringBuilder();
//        Map<String, String> splitMainJsonToPartlyMap = StrUtils.getSplitMainJsonToPartlyMap(sb, NonGenericTypeConverterFactory.getUndecoratedJSONStr(json));
//        System.out.println(splitMainJsonToPartlyMap);
//        String s = "{\"group1\":{\"key1\":\"value1\",\"key2\":\"value2\"},\"group2\":{\"remark\":\"测试备注\",\"info\":\"详细信息\"}}";
//        Map<String, String> splitMainJsonToPartlyMap1 = StrUtils.getSplitMainJsonToPartlyMap(sb, s);
//        System.out.println(splitMainJsonToPartlyMap1);
    }

    @Test
    public void testGetGenericType(){
        Deque<Type> convertRawTypesDeque = new ArrayDeque<>();
        Deque<Type[]> convertActualTypesDeque = new ArrayDeque<>();
        try {
            Field test = TestGetGenericTypeEntity.class.getDeclaredField("test");
            if(test.getGenericType() instanceof ParameterizedType pt){
                while(true){
                    Type[] actualTypeArguments = pt.getActualTypeArguments();
                    if(actualTypeArguments.length==2){
                        convertRawTypesDeque.add(pt.getRawType());
                        convertActualTypesDeque.add(pt.getActualTypeArguments());
                        if(!(actualTypeArguments[1] instanceof ParameterizedType)){
                            break;
                        }else {
                            pt = (ParameterizedType) pt.getActualTypeArguments()[1];
                        }
                    }else{
                        convertRawTypesDeque.add(pt.getRawType());
                        convertActualTypesDeque.add(pt.getActualTypeArguments());
                        if(!(actualTypeArguments[0] instanceof ParameterizedType)){
                            break;
                        }else {
                            pt = (ParameterizedType) pt.getActualTypeArguments()[0];
                        }
                    }
                }
            }
            for(int i=0;i<convertRawTypesDeque.size();i++){
                System.out.println(convertRawTypesDeque.poll());
                System.out.println(Arrays.toString(convertActualTypesDeque.poll()));
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSplitJSON(){
        String json ="{\"id\":10,\"name\":\"Su\",\"age\":22,\"Header\":{\"id\":1,\"name\":\"FX\",\"age\":20,\"gender\":\"male\",\"date\":\"2026-04-17T20:51:06\"},\"map\":{\"Test1\":{\"id\":20,\"name\":\"fx\",\"age\":20,\"gender\":\"male\",\"date\":\"2026-04-17T20:51:06\"}},\"list\":[\"测试\",100,99.9,true,null]}";
        String json2 = "[\"Header\":{\"id\":1,\"name\":\"FX\",\"age\":20,\"gender\":\"male\",\"date\":\"2026-04-17T20:51:06\"},\"map\":{\"id\":20,\"name\":\"fx\",\"age\":20,\"gender\":\"male\",\"date\":\"2026-04-17T20:51:06\"}]";
        StringBuilder sb = new StringBuilder();
        Map<String, String> splitMainEntityAndFieldEntity = JsonOperationUtil.getSplitMainJsonToPartlyMap(sb,json);
        System.out.println(JsonOperationUtil.getKeysAndValuesMapWithJsonStr(sb.toString()));
        System.out.println(sb);
        System.out.println(splitMainEntityAndFieldEntity);
        System.out.println(splitMainEntityAndFieldEntity.keySet());
    }


    @Test
    public void testRemoveEmptyFromJSON(){
        String test = """
                {
                  "id": 10,
                  "name": "Su",
                  "age": 22,
                  "Header": {
                    "id": 1,
                    "name": "FX",
                    "age": 20,
                    "gender": "male",
                    "date": "2026-04-17T20:51:06"
                  },
                  "map": {
                    "Test1": {
                      "id": 20,
                      "name": "fx",
                      "age": 20,
                      "gender": "male",
                      "date": "2026-04-17T20:51:06"
                    }
                  },
                  "list": [
                    "测试",
                    100,
                    99.9,
                    true,
                    null
                  ]
                }""";
        System.out.println(JsonOperationUtil.getUndecoratedJSONStr(test));
    }

    @Test
    public void beanToJSONFinalTest2(){
        TestEntity testEntity = new TestEntity(1,"FX",20,"male",LocalDateTime.now());
        Map<String,TestEntity> map = new HashMap<>();
        map.put("Test1",new TestEntity(20,"fx",20,"male",LocalDateTime.now()));
        TestEntity2 testEntity2 = new TestEntity2(10,"Su",22,testEntity,map,null);
        String json = JsonUtils.beanToJson(testEntity2);
        System.out.println(json);
    }

    @Test
    public void beanToJSONFinalTest(){
        // 1. 基础实体 TestEntity
        TestEntity testEntity = new TestEntity(1, "hello\"Java\\Json\n测试\t制表符'单引号'", 20, "男", LocalDateTime.now());
        System.out.println(JsonUtils.beanToJson(testEntity));

        // 2. 复杂嵌套实体 TestEntity2
        TestEntity header = new TestEntity(2, "子对象\n内容", 18, "女", LocalDateTime.now());

        Map<String, TestEntity> map = new HashMap<>();
        map.put("user1", new TestEntity(3, "Map内部实体", 25, "未知", LocalDateTime.now()));

        List<Object> list = new ArrayList<>();
        list.add(999);
        list.add("列表字符串\"转义测试\"");
        list.add(3.14);
        list.add(Boolean.TRUE);
        list.add(new TestEntity(4, "List里的实体", 30, "男", LocalDateTime.now()));

        TestEntity2 testEntity2 = new TestEntity2(1001, "嵌套测试<>&\"特殊字符'", 22, header, map, list);
        System.out.println(JsonUtils.beanToJson(testEntity2));

        // 3. ArrayList 混合类型（无 null，安全版）
        List<Object> arrayList = new ArrayList<>();
        arrayList.add(10);
        arrayList.add(20.5);
        arrayList.add("换行\n测试\\反斜杠");
        arrayList.add(Integer.MAX_VALUE);
        System.out.println(JsonUtils.beanToJson(arrayList));

        // 4. Map 混合类型
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("title", "标题\"双引号测试\"");
        testMap.put("data", new TestEntity(5, "map-value实体", 28, "女", LocalDateTime.now()));
        testMap.put("num", 666);
        System.out.println(JsonUtils.beanToJson(testMap));

        // 5. 字符串高强度转义测试
        String testStr = "\"首尾双引号\\中间反斜杠\n换行\r回车\t制表\b退格\u0001隐藏符测试";
        System.out.println(JsonUtils.beanToJson(testStr));

        // 6. 普通数字
        Integer num = 9527;
        System.out.println(JsonUtils.beanToJson(num));

        // 7. 普通布尔
        Boolean flag = true;
        System.out.println(JsonUtils.beanToJson(flag));
    }


    @Test
    public void testListToJSON() {
        // 1. List<String>
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("c");
        System.out.println("// List<String>");
        System.out.println(JsonUtils.beanToJson(list1));
        System.out.println();

        // 2. List<Integer>
        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(10);
        list2.add(20);
        list2.add(30);
        System.out.println("// List<Integer>");
        System.out.println(JsonUtils.beanToJson(list2));
        System.out.println();

        // 3. List<Long>
        ArrayList<Long> list3 = new ArrayList<>();
        list3.add(1000L);
        list3.add(2000L);
        list3.add(3000L);
        System.out.println("// List<Long>");
        System.out.println(JsonUtils.beanToJson(list3));
        System.out.println();

        // 4. List<Double>
        ArrayList<Double> list4 = new ArrayList<>();
        list4.add(1.1);
        list4.add(2.2);
        list4.add(3.3);
        System.out.println("// List<Double>");
        System.out.println(JsonUtils.beanToJson(list4));
        System.out.println();

        // 5. List<Boolean>
        ArrayList<Boolean> list5 = new ArrayList<>();
        list5.add(true);
        list5.add(false);
        list5.add(true);
        System.out.println("// List<Boolean>");
        System.out.println(JsonUtils.beanToJson(list5));
        System.out.println();

        // 6. List<Date>
        ArrayList<LocalDateTime> list6 = new ArrayList<>();
        list6.add(LocalDateTime.now());
        System.out.println("// List<LocalDateTime>");
        System.out.println(JsonUtils.beanToJson(list6));
        System.out.println();

        // 7. 空 List
        ArrayList<String> list7 = new ArrayList<>();
        System.out.println("// 空 List");
        System.out.println(JsonUtils.beanToJson(list7));
        System.out.println();

        // 8. List<Object> 混合类型
        ArrayList<Object> list8 = new ArrayList<>();
        list8.add("测试");
        list8.add(100);
        list8.add(99.9);
        list8.add(true);
        list8.add(null);
        System.out.println("// List<Object> 混合");
        System.out.println(JsonUtils.beanToJson(list8));
    }

    @Test
    public void testSingleNonCustomizeClazzJSONUtils(){
        Integer i = 1;
        Long l = 2L;
        System.out.println(JsonUtils.beanToJson(i));
        System.out.println(JsonUtils.beanToJson(l));
    }

    @Test
    public void testNonCustomizeClazzJSONUtils() {
        // 1. String -> String
        Map<String, String> map1 = new HashMap<>();
        map1.put("test", "test");
        map1.put("test2", "test2");
        map1.put("test3", "test3");
        System.out.println("// Map<String,String>");
        System.out.println(JsonUtils.beanToJson(map1));
        System.out.println();

        // 2. String -> Integer
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("id", 1001);
        map2.put("age", 25);
        map2.put("code", 8888);
        System.out.println("// Map<String,Integer>");
        System.out.println(JsonUtils.beanToJson(map2));
        System.out.println();

        // 3. String -> Boolean
        Map<String, Boolean> map3 = new HashMap<>();
        map3.put("success", true);
        map3.put("deleted", false);
        map3.put("enabled", true);
        System.out.println("// Map<String,Boolean>");
        System.out.println(JsonUtils.beanToJson(map3));
        System.out.println();

        // 4. String -> Long
        Map<String, Long> map4 = new HashMap<>();
        map4.put("userId", 123456789L);
        map4.put("orderId", 987654321L);
        map4.put("money", 100000L);
        System.out.println("// Map<String,Long>");
        System.out.println(JsonUtils.beanToJson(map4));
        System.out.println();

        // 5. String -> Double
        Map<String, Double> map5 = new HashMap<>();
        map5.put("price", 19.99);
        map5.put("discount", 0.88);
        map5.put("total", 9999.99);
        System.out.println("// Map<String,Double>");
        System.out.println(JsonUtils.beanToJson(map5));
        System.out.println();

        // 6. 混合基础类型 Map<String,Object>
        Map<String, Object> map6 = new HashMap<>();
        map6.put("name", "测试数据");
        map6.put("age", 30);
        map6.put("isVip", false);
        map6.put("score", 95.5);
        map6.put("total", 10000L);
        System.out.println("// Map<String,Object> 混合基础类型");
        System.out.println(JsonUtils.beanToJson(map6));
        System.out.println();

        // 7. 包含 null 值
        Map<String, Object> map7 = new HashMap<>();
        map7.put("data", null);
        map7.put("msg", "操作成功");
        map7.put("code", 200);
        System.out.println("// Map 包含 null");
        System.out.println(JsonUtils.beanToJson(map7));
        System.out.println();

        // 8. 空 Map
        Map<String, String> map8 = new HashMap<>();
        System.out.println("// 空 Map");
        System.out.println(JsonUtils.beanToJson(map8));
    }

    @Test
    public void testIsCustomizeClazz(){
        TestEntity testEntity = new TestEntity();
        Integer id = 1;
        assertEquals(true, GlobalCustomizeClazzDetector.isCustomizeClazz(testEntity.getClass()));
        assertEquals(false, GlobalCustomizeClazzDetector.isCustomizeClazz(id.getClass()));
    }

    @Test
    public void testJSONUtils() {
        TestEntity testEntity = new TestEntity(1,"FX",20,"male",LocalDateTime.now());
        TestEntity testEntity2 = new TestEntity(2,null,21,"male",LocalDateTime.now());
        String jsonString = JsonUtils.beanToJson(testEntity);
        System.out.println(jsonString);
        System.out.println(JsonUtils.beanToJson(testEntity2));
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
        String jsonString = JsonUtils.beanToJson(testEntity2);
        System.out.println(jsonString);
    }

    @Test
    public void testGetJSONKeysAndValues2() {
        String json = "{\"date\":\"2026-04-14T13:34:31\"}";
        JsonOperationUtil.getKeysAndValuesMapWithJsonStr(json);
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

        assertEquals(false, JsonOperationUtil.strIsNotBlank(s1));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s2));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s3));
        assertEquals(false, JsonOperationUtil.strIsNotBlank(s4));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s5));
        assertEquals(false, JsonOperationUtil.strIsNotBlank(s6));
        assertEquals(false, JsonOperationUtil.strIsNotBlank(s7));
        assertEquals(false, JsonOperationUtil.strIsNotBlank(s8));
        assertEquals(false, JsonOperationUtil.strIsNotBlank(s9));
        assertEquals(false, JsonOperationUtil.strIsNotBlank(s10));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s11));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s12));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s13));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s14));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s15));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s16));
        assertEquals(true, JsonOperationUtil.strIsNotBlank(s17));
    }
}
