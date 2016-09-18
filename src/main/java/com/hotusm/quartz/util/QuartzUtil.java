package com.hotusm.quartz.util;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hotusm  <br/>
 * @date 2016年9月18日   <br/>
 * @description 基本的一些操作 像增加 删除  修改 任务 
 * ①trigger 是用于定义调度时间的元素，即按照什么时间规则去执行任务。Quartz 中主要提供了四种类型的
 * trigger：SimpleTrigger，CronTirgger，DateIntervalTrigger，和 NthIncludedDayTrigger。
 * ②在 Quartz 中， scheduler 由 scheduler 工厂创建：DirectSchedulerFactory 或者 StdSchedulerFactory。 第二种工厂 StdSchedulerFactory 使用较多，
 * 因为 DirectSchedulerFactory 使用起来不够方便，需要作许多详细的手工编码设置。
 * ③Scheduler 主要有三种：RemoteMBeanScheduler， RemoteScheduler 和 StdScheduler。
 * 本文以最常用的 StdScheduler 为例讲解
 * 
 */
public class QuartzUtil {
	
	protected static final Logger LOGGER=LoggerFactory.getLogger(QuartzUtil.class);
	
	private SchedulerFactory schedulerFactory=new StdSchedulerFactory();
	private Scheduler scheduler=null;
	
	public QuartzUtil(){
		start();
	}
	
	
	public void start(){
		try {
			scheduler=schedulerFactory.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("quartz启动失败",e);
		} 
	}
	
	public void addJob(String name, String group, Class<? extends Job> clazz,
			String cronExpression, Map<String, Object> maps) {
		try {
			// 构造任务
			JobDetail job = newJob(clazz).withIdentity(name, group).build();

			if (null != maps && !maps.isEmpty()) {
				for (String key : maps.keySet()) {
					job.getJobDataMap().put(key, maps.get(key));
				}
			}

			// 构造任务触发器
			Trigger trg = newTrigger().withIdentity(name, group)
					.withSchedule(cronSchedule(cronExpression)).build();

			// 将作业添加到调度器
			scheduler.scheduleJob(job, trg);
			LOGGER.info("创建作业=> [作业名称：" + name + " 作业组：" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("创建作业=> [作业名称：" + name + " 作业组：" + group + "]=> [失败]");
		}
	}
	
	/**
	 * @param name
	 * @param group
	 * @param clazz
	 * @param startTime
	 * @param RepeatSeconds 循环时间间隔（秒）
	 * @param RepeatCount  总共的执行次数
	 * @param maps
	 */
	public void addJob2(String name, String group, Class<? extends Job> clazz,
			Date startTime, int RepeatSeconds, int RepeatCount,
			Map<String, Object> maps) {
		try {

			// 构造任务
			JobDetail job = newJob(clazz).withIdentity(name, group).build();

			if (null != maps && !maps.isEmpty()) {
				for (String key : maps.keySet()) {
					job.getJobDataMap().put(key, maps.get(key));
				}
			}
			SimpleTrigger trg = null;

			// 只执行一次
			if (new Integer(RepeatSeconds) == 0
					&& new Integer(RepeatCount) == 0) {
				trg = (SimpleTrigger) newTrigger().withIdentity(name, group)
						.startAt(startTime).build();

			} else {
				// 永久执行(每分钟执行一次)
				/*trg = (SimpleTrigger) newTrigger()
						.withIdentity(name, group)
						.startAt(startTime)
						.withSchedule(
								simpleSchedule()
										.repeatForever()
										.withIntervalInSeconds(RepeatSeconds)
										.withMisfireHandlingInstructionNextWithRemainingCount())
						.build();*/
				trg = (SimpleTrigger) newTrigger().withIdentity(name, group)
						.withSchedule(simpleSchedule().withIntervalInSeconds(RepeatSeconds).withRepeatCount(RepeatCount-1)).startAt(startTime).build();
			}
			// 将作业添加到调度器
			scheduler.scheduleJob(job, trg);
		/*	LOGGER.info("创建作业=> [作业名称：" + name + " 作业组：" + group + "触发时间：] "
					+ DateUtils.formatDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		*/} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("创建作业=> [作业名称：" + name + " 作业组：" + group + "]=> [失败]");
		}
	}

