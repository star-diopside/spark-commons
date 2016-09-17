package jp.gr.java_conf.star_diopside.spark.commons.core.interceptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractTraceInterceptor;

/**
 * ログ出力インターセプター
 */
@SuppressWarnings("serial")
public class LoggingInterceptor extends AbstractTraceInterceptor {

    /** メソッド名のデフォルトプレースホルダー */
    private static final String DEFAULT_PLACEHOLDER_METHOD_NAME = "$[methodName]";

    /** 実行対象クラスの完全修飾名のデフォルトプレースホルダー */
    private static final String DEFAULT_PLACEHOLDER_TARGET_CLASS_NAME = "$[targetClassName]";

    /** 実行対象クラスの単純名のデフォルトプレースホルダー */
    private static final String DEFAULT_PLACEHOLDER_TARGET_CLASS_SHORT_NAME = "$[targetClassShortName]";

    /** 戻り値のデフォルトプレースホルダー */
    private static final String DEFAULT_PLACEHOLDER_RETURN_VALUE = "$[returnValue]";

    /** メソッド仮引数型のデフォルトプレースホルダー */
    private static final String DEFAULT_PLACEHOLDER_ARGUMENT_TYPES = "$[argumentTypes]";

    /** メソッド引数インデックスのデフォルトプレースホルダー */
    private static final String DEFAULT_PLACEHOLDER_ARGUMENT_INDEX = "$[argumentIndex]";

    /** メソッド実引数値のデフォルトプレースホルダー */
    private static final String DEFAULT_PLACEHOLDER_ARGUMENT_VALUE = "$[argumentValue]";

    /** 例外メッセージのデフォルトプレースホルダー */
    private static final String DEFAULT_PLACEHOLDER_EXCEPTION = "$[exception]";

    /** 実行時間(ミリ秒)のデフォルトプレースホルダー */
    private static final String DEFAULT_PLACEHOLDER_INVOCATION_TIME = "$[invocationTime]";

    /** プレースホルダーの正規表現 */
    private static final Pattern PATTERN_PLACEHOLDER = Pattern.compile("\\$\\[\\p{Alnum}+\\]");

    /** 開始ログフォーマットのデフォルト値 */
    private static final String DEFAULT_ENTER_MESSAGE = "[START] " + DEFAULT_PLACEHOLDER_TARGET_CLASS_NAME + "."
            + DEFAULT_PLACEHOLDER_METHOD_NAME + "(" + DEFAULT_PLACEHOLDER_ARGUMENT_TYPES + ")";

    /** 終了ログフォーマットのデフォルト値 */
    private static final String DEFAULT_EXIT_MESSAGE = "[END] " + DEFAULT_PLACEHOLDER_TARGET_CLASS_NAME + "."
            + DEFAULT_PLACEHOLDER_METHOD_NAME + "(" + DEFAULT_PLACEHOLDER_ARGUMENT_TYPES + ") : invocationTime = "
            + DEFAULT_PLACEHOLDER_INVOCATION_TIME + " [ms]";

    /** 例外ログフォーマットのデフォルト値 */
    private static final String DEFAULT_EXCEPTION_MESSAGE = "Exception thrown in method '"
            + DEFAULT_PLACEHOLDER_METHOD_NAME + "' of class [" + DEFAULT_PLACEHOLDER_TARGET_CLASS_NAME + "], thrown "
            + DEFAULT_PLACEHOLDER_EXCEPTION;

    /** メソッド引数ログフォーマットのデフォルト値 */
    private static final String DEFAULT_ARGUMENTS_MESSAGE = "INPUT[" + DEFAULT_PLACEHOLDER_ARGUMENT_INDEX + "]: "
            + DEFAULT_PLACEHOLDER_ARGUMENT_VALUE;

    /** 戻り値ログフォーマットのデフォルト値 */
    private static final String DEFAULT_RESULT_MESSAGE = "OUTPUT: " + DEFAULT_PLACEHOLDER_RETURN_VALUE;

