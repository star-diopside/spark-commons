package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

/**
 * {@link Loggable} デフォルト実装でのパラメータのログ出力タイプを指定する列挙体
 * 
 * @see Loggable
 * @see LoggableSupport
 * @see LogSetting
 */
public enum LoggingType {

    /** ログ出力パラメータに含める。 */
    INCLUDE,

    /** ログ出力パラメータに含めない。 */
    EXCLUDE,

    /** ログ出力パラメータに含めるが、値は表示しない。 */
    PROTECT

}
