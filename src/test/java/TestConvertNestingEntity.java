import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestConvertNestingEntity {
    private Integer id;
    private String name;
    private Boolean isActive;
    private Double score;
    private List<Map<String, List<Map<String, Object>>>> ultimateTest;
}
