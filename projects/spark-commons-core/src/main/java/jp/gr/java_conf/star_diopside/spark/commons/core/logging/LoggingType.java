package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * {@link Loggable} デフォルト実装でのパラメータのログ出力タイプを指定する列挙体
 * 
 * @see Loggable
 * @see LoggableSupport
 * @see LogSetting
 */
public enum LoggingType {

    /**
     * ログ出力パラメータに含める
     */
    INCLUDE {
        @Override
        Optional<Entry<String, Object>> getLoggingObject(LogSetting setting, Field field, Object obj)
                throws IllegalAccessException {
            return Optional.of(Pair.of(getKey(setting, field), field.get(obj)));
        }
    },

    /**
     * ログ出力パラメータに含めない。
     */
    EXCLUDE {
        @Override
        Optional<Entry<String, Object>> getLoggingObject(LogSetting setting, Field field, Object obj)
                throws IllegalAccessException {
            return Optional.empty();
        }
    },

    /**
     * ログ出力パラメータに含めるが、値は表示しない。
     */
    PROTECT {
        @Override
        Optional<Entry<String, Object>> getLoggingObject(LogSetting setting, Field field, Object obj)
                throws IllegalAccessException {
            return Optional.of(Pair.of(getKey(setting, field), setting.protectValue()));
        }
    };

    /**
     * ログ出力パラメータ値を取得する。
     * 
     * @param setting ログ出力設定情報
     * @param field ログ出力パラメータ情報
     * @param obj ログ出力オブジェクト
     * @return ログ出力パラメータのキー名と値を格納する{@link Map.Entry} (ログ出力を行わない場合はEMPTYを返す。)
     * @throws IllegalAccessException ログ出力フィールドにアクセスできない場合
     */
    abstract Optional<Map.Entry<String, Object>> getLoggingObject(LogSetting setting, Field field, Object obj)
            throws IllegalAccessException;

    /**
     * ログ出力キー名を取得する。
     * 
     * @param setting ログ出力設定情報
     * @param field ログ出力パラメータ情報
     * @return ログ出力キー名
     */
    protected String getKey(LogSetting setting, Field field) {
        return StringUtils.isEmpty(setting.key()) ? field.getName() : setting.key();
    }
}
