#!/bin/bash
RESULT="linux_cold_results.csv"
echo "run,fxtools_ms,jackson_ms,gson_ms" > $RESULT

CP="fx-tools-1.0.0.jar:jackson-core-2.21.3.jar:jackson-databind-2.21.3.jar:jackson-annotations-2.21.3.jar:jackson-datatype-jsr310-2.21.3.jar:gson-2.14.0.jar:."

for i in {1..10}; do
    echo "===== 第 $i 轮 ====="
    fxtime=$(java -Xmx256m -cp "$CP" FxToolsColdStart | grep -oP '[\d.]+(?= ms)')
    jacktime=$(java -Xmx256m -cp "$CP" JacksonColdStart | grep -oP '[\d.]+(?= ms)')
    gsontime=$(java -Xmx256m -cp "$CP" GsonColdStart | grep -oP '[\d.]+(?= ms)')
    echo "$i,$fxtime,$jacktime,$gsontime" >> $RESULT
    echo "FxTools: $fxtime ms, Jackson: $jacktime ms, Gson: $gsontime ms"
done

echo "完成！"
cat $RESULT
