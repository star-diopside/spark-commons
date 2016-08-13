package jp.gr.java_conf.star_diopside.spark.commons.core.exception;

import java.util.Objects;

/**
 * {@link ReflectiveOperationException}をラップする非検査例外クラス
 */
@SuppressWarnings("serial")
public class UncheckedReflectiveOperationException extends RuntimeException {

    /**
     * コンストラクタ
     * 
     * @param message 例外メッセージ
     * @param cause {@link ReflectiveOperationException}
     */
    public UncheckedReflectiveOperationException(String message, ReflectiveOperationException cause) {
        super(message, Objects.requireNonNull(cause));
    }

    /**
     * コンストラクタ
     * 
     * @param cause {@link ReflectiveOperationException}
     */
    public UncheckedReflectiveOperationException(ReflectiveOperationException cause) {
        super(Objects.requireNonNull(cause));
    }

    @Override
    public ReflectiveOperationException getCause() {
        return (ReflectiveOperationException) super.getCause();
    }
}
