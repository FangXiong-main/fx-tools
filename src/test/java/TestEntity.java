import com.fangxiong.jsonUtilsCore.annotations.TimeType;

import java.time.LocalDateTime;

public class TestEntity {
    private int id;
    private String name;
    private int age;
    private String gender;
    @TimeType("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    public TestEntity() {
    }

    public TestEntity(int id, String name, int age, String gender, LocalDateTime date) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", date=" + date +
                '}';
    }
}
