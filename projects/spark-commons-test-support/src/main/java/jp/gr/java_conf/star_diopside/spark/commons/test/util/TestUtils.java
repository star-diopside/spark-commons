package jp.gr.java_conf.star_diopside.spark.commons.test.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import jp.gr.java_conf.star_diopside.spark.commons.test.exception.TestException;

/**
 * テスト用ユーティリティクラス
 */
public final class TestUtils {

    private TestUtils() {
    }

    /**
     * テストデータディレクトリを取得する。
     * 
     * @param tester テストクラスのインスタンス
     * @return テストデータを格納するディレクトリパス
     */
    public static Path findTestDataDir(Object tester) {
        return findTestDataDir(tester.getClass());
    }

    /**
     * テストデータディレクトリを取得する。
     * 
     * @param testerClass テストクラス
     * @return テストデータを格納するディレクトリパス
     */
    public static Path findTestDataDir(Class<?> testerClass) {
        try {
            return Paths.get(testerClass.getResource(testerClass.getSimpleName() + "-data").toURI());
        } catch (URISyntaxException e) {
            throw new TestException(e);
        }
    }

    /**
     * テストデータファイルを取得する。
     * 
     * @param tester テストクラスのインスタンス
     * @param path テストファイルパス (テストデータディレクトリからの相対パス)
     * @return テストデータファイルパス
     */
    public static Path findTestDataFile(Object tester, String path) {
        return findTestDataFile(tester.getClass(), path);
    }

    /**
     * テストデータファイルを取得する。
     * 
     * @param testerClass テストクラス
     * @param path テストファイルパス (テストデータディレクトリからの相対パス)
     * @return テストデータファイルパス
     */
    public static Path findTestDataFile(Class<?> testerClass, String path) {
        return findTestDataDir(testerClass).resolve(path);
    }
}
