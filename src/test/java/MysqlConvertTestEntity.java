import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MysqlConvertTestEntity {
    private Integer id;
    private String name;
    private Integer age;
    private Double score;
    private String favoriteSubject;
}
