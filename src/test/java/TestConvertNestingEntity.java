import com.fangxiong.jsonUtilsCore.annotations.NotNullClass;
import com.fangxiong.jsonUtilsCore.annotations.NotNullField;
import com.fangxiong.jsonUtilsCore.annotations.TimeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestConvertNestingEntity {
    private Integer id;
    private String name;
    private Boolean isActive;
    private BigDecimal score;
    @TimeType("yyyy:MM:dd'T'HH:mm:ss.SSS")
    private LocalDateTime createTime;
    private List<Map<String, List<Map<String, Integer>>>> ultimateTest;

}
