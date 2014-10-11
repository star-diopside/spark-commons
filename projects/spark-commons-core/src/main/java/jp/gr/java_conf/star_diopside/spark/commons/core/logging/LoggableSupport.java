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
     * オブジェクトをログパラメータに編集し、ログストリームに追加する。
     * 
     * @param builder ログパラメータを設定するストリームビルダー
     * @param itemName ログ出力項目名称
     * @param item ログ出力項目
     */
    public static void addLog(Stream.Builder<LoggingParameter> builder, String itemName, Object item) {
        if (item instanceof Loggable) {
            addLoggableToLog(builder, itemName, (Loggable) item);
        } else if (item instanceof Collection<?>) {
            addListToLog(builder, itemName, (Collection<?>) item);
        } else if (item != null && item.getClass().isArray()) {
            addArrayToLog(builder, itemName, item);
        } else {
            builder.add(new KeyValueLoggingParameter(itemName, String.valueOf(item)));
        }
    }

    /**
     * {@link Loggable}をログパラメータに編集し、ログストリームに追加する。
     * 
     * @param builder ログパラメータを設定するストリームビルダー
     * @param itemName ログ出力項目名称
     * @param item ログ出力項目
     */
    private static void addLoggableToLog(Stream.Builder<LoggingParameter> builder, String itemName, Loggable item) {
        item.streamLoggingObjects().forEach(param -> builder.add(param.createNestedLoggingParameter(itemName)));
    }

    /**
     * リスト項目をログパラメータに編集し、ログストリームに追加する。
     * 
     * @param builder ログパラメータを設定するストリームビルダー
     * @param itemName ログ出力リスト項目名称
     * @param itemList ログ出力リスト項目
     */
    private static void addListToLog(Stream.Builder<LoggingParameter> builder, String itemName, Collection<?> itemList) {
        int count = 0;
        for (Object item : itemList) {
            addLog(builder, itemName + "[" + (count++) + "]", item);
        }
    }

    /**
     * 配列項目をログパラメータに編集し、ログストリームに追加する。
     * 
     * @param builder ログパラメータを設定するストリームビルダー
     * @param itemName ログ出力配列項目名称
     * @param itemList ログ出力配列項目
     */
    private static void addArrayToLog(Stream.Builder<LoggingParameter> builder, String itemName, Object itemList) {
        int length = Array.getLength(itemList);
        for (int i = 0; i < length; i++) {
            addLog(builder, itemName + "[" + i + "]", Array.get(itemList, i));
        }
    }

    /**
     * ログ出力フィールド情報を取得する。
     * 
     * @param field ログ出力フィールド
     * @param obj ログ出力オブジェクト
     * @return ログ出力フィールドのキー名と値を格納する{@link Map.Entry} (ログ出力を行わない場合はEMPTYを返す。)
     * @throws IllegalAccessException ログ出力フィールドにアクセスできない場合
     */
    public static Optional<Map.Entry<String, Object>> getLoggingObject(Field field, Object obj)
            throws IllegalAccessException {
        LoggingSetting setting = field.getDeclaredAnnotation(LoggingSetting.class);
        if (setting == null) {
            return Optional.of(Pair.of(field.getName(), field.get(obj)));
        } else {
            return setting.value().getLoggingObject(setting, field, obj);
        }
    }
}
