public class TestEntity2 {
    private int id;
    private String name;
    private int age;
    private TestEntity Header;

    public TestEntity2(int id, int age, String name, TestEntity t) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.Header = t;
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
