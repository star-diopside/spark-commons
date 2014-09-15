package jp.gr.java_conf.star_diopside.spark.commons.core.logging;

import java.util.Map;

/**
 * ログ出力情報取得機能を持つクラスが実装するインタフェース
 */
public interface Loggable {

    /**
     * ログ出力用オブジェクトのマップを生成する。
     * 
     * @return ログ出力用オブジェクトのマップ
     */
    Map<String, ?> toLoggingObjects();

}
