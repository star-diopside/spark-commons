package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

/**
 * ログパラメータが実装するインタフェース
 */
public interface LoggingParameter {

    /**
     * ログ出力文字列を取得する。
     * 
     * @return ログ出力文字列
     */
    String get();

    /**
     * ネストした {@link Loggable} オブジェクトのログ出力パラメータを取得する。
     * 
     * @param name ログ出力キー名
     * @return ネストしたログ出力パラメータ情報
     */
    LoggingParameter createNestedLoggingParameter(String name);

}
