package com.hotusm.quartz.main;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.hotusm.quartz.job.HelloJob;
/**
 * 
 * @author Hotusm  <br/>
 * @date 2016年9月18日   <br/>
 * @description
 * @link https://www.ibm.com/developerworks/cn/opensource/os-cn-quartz/
 */
public class JobTest {

	@Test
	public void jobTest() throws SchedulerException{
	  Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.start();
      
      JobDataMap dataMap=new JobDataMap();
      dataMap.put("value", "TEST");
      
      JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity("job1", "group1").setJobData(dataMap).build();
      
      //一般形式的
      SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
              .withIntervalInSeconds(5).repeatForever();
      
      Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
              .startNow().withSchedule(simpleScheduleBuilder).build();
      
      scheduler.scheduleJob(job, trigger);
      
      try {
    	  TimeUnit.MINUTES.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
      //告诉Quartz等待当前的任务结束时 再进行关闭
      scheduler.shutdown(true);
	}
	

	/**
	 * @description 使用CronTirgger的方式来启动<br/> 
	 * @throws SchedulerException
	 */
	@Test
	public void jobTest1() throws SchedulerException{
		  Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
	      scheduler.start();
	      
	      JobDataMap dataMap=new JobDataMap();
	      dataMap.put("value", "TEST");
	      
	      JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity("job1", "group1").setJobData(dataMap).build();
	      
	      //表达式的
	      /*  CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0/5 * * * * ? *"); 
	      Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
	              .startNow().withSchedule(cronScheduleBuilder).build();
	      */
	      
	      
	      Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
	              .startNow().withSchedule(cronSchedule("0/5 * * * * ? *")).build();
	      
	      scheduler.scheduleJob(job,trigger);
	      
	      try {
	    	  TimeUnit.MINUTES.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	      
	      scheduler.shutdown();
	}
	
	
	//默认的情况下  是master/salve的方式执行的  只有其中的一个
	//master当掉了  另外的一个JOB才能运行
	@Test
	public void jobCluster1() throws SchedulerException{
		  Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
	      scheduler.start();
	      
	      List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();
	      
	      System.out.println("Job number:"+jobs.size());
	      try {
	    	  TimeUnit.MINUTES.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	@Test
	public void jobCluster2() throws SchedulerException{
		  Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
	      scheduler.start();
	      
	      try {
	    	  TimeUnit.MINUTES.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
