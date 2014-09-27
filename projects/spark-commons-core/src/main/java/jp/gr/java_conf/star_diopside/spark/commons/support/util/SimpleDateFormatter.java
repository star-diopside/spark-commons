package jp.gr.java_conf.star_diopside.spark.commons.support.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * スレッドローカルで保持される {@link SimpleDateFormat} に処理を委譲するクラス
 */
public class SimpleDateFormatter {

    /** <code>yyyyMMdd</code> 形式の時刻値を扱う {@link SimpleDateFormatter} */
    public static final SimpleDateFormatter BASIC_ISO_DATE = new SimpleDateFormatter("yyyyMMdd");

    /** <code>yyyy-MM-dd</code> 形式の時刻値を扱う {@link SimpleDateFormatter} */
    public static final SimpleDateFormatter ISO_LOCAL_DATE = new SimpleDateFormatter("yyyy-MM-dd");

    /** スレッドローカル領域 */
    private ThreadLocal<SimpleDateFormat> formatter;

    /**
     * コンストラクタ
     * 
     * @param pattern 日付時刻文字列のフォーマットパターン
     */
    public SimpleDateFormatter(String pattern) {
        formatter = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
    }

    /**
     * {@link SimpleDateFormat#format(Date)} に処理を委譲する。
     * 
     * @param date 時刻文字列にフォーマットする{@link Date}
     * @return フォーマットされた文字列
     * @see SimpleDateFormat#format(Date)
     */
    public String format(Date date) {
        return formatter.get().format(date);
    }

    /**
     * {@link SimpleDateFormat#parse(String)} に処理を委譲する。
     * 
     * @param source 解析する文字列
     * @return 解析結果の{@link Date}
     * @throws ParseException 指定した文字列が解析できない場合
     * @see SimpleDateFormat#parse(String)
     */
    public Date parse(String source) throws ParseException {
        return formatter.get().parse(source);
    }
}