    /** メソッド名のプレースホルダー */
    private String placeholderMethodName = DEFAULT_PLACEHOLDER_METHOD_NAME;

    /** 実行対象クラスの完全修飾名のプレースホルダー */
    private String placeholderTargetClassName = DEFAULT_PLACEHOLDER_TARGET_CLASS_NAME;

    /** 実行対象クラスの単純名のプレースホルダー */
    private String placeholderTargetClassShortName = DEFAULT_PLACEHOLDER_TARGET_CLASS_SHORT_NAME;

    /** 戻り値のプレースホルダー */
    private String placeholderReturnValue = DEFAULT_PLACEHOLDER_RETURN_VALUE;

    /** メソッド仮引数型のプレースホルダー */
    private String placeholderArgumentTypes = DEFAULT_PLACEHOLDER_ARGUMENT_TYPES;

    /** メソッド引数インデックスのプレースホルダー */
    private String placeholderArgumentIndex = DEFAULT_PLACEHOLDER_ARGUMENT_INDEX;

    /** メソッド実引数値のプレースホルダー */
    private String placeholderArgumentValue = DEFAULT_PLACEHOLDER_ARGUMENT_VALUE;

    /** 例外メッセージのプレースホルダー */
    private String placeholderException = DEFAULT_PLACEHOLDER_EXCEPTION;

    /** 実行時間(ミリ秒)のプレースホルダー */
    private String placeholderInvocationTime = DEFAULT_PLACEHOLDER_INVOCATION_TIME;

    /** 開始ログフォーマット */
    private String enterMessage = DEFAULT_ENTER_MESSAGE;

    /** 終了ログフォーマット */
    private String exitMessage = DEFAULT_EXIT_MESSAGE;

    /** 例外ログフォーマット */
    private String exceptionMessage = DEFAULT_EXCEPTION_MESSAGE;

    /** メソッド引数ログフォーマット */
    private String argumentsMessage = DEFAULT_ARGUMENTS_MESSAGE;

    /** 戻り値ログフォーマット */
    private String resultMessage = DEFAULT_RESULT_MESSAGE;

    /**
     * メソッド名のプレースホルダーを設定する。
     * 
     * @param placeholderMethodName メソッド名のプレースホルダー
     */
    public void setPlaceholderMethodName(String placeholderMethodName) {
        if (!PATTERN_PLACEHOLDER.matcher(placeholderMethodName).matches()) {
            throw new IllegalArgumentException("Parameter don't match the pattern of the placeholder.");
        }
        this.placeholderMethodName = placeholderMethodName;
    }

    /**
     * 実行対象クラスの完全修飾名のプレースホルダーを設定する。
     * 
     * @param placeholderTargetClassName 実行対象クラスの完全修飾名のプレースホルダー
     */
    public void setPlaceholderTargetClassName(String placeholderTargetClassName) {
        if (!PATTERN_PLACEHOLDER.matcher(placeholderTargetClassName).matches()) {
            throw new IllegalArgumentException("Parameter don't match the pattern of the placeholder.");
        }
        this.placeholderTargetClassName = placeholderTargetClassName;
    }

    /**
     * 実行対象クラスの単純名のプレースホルダーを設定する。
     * 
     * @param placeholderTargetClassShortName 実行対象クラスの単純名のプレースホルダー
     */
    public void setPlaceholderTargetClassShortName(String placeholderTargetClassShortName) {
        if (!PATTERN_PLACEHOLDER.matcher(placeholderTargetClassShortName).matches()) {
            throw new IllegalArgumentException("Parameter don't match the pattern of the placeholder.");
        }
        this.placeholderTargetClassShortName = placeholderTargetClassShortName;
    }

    /**
     * 戻り値のプレースホルダーを設定する。
     * 
     * @param placeholderReturnValue 戻り値のプレースホルダー
     */
    public void setPlaceholderReturnValue(String placeholderReturnValue) {
        if (!PATTERN_PLACEHOLDER.matcher(placeholderReturnValue).matches()) {
            throw new IllegalArgumentException("Parameter don't match the pattern of the placeholder.");
        }
        this.placeholderReturnValue = placeholderReturnValue;
    }

