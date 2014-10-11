package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Loggable} デフォルト実装でのパラメータのログ出力制御を設定する。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogSetting {

    /**
     * ログ出力タイプを指定する。<br>
     * 未指定の場合は {@link LoggingType#INCLUDE} を使用する。
     * 
     * @return ログ出力タイプ
     */
    LoggingType value() default LoggingType.INCLUDE;

    /**
     * ログ出力時のキー名を指定する。<br>
     * 未指定の場合はフィールド名を使用する。
     * 
     * @return ログ出力キー名
     */
    String key() default "";

    /**
     * ログ出力タイプに {@link LoggingType#PROTECT} を指定した場合の出力パラメータ値を指定する。<br>
     * 未指定の場合は <code>[PROTECTED]</code> を使用する。
     * 
     * @return ログ出力パラメータ値
     */
    String protectValue() default "[PROTECTED]";

}
