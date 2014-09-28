package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import jp.gr.java_conf.star_diopside.spark.commons.core.exception.ReflectiveOperationRuntimeException;

/**
 * ログ出力情報取得機能を持つクラスが実装するインタフェース
 */
public interface Loggable {

    /**
     * ログ出力用オブジェクトのマップを生成する。
     * 
     * @return ログ出力用オブジェクトのマップ
     */
    default Map<String, ?> toLoggingObjects() {
        Map<String, Object> data = new HashMap<>();
        Class<?> clazz = getClass();

        try {
            do {
                Field[] fields = clazz.getDeclaredFields();
                AccessibleObject.setAccessible(fields, true);
                for (Field field : fields) {
                    data.put(field.getName(), field.get(this));
                }
            } while ((clazz = clazz.getSuperclass()) != null);
        } catch (IllegalAccessException e) {
            throw new ReflectiveOperationRuntimeException(e);
        }

        return data;
    }
}
