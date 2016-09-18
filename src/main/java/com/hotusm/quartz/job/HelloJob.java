package com.hotusm.quartz.job;

import java.util.concurrent.TimeUnit;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 * 在执行的过程中  需要重新的更新JobDataMap 在执行完之后  重新的储存
 * JobDataMap 
 */
@PersistJobDataAfterExecution
/*
 * 1.防止出现竞态条件(如果出现触发器没5秒触发一次 但是任务却需要执行10秒  这个时候就会出现竞态条件
 * 这个就会防止这个情况的出现) 
 * 2.以前的版本是实现StatefulJob  这是一个有状态的Job 
 * 3.如果不加这个注解的话  那么就是无专状态的Job 那么就可以同步的执行
 */
@DisallowConcurrentExecution
/**
 * @author Hotusm     <br/>
 * @date 2016年9月18日     <br/>
 * @description  
 * Job:任务单元 
 * ①实现了Job接口的类必须使用execute(JobExecutionContext context)方法.表示的就是
 * 执行的逻辑  默认的实现有 @see InterruptableJob 表示的是可中断的任务 
 * ②有状态的Job的意思是说和上下文是有关系的 不能够并发的执行(只有上一次的执行完以后  下一次才能
 * 执行)
 * ③一个Job可以关联多个Tigger 但是一个Tigger只能关联一个Job
 * ⑤基本的几个概念
 * scheduler：
 *	任务调度器
	trigger：
	触发器，用于定义任务调度时间规则
	job：
	任务，即被调度的任务
	misfire：
	错过的，指本来应该被执行但实际没有被执行的任务调度
 * <br/>
 * @link http://www.quartz-scheduler.org/api/2.2.1/index.html
 */
public class HelloJob implements Job{
	
	protected Logger logger=LoggerFactory.getLogger(HelloJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long startTime=System.currentTimeMillis();
		logger.info("execute HelloJob");
		//JobDataMap作为数据的交互
		logger.info("value:"+context.getJobDetail().getJobDataMap().getString("value"));
		
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long totalTime=System.currentTimeMillis()-startTime;
		System.out.println("TotalTime:"+TimeUnit.SECONDS.convert(totalTime, TimeUnit.MILLISECONDS));
	}

}
