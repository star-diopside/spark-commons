package jp.gr.java_conf.star_diopside.spark.commons.test.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import jp.gr.java_conf.star_diopside.spark.commons.test.dataset.csv.CsvProducerEx;
import jp.gr.java_conf.star_diopside.spark.commons.test.exception.TestException;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

/**
 * データセットユーティリティ
 */
public final class DataSetUtils {

    private DataSetUtils() {
    }

    /**
     * フラットXMLデータセットを生成する。
     * 
     * @param testFile テストデータファイル名
     * @return フラットXMLデータセット
     */
    public static IDataSet createFlatXmlDataSet(Path testFile) {
        try {
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
            return builder.build(Files.newInputStream(testFile));
        } catch (DataSetException | IOException e) {
            throw new TestException(e);
        }
    }

    /**
     * CSVデータセットを生成する。
     * 
     * @param testDirectory テストデータディレクトリ名
     * @return CSVデータセット
     */
    public static IDataSet createCsvDataSet(Path testDirectory) {
        return createCsvDataSet(testDirectory, StandardCharsets.UTF_8);
    }

    /**
     * CSVデータセットを生成する。
     * 
     * @param testDirectory テストデータディレクトリ名
     * @param charset CSVファイルの文字セット
     * @return CSVデータセット
     */
    public static IDataSet createCsvDataSet(Path testDirectory, Charset charset) {
        try {
            return new CachedDataSet(new CsvProducerEx(testDirectory, charset));
        } catch (DataSetException e) {
            throw new TestException(e);
        }
    }
}
