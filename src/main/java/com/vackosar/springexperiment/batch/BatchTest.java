package com.vackosar.springexperiment.batch;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BatchTest {

    private static final String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><book><record id=\"Library-Klementinum--a\"></record><record id=\"Library-Klementinum--b\"></record><record id=\"Library-Klementinum--c\"></record><record id=\"Library-Klementinum--d\"></record><record id=\"Library-Klementinum--e\"></record></book>";

    @Test
    public void test() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring/batch/jobs/job.xml"});
        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("helloWorldJob");
        JobExecution execution = jobLauncher.run(job, new JobParameters());
        Assert.assertEquals(BatchStatus.COMPLETED, execution.getStatus());
        final List<String> lines = Files.readAllLines(Paths.get("outputs/book.xml"));
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals(EXPECTED_XML, lines.get(0));
    }
}
