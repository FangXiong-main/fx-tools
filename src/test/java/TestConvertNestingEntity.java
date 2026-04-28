import com.fangxiong.annotations.IgnoredField;
import com.fangxiong.annotations.NotNullClass;
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
@NotNullClass
public class TestConvertNestingEntity {
    private Integer id;
    private String name;
    private Boolean isActive;
    private Double score;
    private LocalDateTime createTime;
    private List<Map<String, List<Map<String, Integer>>>> ultimateTest;
}
