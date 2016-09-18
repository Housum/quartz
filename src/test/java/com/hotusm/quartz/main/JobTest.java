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
 * @date 2016��9��18��   <br/>
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
      
      //һ����ʽ��
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
      //����Quartz�ȴ���ǰ���������ʱ �ٽ��йر�
      scheduler.shutdown(true);
	}
	

	/**
	 * @description ʹ��CronTirgger�ķ�ʽ������<br/> 
	 * @throws SchedulerException
	 */
	@Test
	public void jobTest1() throws SchedulerException{
		  Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
	      scheduler.start();
	      
	      JobDataMap dataMap=new JobDataMap();
	      dataMap.put("value", "TEST");
	      
	      JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity("job1", "group1").setJobData(dataMap).build();
	      
	      //���ʽ��
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
	
	
	//Ĭ�ϵ������  ��master/salve�ķ�ʽִ�е�  ֻ�����е�һ��
	//master������  �����һ��JOB��������
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
