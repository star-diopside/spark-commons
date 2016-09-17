package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

import java.util.Objects;

/**
 * キーと値のセットをもとにログ出力情報を生成するログパラメータクラス
 */
public class KeyValueLoggingParameter implements LoggingParameter {

    private String key;
    private String value;

    /**
     * コンストラクタ
     * 
     * @param key キー
     * @param value 値
     */
    public KeyValueLoggingParameter(String key, String value) {
        this.key = Objects.requireNonNull(key);
        this.value = value;
    }

    @Override
    public String get() {
        return key + " = " + value;
    }

    @Override
    public LoggingParameter createNestedLoggingParameter(String name) {
        return new KeyValueLoggingParameter(name + "." + key, value);
    }
}
