package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Stream;

import jp.gr.java_conf.star_diopside.spark.commons.core.exception.ReflectiveOperationRuntimeException;

/**
 * ログ出力情報取得機能を持つクラスが実装するインタフェース
 */
public interface Loggable {

    /**
     * ログ出力用文字列のストリームを生成する。
     * 
     * @return ログ出力用文字列のストリーム
     */
    default Stream<Map.Entry<String, String>> streamLoggingObjects() {
        Stream.Builder<Map.Entry<String, String>> builder = Stream.builder();
        Class<?> clazz = getClass();

        try {
            do {
                Field[] fields = clazz.getDeclaredFields();
                AccessibleObject.setAccessible(fields, true);
                for (Field field : fields) {
                    LoggableSupport.getLoggingObject(field, this).ifPresent(
                            entry -> LoggableSupport.addLog(builder, entry.getKey(), entry.getValue()));
                }
            } while ((clazz = clazz.getSuperclass()) != null);
        } catch (IllegalAccessException e) {
            throw new ReflectiveOperationRuntimeException(e);
        }

        return builder.build();
    }
}
