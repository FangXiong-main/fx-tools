import com.fangxiong.annotations.NotNullField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestConvertNestingEntity {
    @NotNullField
    private Integer id;
    @NotNullField
    private String name;
    @NotNullField
    private Boolean isActive;
    @NotNullField
    private Double score;
    @NotNullField
    private LocalDateTime createTime;
    @NotNullField
    private List<Map<String, List<Map<String, Integer>>>> ultimateTest;
}
