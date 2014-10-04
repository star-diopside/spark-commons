package jp.gr.java_conf.star_diopside.spark.commons.core.interceptor;

import java.util.stream.Stream;

import jp.gr.java_conf.star_diopside.spark.commons.core.logging.Loggable;

/**
 * オブジェクトの詳細情報をログに出力するインターセプター
 */
@SuppressWarnings("serial")
public class LoggingObjectDetailsInterceptor extends LoggingInterceptor {

    @Override
    protected Stream<?> streamLoggingObjects(Object obj) {
        if (obj instanceof Loggable) {
            return ((Loggable) obj).streamLoggingObjects().map(entry -> entry.getKey() + " = " + entry.getValue());
        } else {
            return super.streamLoggingObjects(obj);
        }
    }
}
