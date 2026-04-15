import java.util.List;
import java.util.Map;

public class TestEntity2 {
    private int id;
    private String name;
    private int age;
    private TestEntity Header;
    private Map<String,TestEntity> map;
    private List<Object> list;

    public TestEntity2(int id, String name, int age, TestEntity header, Map<String, TestEntity> map, List<Object> list) {
        this.id = id;
        this.name = name;
        this.age = age;
        Header = header;
        this.map = map;
        this.list = list;
    }

    public TestEntity getHeader() {
        return Header;
    }

    public void setHeader(TestEntity header) {
        Header = header;
    }

    public Map<String, TestEntity> getMap() {
        return map;
    }

    public void setMap(Map<String, TestEntity> map) {
        this.map = map;
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public TestEntity2() {
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestEntity getT() {
        return Header;
    }

    public void setT(TestEntity t) {
        this.Header = t;
    }
}
