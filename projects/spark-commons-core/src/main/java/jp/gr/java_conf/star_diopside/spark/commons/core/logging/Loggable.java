package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.stream.Stream;

import jp.gr.java_conf.star_diopside.spark.commons.core.exception.ReflectiveOperationRuntimeException;

/**
 * ログ出力情報取得機能を持つクラスが実装するインタフェース
 */
public interface Loggable {

    /**
     * ログ出力パラメータのストリームを生成する。
     * 
     * @return ログ出力パラメータのストリーム
     */
    default Stream<LoggingParameter> streamLoggingObjects() {
        Stream.Builder<LoggingParameter> builder = Stream.builder();
        Class<?> clazz = getClass();

        try {
            do {
                Field[] fields = clazz.getDeclaredFields();
                String className = clazz.getSimpleName();
                AccessibleObject.setAccessible(fields, true);
                for (Field field : fields) {
                    LoggableSupport.getLoggingObject(field, this).ifPresent(
                            entry -> LoggableSupport.addLog(builder, className + "." + entry.getKey(), entry.getValue()));
                }
            } while ((clazz = clazz.getSuperclass()) != null);
        } catch (IllegalAccessException e) {
            throw new ReflectiveOperationRuntimeException(e);
        }

        return builder.build();
    }
}
