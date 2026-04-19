import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestGetGenericTypeEntity {
    public Map<String,Map<String,Map<String,String>>> test;


    @Override
    public String toString() {
        return "TestGetGenericTypeEntity{" +
                "test=" + test +
                '}';
    }
}