    /**
     * メソッド仮引数型のプレースホルダーを設定する。
     * 
     * @param placeholderArgumentTypes メソッド仮引数型のプレースホルダー
     */
    public void setPlaceholderArgumentTypes(String placeholderArgumentTypes) {
        if (!PATTERN_PLACEHOLDER.matcher(placeholderArgumentTypes).matches()) {
            throw new IllegalArgumentException("Parameter don't match the pattern of the placeholder.");
        }
        this.placeholderArgumentTypes = placeholderArgumentTypes;
    }

    /**
     * メソッド引数インデックスのプレースホルダーを設定する。
     * 
     * @param placeholderArgumentIndex メソッド引数インデックスのプレースホルダー
     */
    public void setPlaceholderArgumentIndex(String placeholderArgumentIndex) {
        if (!PATTERN_PLACEHOLDER.matcher(placeholderArgumentIndex).matches()) {
            throw new IllegalArgumentException("Parameter don't match the pattern of the placeholder.");
        }
        this.placeholderArgumentIndex = placeholderArgumentIndex;
    }

    /**
     * メソッド実引数値のプレースホルダーを設定する。
     * 
     * @param placeholderArgumentValue メソッド実引数値のプレースホルダー
     */
    public void setPlaceholderArgumentValue(String placeholderArgumentValue) {
        if (!PATTERN_PLACEHOLDER.matcher(placeholderArgumentValue).matches()) {
            throw new IllegalArgumentException("Parameter don't match the pattern of the placeholder.");
        }
        this.placeholderArgumentValue = placeholderArgumentValue;
    }

    /**
     * 例外メッセージのプレースホルダーを設定する。
     * 
     * @param placeholderException 例外メッセージのプレースホルダー
     */
    public void setPlaceholderException(String placeholderException) {
        if (!PATTERN_PLACEHOLDER.matcher(placeholderException).matches()) {
            throw new IllegalArgumentException("Parameter don't match the pattern of the placeholder.");
        }
        this.placeholderException = placeholderException;
    }

    /**
     * 実行時間(ミリ秒)のプレースホルダーを設定する。
     * 
     * @param placeholderInvocationTime 実行時間(ミリ秒)のプレースホルダー
     */
    public void setPlaceholderInvocationTime(String placeholderInvocationTime) {
        if (!PATTERN_PLACEHOLDER.matcher(placeholderInvocationTime).matches()) {
            throw new IllegalArgumentException("Parameter don't match the pattern of the placeholder.");
        }
        this.placeholderInvocationTime = placeholderInvocationTime;
    }

    /**
     * 開始ログのメッセージフォーマットを設定する。
     * 
     * @param enterMessage 開始ログのメッセージフォーマット
     */
    public void setEnterMessage(String enterMessage) {
        this.enterMessage = enterMessage;
    }

