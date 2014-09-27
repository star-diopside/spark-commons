package jp.gr.java_conf.star_diopside.spark.commons.batch.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;

/**
 * 一時ファイルを生成する{@link JobExecutionListener}
 */
public class TemporaryFileJobListener implements JobExecutionListener, InitializingBean {

    /** 生成した一時ファイル名をExecutionContextに格納するキー */
    @Setter
    private String key;

    /** 一時ファイル名の接頭辞文字列 */
    @Setter
    private String prefix;

    /** 一時ファイル名の接尾辞文字列 */
    @Setter
    private String suffix;

    /** 一時ファイルを生成するディレクトリ */
    @Setter
    private String directory;

    /** 終了時に一時ファイルを削除するかを示すフラグ */
    @Setter
    private boolean deleteOnExit = true;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (key == null) {
            throw new BeanInitializationException("Property 'key' is required.");
        }
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        Path tempFile;
        try {
            Path tempDir = Paths.get(directory == null ? FileUtils.getTempDirectoryPath() : directory);
            tempFile = Files.createTempFile(tempDir, prefix, suffix);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        jobExecution.getExecutionContext().putString(key, tempFile.toString());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (!deleteOnExit) {
            return;
        }
        Path tempFile = Paths.get(jobExecution.getExecutionContext().getString(key));
        try {
            Files.delete(tempFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
