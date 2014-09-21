package jp.gr.java_conf.star_diopside.spark.commons.test.support;

import java.util.Map;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

/**
 * データベースを使用するテストをサポートする機能を提供するインタフェース
 */
public interface DatabaseTestSupport {

    /**
     * テストデータセットを取得する。
     * 
     * @return テストデータセット
     */
    IDataSet getDataSet();

    /**
     * オブジェクト置換マップを設定する。
     * 
     * @param replacementObjectMap オブジェクト置換マップ
     */
    void setReplacementObjectMap(Map<?, ?> replacementObjectMap);

    /**
     * 文字列置換マップを設定する。
     * 
     * @param replacementSubstringMap 文字列置換マップ
     */
    void setReplacementSubstringMap(Map<String, String> replacementSubstringMap);

    /**
     * フラットXMLデータセットを設定する。
     * 
     * @param testFile テストデータファイル名
     */
    void setFlatXmlDataSet(String testFile);

    /**
     * CSVデータセットを設定する。
     * 
     * @param testDirectory テストデータディレクトリ名
     */
    void setCsvDataSet(String testDirectory);

    /**
     * テストデータのセットアップを行う。
     */
    void onSetup();

    /**
     * データベースをテスト前の状態に戻す。
     */
    void onTearDown();

    /**
     * データベースコネクションを取得する。
     * 
     * @return データベースコネクション
     */
    IDatabaseConnection getConnection();

}