    /**
     * 終了ログのメッセージフォーマットを設定する。
     * 
     * @param exitMessage 終了ログのメッセージフォーマット
     */
    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }

    /**
     * 例外ログのメッセージフォーマットを設定する。
     * 
     * @param exceptionMessage 例外ログのメッセージフォーマット
     */
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * メソッド引数ログのメッセージフォーマットを設定する。
     * 
     * @param argumentsMessage メソッド引数ログのメッセージフォーマット
     */
    public void setArgumentsMessage(String argumentsMessage) {
        this.argumentsMessage = argumentsMessage;
    }

    /**
     * 戻り値ログのメッセージフォーマットを設定する。
     * 
     * @param resultMessage 戻り値ログのメッセージフォーマット
     */
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        Map<String, Supplier<?>> base = createReplacementMap(invocation);
        HashMap<String, Supplier<?>> addition = new HashMap<>();

        try {
            // 開始ログを出力する
            if (isEnterLogEnabled(logger)) {
                writeToEnterLog(logger, replacePlaceholders(enterMessage, base));
            }

            // 入力パラメータログを出力する
            if (isArgumentsLogEnabled(logger)) {
                Object[] arguments = invocation.getArguments();
                addition.clear();
                IntStream.range(0, arguments.length).forEach(i -> {
                    addition.put(placeholderArgumentIndex, () -> Integer.valueOf(i));
                    streamLoggingObjects(arguments[i]).forEach(data -> {
                        addition.put(placeholderArgumentValue, () -> data);
                        writeToArgumentsLog(logger, replacePlaceholders(argumentsMessage, base, addition));
                    });
                });
            }

            // 実行時間の計測を開始する
            stopWatch.start();

            // 処理を実行する
            Object result = invocation.proceed();

            // 実行時間の計測を停止する
            stopWatch.stop();

            // 出力パラメータログを出力する
            if (isResultLogEnabled(logger)) {
                // 戻り値が void 以外の場合にログ出力する
                if (!invocation.getMethod().getReturnType().equals(Void.TYPE)) {
                    addition.clear();
                    streamLoggingObjects(result).forEach(data -> {
                        addition.put(placeholderReturnValue, () -> data);
                        writeToResultLog(logger, replacePlaceholders(resultMessage, base, addition));
                    });
                }
            }

            return result;

        } catch (Throwable t) {
            // 実行時間の計測を終了する。
            if (stopWatch.isStarted()) {
                stopWatch.stop();
            }

            // エラーログを出力する
            if (isExceptionLogEnabled(logger, t)) {
                addition.clear();
                addition.put(placeholderInvocationTime, () -> Long.valueOf(stopWatch.getTime()));
                addition.put(placeholderException, () -> t);
                writeToExceptionLog(logger, replacePlaceholders(exceptionMessage, base, addition), t);
            }

            // 例外を再スローする
            throw t;

        } finally {
            // 終了ログを出力する
            if (isExitLogEnabled(logger)) {
                addition.clear();
                addition.put(placeholderInvocationTime, () -> Long.valueOf(stopWatch.getTime()));
                writeToExitLog(logger, replacePlaceholders(exitMessage, base, addition));
            }
        }
    }

    /**
     * 開始ログを出力する。
     * 
     * @param logger ログ出力インスタンス
     * @param message ログ出力メッセージ
     */
    protected void writeToEnterLog(Log logger, Object message) {
        logger.info(message);
    }

    /**
     * 終了ログを出力する。
     * 
     * @param logger ログ出力インスタンス
     * @param message ログ出力メッセージ
     */
    protected void writeToExitLog(Log logger, Object message) {
        logger.info(message);
    }

    /**
     * 例外ログを出力する。
     * 
     * @param logger ログ出力インスタンス
     * @param message ログ出力メッセージ
     * @param t 例外インスタンス
     */
    protected void writeToExceptionLog(Log logger, Object message, Throwable t) {
        if (t == null) {
            logger.error(message);
        } else {
            logger.error(message, t);
        }
    }

    /**
     * メソッド引数ログを出力する。
     * 
     * @param logger ログ出力インスタンス
     * @param message ログ出力メッセージ
     */
    protected void writeToArgumentsLog(Log logger, Object message) {
        logger.debug(message);
    }

    /**
     * 戻り値ログを出力する。
     * 
     * @param logger ログ出力インスタンス
     * @param message ログ出力メッセージ
     */
    protected void writeToResultLog(Log logger, Object message) {
        logger.debug(message);
    }

    /**
     * 開始ログの出力が有効かどうかを判定する。
     * 
     * @param logger ログ出力インスタンス
     * @return ログ出力を有効にする場合はtrueを返す。
     */
    protected boolean isEnterLogEnabled(Log logger) {
        return logger.isInfoEnabled();
    }

    /**
     * 終了ログの出力が有効かどうかを判定する。
     * 
     * @param logger ログ出力インスタンス
     * @return ログ出力を有効にする場合はtrueを返す。
     */
    protected boolean isExitLogEnabled(Log logger) {
        return logger.isInfoEnabled();
    }

    /**
     * 例外ログの出力が有効かどうかを判定する。
     * 
     * @param logger ログ出力インスタンス
     * @param t 例外インスタンス
     * @return ログ出力を有効にする場合はtrueを返す。
     */
    protected boolean isExceptionLogEnabled(Log logger, Throwable t) {
        return logger.isErrorEnabled();
    }

    /**
     * メソッド引数ログの出力が有効かどうかを判定する。
     * 
     * @param logger ログ出力インスタンス
     * @return ログ出力を有効にする場合はtrueを返す。
     */
    protected boolean isArgumentsLogEnabled(Log logger) {
        return logger.isDebugEnabled();
    }

    /**
     * 戻り値ログの出力が有効かどうかを判定する。
     * 
     * @param logger ログ出力インスタンス
     * @return ログ出力を有効にする場合はtrueを返す。
     */
    protected boolean isResultLogEnabled(Log logger) {
        return logger.isDebugEnabled();
    }

    /**
     * オブジェクトのインスタンスからログ出力用オブジェクト列のストリームを生成する。
     * 
     * @param obj ログ出力対象オブジェクト
     * @return ログ出力用オブジェクトのストリーム
     */
    protected Stream<?> streamLoggingObjects(Object obj) {
        return Stream.of(obj);
    }

    /**
     * {@link org.aopalliance.intercept.MethodInvocation MethodInvocation}
     * インスタンスからプレースホルダー置換文字列マップを生成する。
     * 
     * @param invocation メソッド実行インスタンス
     * @return 置換文字列マップ
     */
    private Map<String, Supplier<?>> createReplacementMap(MethodInvocation invocation) {

        HashMap<String, Supplier<?>> replacement = new HashMap<>();

        replacement.put(placeholderTargetClassName, () -> getClassForLogging(invocation.getThis()).getName());
        replacement.put(placeholderTargetClassShortName, () -> getClassForLogging(invocation.getThis()).getSimpleName());
        replacement.put(placeholderMethodName, () -> invocation.getMethod().getName());
        replacement.put(placeholderArgumentTypes, () -> Arrays.stream(invocation.getMethod().getParameterTypes())
                .map(Class::getSimpleName).collect(Collectors.joining(", ")));

        return replacement;
    }

    /**
     * プレースホルダーを置換したメッセージを取得する。
     * 
     * @param message プレースホルダーを含む文字列
     * @param replacement 置換文字列マップ。nullの場合は置換を行わない。
     * @return プレースホルダーを置換した結果の文字列
     */
    private static String replacePlaceholders(String message, Map<String, Supplier<?>> replacement) {
        return replacePlaceholders(message, replacement, null);
    }

    /**
     * プレースホルダーを置換したメッセージを取得する。
     * 
     * @param message プレースホルダーを含む文字列
     * @param base 置換文字列マップ。nullの場合は置換を行わない。
     * @param addition 追加の置換文字列マップ。nullの場合は置換を行わない。
     * @return プレースホルダーを置換した結果の文字列
     */
    private static String replacePlaceholders(String message, Map<String, Supplier<?>> base,
            Map<String, Supplier<?>> addition) {

        if (base == null && addition == null) {
            return message;
        }

        StringBuffer output = new StringBuffer();
        Matcher matcher = PATTERN_PLACEHOLDER.matcher(message);

        while (matcher.find()) {
            String match = matcher.group();
            if (base != null && base.containsKey(match)) {
                matcher.appendReplacement(output, Matcher.quoteReplacement(String.valueOf(base.get(match).get())));
            } else if (addition != null && addition.containsKey(match)) {
                matcher.appendReplacement(output, Matcher.quoteReplacement(String.valueOf(addition.get(match).get())));
            } else {
                matcher.appendReplacement(output, Matcher.quoteReplacement(match));
            }
        }
        matcher.appendTail(output);

        return output.toString();
    }
}
