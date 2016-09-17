package jp.gr.java_conf.star_diopside.spark.commons.batch.support;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;

/**
 * 繰り返しジョブパラメータリストアクセスインタフェース
 */
public interface IterateJobParametersListAccessor {

    /**
     * 後続の繰り返しジョブに渡すジョブパラメータリストを取得する。
     * 
     * @param jobExecution ジョブ実行オブジェクト
     * @return 後続の繰り返しジョブに渡すジョブパラメータリスト
     */
    List<JobParameters> getJobParametersList(JobExecution jobExecution);

}
