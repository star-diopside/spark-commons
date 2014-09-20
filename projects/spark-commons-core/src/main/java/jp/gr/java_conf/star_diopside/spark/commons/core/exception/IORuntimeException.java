package jp.gr.java_conf.star_diopside.spark.commons.core.exception;

import java.io.IOException;

/**
 * {@link IOException}を原因例外とする実行時例外クラス
 */
@SuppressWarnings("serial")
public class IORuntimeException extends RuntimeException {

    /**
     * コンストラクタ
     * 
     * @param cause 原因例外
     */
    public IORuntimeException(IOException cause) {
        super(cause);
    }

    /**
     * コンストラクタ
     * 
     * @param message 例外メッセージ
     * @param cause 原因例外
     */
    public IORuntimeException(String message, IOException cause) {
        super(message, cause);
    }
}
