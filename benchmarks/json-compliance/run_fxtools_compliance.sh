#!/bin/bash

FXTOOLS_JAR="/Users/fangxiong/Desktop/fx-tools-1.0.0.jar"

if [ ! -f "$FXTOOLS_JAR" ]; then
    echo "找不到 $FXTOOLS_JAR"
    exit 1
fi

cat > FxComplianceTest.java << 'JAVA'
import com.fangxiong.utils.json.JsonUtils;
import com.fangxiong.jsonUtilsCore.enums.SafetyCheckLevel;
import java.nio.file.Files;
import java.nio.file.Path;

public class FxComplianceTest {
    public static void main(String[] args) throws Exception {
        String file = args[0];
        String json = Files.readString(Path.of(file));
        try {
            Object obj = JsonUtils.jsonToBean(json, Object.class, SafetyCheckLevel.NORMAL);
            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL: " + e.getClass().getSimpleName());
        }
    }
}
JAVA

javac -cp "$FXTOOLS_JAR" FxComplianceTest.java

Y_TOTAL=0; Y_PASS=0
N_TOTAL=0; N_PASS=0
I_TOTAL=0; I_PASS=0; I_FAIL=0

echo "===== FxTools JSON Compliance Test ====="
echo ""

echo "--- 合法 JSON (y_*.json) ---"
for f in test_parsing/y_*.json; do
    Y_TOTAL=$((Y_TOTAL + 1))
    result=$(java -cp ".:$FXTOOLS_JAR" FxComplianceTest "$f" 2>&1)
    if echo "$result" | grep -q "PASS"; then
        Y_PASS=$((Y_PASS + 1))
    else
        echo "  失败: $f → $result"
    fi
done
echo "  结果: $Y_PASS / $Y_TOTAL 通过"
echo ""

echo "--- 非法 JSON (n_*.json) ---"
for f in test_parsing/n_*.json; do
    N_TOTAL=$((N_TOTAL + 1))
    result=$(java -cp ".:$FXTOOLS_JAR" FxComplianceTest "$f" 2>&1)
    if echo "$result" | grep -q "FAIL"; then
        N_PASS=$((N_PASS + 1))
    else
        echo "  未拒绝: $f"
    fi
done
echo "  结果: $N_PASS / $N_TOTAL 拒绝"
echo ""

echo "--- 实现定义 (i_*.json) ---"
for f in test_parsing/i_*.json; do
    I_TOTAL=$((I_TOTAL + 1))
    result=$(java -cp ".:$FXTOOLS_JAR" FxComplianceTest "$f" 2>&1)
    if echo "$result" | grep -q "PASS"; then
        I_PASS=$((I_PASS + 1))
    else
        I_FAIL=$((I_FAIL + 1))
    fi
done
echo "  接受: $I_PASS, 拒绝: $I_FAIL (共 $I_TOTAL 个)"
echo ""

echo "===== 总结 ====="
echo "合法 JSON 通过率: $Y_PASS / $Y_TOTAL"
echo "非法 JSON 拒绝率: $N_PASS / $N_TOTAL"

rm -f FxComplianceTest.java FxComplianceTest.class