	public void removeJob(String name, String group) {
		try {
			TriggerKey tk = TriggerKey.triggerKey(name, group);
			scheduler.pauseTrigger(tk);// 停止触发器
			scheduler.unscheduleJob(tk);// 移除触发器
			JobKey jobKey = JobKey.jobKey(name, group);
			scheduler.deleteJob(jobKey);// 删除作业
			LOGGER.info("删除作业=> [作业名称：" + name + " 作业组：" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("删除作业=> [作业名称：" + name + " 作业组：" + group + "]=> [失败]");
		}
	}

	public void pauseJob(String name, String group) {
		try {
			JobKey jobKey = JobKey.jobKey(name, group);
			scheduler.pauseJob(jobKey);
			LOGGER.info("暂停作业=> [作业名称：" + name + " 作业组：" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("暂停作业=> [作业名称：" + name + " 作业组：" + group + "]=> [失败]");
		}
	}

	public boolean resumeJob(String name, String group) {
		try {
			JobKey jobKey = JobKey.jobKey(name, group);
			if (scheduler.checkExists(jobKey)) {
				scheduler.resumeJob(jobKey);
				LOGGER.info("恢复作业=> [作业名称：" + name + " 作业组：" + group + "] ");
			} else {
				return false;
			}

		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("恢复作业=> [作业名称：" + name + " 作业组：" + group + "]=> [失败]");
		}
		return true;
	}

	public void modifyTime(String name, String group, String cronExpression) {
		try {
			TriggerKey tk = TriggerKey.triggerKey(name, group);
			// 构造任务触发器
			Trigger trg = newTrigger().withIdentity(name, group)
					.withSchedule(cronSchedule(cronExpression)).build();
			scheduler.rescheduleJob(tk, trg);
			LOGGER.info("修改作业触发时间=> [作业名称：" + name + " 作业组：" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("修改作业触发时间=> [作业名称：" + name + " 作业组：" + group + "]=> [失败]");
		}
	}

	public void modifyTime2(String name, String group, int RepeatSeconds,
			int RepeatCount, Date startTime) {
		try {
			TriggerKey tk = TriggerKey.triggerKey(name, group);
			Trigger trg;

			// 构造任务触发器
			if (new Integer(RepeatSeconds) == 0
					&& new Integer(RepeatCount) == 0) {
				trg = (SimpleTrigger) newTrigger().withIdentity(name, group)
						.startAt(startTime).build();
/*				LOGGER.info("修改作业触发时间=> [作业名称："
						+ name
						+ " 作业组："
						+ group
						+ " 重新触发时间： "
						+ DateUtils
								.formatDate(startTime, "yyyy-MM-dd HH:mm:ss")
						+ " ]");*/
				// 永久执行(每天执行一次)
			} else {
				trg = (SimpleTrigger) newTrigger()
						.withIdentity(name, group)
						.startAt(startTime)
						.withSchedule(
								simpleSchedule()
										.withIntervalInSeconds(RepeatSeconds)
										.repeatForever()
										.withMisfireHandlingInstructionNextWithRemainingCount())
						.build();
			}
			scheduler.rescheduleJob(tk, trg);
			// LOGGER.info("修改作业触发时间=> [作业名称：" + name + " 作业组：" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("修改作业触发时间=> [作业名称：" + name + " 作业组：" + group + "]=> [失败]");
		}
	}
	/**
	 * @param ifWait 是否马上关闭
	 */
	public void shutDown(boolean ifNotWait){
		try {
			scheduler.shutdown(ifNotWait);
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("关闭Quartz出现问题",e);
		}
	}
	
}
