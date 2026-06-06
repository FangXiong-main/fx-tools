import com.fangxiong.utils.json.JsonUtils;
import com.fangxiong.jsonUtilsCore.enums.SafetyCheckLevel;

public class FxComplianceV2 {
    public static void main(String[] args) throws Exception {
        String json = new String(java.nio.file.Files.readAllBytes(
            java.nio.file.Path.of(args[0])));
        try {
            JsonUtils.jsonToBean(json, java.util.Map.class, SafetyCheckLevel.SKIP);
            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getClass().getSimpleName());
        }
    }
}
