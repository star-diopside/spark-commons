package jp.gr.java_conf.star_diopside.spark.commons.test.exception;

/**
 * テスト例外クラス
 */
@SuppressWarnings("serial")
public class TestException extends RuntimeException {

    /**
     * コンストラクタ
     */
    public TestException() {
        super();
    }

    /**
     * コンストラクタ
     * 
     * @param message 例外メッセージ
     */
    public TestException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     * 
     * @param cause 原因例外
     */
    public TestException(Throwable cause) {
        super(cause);
    }

    /**
     * コンストラクタ
     * 
     * @param message 例外メッセージ
     * @param cause 原因例外
     */
    public TestException(String message, Throwable cause) {
        super(message, cause);
    }
}
