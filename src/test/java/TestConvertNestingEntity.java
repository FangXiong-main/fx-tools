import com.fangxiong.globalAnnotations.TimeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestConvertNestingEntity {
    private long id;
    private String name;
    private Boolean isActive;
    private BigDecimal score;
    @TimeType("yyyy:MM:dd'T'HH:mm:ss.SSS")
    private LocalDateTime createTime;
    private LocalDate testTime;
    private Date testDateTime;
    private List<Map<String, List<Map<String, Integer>>>> ultimateTest;

}
