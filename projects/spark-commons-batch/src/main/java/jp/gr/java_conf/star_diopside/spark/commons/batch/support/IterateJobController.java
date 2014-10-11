package jp.gr.java_conf.star_diopside.spark.commons.batch.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;

/**
 * 繰り返しジョブ実行コントローラークラス
 */
public class IterateJobController implements JobExecutionListener, JobExecutionDecider, JobParametersExtractor,
        IterateJobParametersListAccessor, InitializingBean {

    /** ジョブ実行コンテキストに設定するキーのプレフィックス */
    private String keyPrefix;

    /** 繰り返しジョブパラメータリストを格納するジョブ実行コンテキストキー */
    private String keyJobParametersList;

    /** 繰り返しジョブの現在実行位置を格納するジョブ実行コンテキストキー */
    private String keyCurrent;

    /**
     * ジョブ実行コンテキストに設定するキーのプレフィックスを設定する。
     * 
     * @param keyPrefix ジョブ実行コンテキストに設定するキーのプレフィックス
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String prefix = StringUtils.defaultString(keyPrefix);
        keyJobParametersList = prefix + "JOB_PARAMETERS_LIST";
        keyCurrent = prefix + "CURRENT";
    }

    @Override
    public List<JobParameters> getJobParametersList(JobExecution jobExecution) {
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        @SuppressWarnings("unchecked")
        List<JobParameters> jobParametersList = (List<JobParameters>) jobExecutionContext.get(keyJobParametersList);
        return jobParametersList;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        ArrayList<JobParameters> jobParametersList = new ArrayList<>();
        jobExecutionContext.put(keyJobParametersList, jobParametersList);
        jobExecutionContext.putInt(keyCurrent, 0);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
    }

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        if (hasNext(jobExecution)) {
            return new FlowExecutionStatus("CONTINUE");
        } else {
            for (StepExecution se : jobExecution.getStepExecutions()) {
                if (se.getExitStatus().compareTo(ExitStatus.FAILED) == 0) {
                    return FlowExecutionStatus.FAILED;
                }
            }
            return FlowExecutionStatus.COMPLETED;
        }
    }

    @Override
    public JobParameters getJobParameters(Job job, StepExecution stepExecution) {
        return next(stepExecution.getJobExecution());
    }

    /**
     * 次の繰り返し実行ジョブパラメータが存在する場合、trueを返す。
     * 
     * @param jobExecution ジョブ実行オブジェクト
     * @return 次の繰り返し実行ジョブパラメータが存在する場合はtrue
     */
    private boolean hasNext(JobExecution jobExecution) {
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        @SuppressWarnings("unchecked")
        List<JobParameters> jobParametersList = (List<JobParameters>) jobExecutionContext.get(keyJobParametersList);
        int current = jobExecutionContext.getInt(keyCurrent);
        return (jobParametersList.size() - 1 >= current);
    }

    /**
     * 次の繰り返し実行ジョブパラメータを取得する。
     * 
     * @param jobExecution ジョブ実行オブジェクト
     * @return 次の繰り返し実行ジョブパラメータ
     */
    private JobParameters next(JobExecution jobExecution) {
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        @SuppressWarnings("unchecked")
        List<JobParameters> jobParametersList = (List<JobParameters>) jobExecutionContext.get(keyJobParametersList);
        int current = jobExecutionContext.getInt(keyCurrent);
        JobParameters jobParameters = jobParametersList.get(current);
        jobExecutionContext.putInt(keyCurrent, current + 1);
        return jobParameters;
    }
}
