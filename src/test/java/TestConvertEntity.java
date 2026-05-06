import com.fangxiong.jsonUtilsCore.annotations.NotNullField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestConvertEntity {
    @NotNullField
    private Integer id;
    private String name;
    private Map<String,Map<String,Map<String,Map<String,Map<String,Map<String,Map<String,String>>>>>>> record;
}
