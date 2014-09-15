package jp.gr.java_conf.star_diopside.spark.commons.core.interceptor;

import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import jp.gr.java_conf.star_diopside.spark.commons.core.logging.Loggable;

/**
 * オブジェクトの詳細情報をログに出力するインターセプター
 */
@SuppressWarnings("serial")
public class LoggingObjectDetailsInterceptor extends LoggingInterceptor {

    @Override
    protected Stream<?> streamLoggingObjects(Object obj) {
        if (obj instanceof Loggable) {
            Builder<String> builder = Stream.builder();
            ((Loggable) obj).toLoggingObjects().forEach((key, value) -> {
                addLog(builder, key, value);
            });
            return builder.build();
        } else {
            return super.streamLoggingObjects(obj);
        }
    }

    /**
     * オブジェクトをログ出力用文字列に編集し、ログストリームに追加する。
     * 
     * @param builder ログ出力用文字列を設定するストリームビルダー
     * @param itemName ログ出力項目名称
     * @param item ログ出力項目
     */
    private static void addLog(Builder<String> builder, String itemName, Object item) {
        if (item instanceof Loggable) {
            addLoggableToLog(builder, itemName, (Loggable) item);
        } else if (item instanceof Collection) {
            addListToLog(builder, itemName, (Collection<?>) item);
        } else {
            builder.add(itemName + " = " + item);
        }
    }

    /**
     * {@link Loggable}をログ出力用文字列に編集し、ログストリームに追加する。
     * 
     * @param builder ログ出力用文字列を設定するストリームビルダー
     * @param itemName ログ出力項目名称
     * @param item ログ出力項目
     */
    private static void addLoggableToLog(Builder<String> builder, String itemName, Loggable item) {
        item.toLoggingObjects().forEach((key, value) -> {
            addLog(builder, itemName + "." + key, value);
        });
    }

    /**
     * リスト項目をログ出力用文字列に編集し、ログストリームに追加する。
     * 
     * @param builder ログ出力用文字列を設定するストリームビルダー
     * @param itemName ログ出力リスト項目名称
     * @param itemList ログ出力リスト項目
     */
    private static void addListToLog(Builder<String> builder, String itemName, Collection<?> itemList) {
        int count = 0;
        for (Object item : itemList) {
            addLog(builder, itemName + "[" + (count++) + "]", item);
        }
    }
}
