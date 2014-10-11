package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * ログ出力情報編集用ユーティリティクラス
 */
public final class LoggableSupport {

    private LoggableSupport() {
    }

    /**
     * オブジェクトをログ出力用文字列に編集し、ログストリームに追加する。
     * 
     * @param builder ログ出力用文字列を設定するストリームビルダー
     * @param itemName ログ出力項目名称
     * @param item ログ出力項目
     */
    public static void addLog(Stream.Builder<Map.Entry<String, String>> builder, String itemName, Object item) {
        if (item instanceof Loggable) {
            addLoggableToLog(builder, itemName, (Loggable) item);
        } else if (item instanceof Collection) {
            addListToLog(builder, itemName, (Collection<?>) item);
        } else if (item != null && item.getClass().isArray()) {
            addArrayToLog(builder, itemName, item);
        } else {
            builder.add(Pair.of(itemName, String.valueOf(item)));
        }
    }

    /**
     * {@link Loggable}をログ出力用文字列に編集し、ログストリームに追加する。
     * 
     * @param builder ログ出力用文字列を設定するストリームビルダー
     * @param itemName ログ出力項目名称
     * @param item ログ出力項目
     */
    private static void addLoggableToLog(Stream.Builder<Map.Entry<String, String>> builder, String itemName,
            Loggable item) {
        item.streamLoggingObjects()
                .forEach(entry -> addLog(builder, itemName + "." + entry.getKey(), entry.getValue()));
    }

    /**
     * リスト項目をログ出力用文字列に編集し、ログストリームに追加する。
     * 
     * @param builder ログ出力用文字列を設定するストリームビルダー
     * @param itemName ログ出力リスト項目名称
     * @param itemList ログ出力リスト項目
     */
    private static void addListToLog(Stream.Builder<Map.Entry<String, String>> builder, String itemName,
            Collection<?> itemList) {
        int count = 0;
        for (Object item : itemList) {
            addLog(builder, itemName + "[" + (count++) + "]", item);
        }
    }

    /**
     * 配列項目をログ出力用文字列に編集し、ログストリームに追加する。
     * 
     * @param builder ログ出力用文字列を設定するストリームビルダー
     * @param itemName ログ出力配列項目名称
     * @param itemList ログ出力配列項目
     */
    private static void addArrayToLog(Stream.Builder<Map.Entry<String, String>> builder, String itemName,
            Object itemList) {
        int length = Array.getLength(itemList);
        for (int i = 0; i < length; i++) {
            addLog(builder, itemName + "[" + i + "]", Array.get(itemList, i));
        }
    }

    /**
     * ログ出力パラメータ値を取得する。
     * 
     * @param field ログ出力パラメータ情報
     * @param obj ログ出力オブジェクト
     * @return ログ出力パラメータのキー名と値を格納する{@link Map.Entry} (ログ出力を行わない場合はEMPTYを返す。)
     * @throws IllegalAccessException ログ出力フィールドにアクセスできない場合
     */
    public static Optional<Map.Entry<String, Object>> getLoggingObject(Field field, Object obj)
            throws IllegalAccessException {
        LogSetting setting = field.getDeclaredAnnotation(LogSetting.class);
        if (setting == null) {
            return Optional.of(Pair.of(field.getName(), field.get(obj)));
        } else {
            return setting.value().getLoggingObject(setting, field, obj);
        }
    }
}
