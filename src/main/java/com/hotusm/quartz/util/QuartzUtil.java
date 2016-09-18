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
 * @date 2016��9��18��   <br/>
 * @description ������һЩ���� ������ ɾ��  �޸� ���� 
 * ��trigger �����ڶ������ʱ���Ԫ�أ�������ʲôʱ�����ȥִ������Quartz ����Ҫ�ṩ���������͵�
 * trigger��SimpleTrigger��CronTirgger��DateIntervalTrigger���� NthIncludedDayTrigger��
 * ���� Quartz �У� scheduler �� scheduler ����������DirectSchedulerFactory ���� StdSchedulerFactory�� �ڶ��ֹ��� StdSchedulerFactory ʹ�ý϶࣬
 * ��Ϊ DirectSchedulerFactory ʹ�������������㣬��Ҫ�������ϸ���ֹ��������á�
 * ��Scheduler ��Ҫ�����֣�RemoteMBeanScheduler�� RemoteScheduler �� StdScheduler��
 * ��������õ� StdScheduler Ϊ������
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
			LOGGER.error("quartz����ʧ��",e);
		} 
	}
	
	public void addJob(String name, String group, Class<? extends Job> clazz,
			String cronExpression, Map<String, Object> maps) {
		try {
			// ��������
			JobDetail job = newJob(clazz).withIdentity(name, group).build();

			if (null != maps && !maps.isEmpty()) {
				for (String key : maps.keySet()) {
					job.getJobDataMap().put(key, maps.get(key));
				}
			}

			// �������񴥷���
			Trigger trg = newTrigger().withIdentity(name, group)
					.withSchedule(cronSchedule(cronExpression)).build();

			// ����ҵ��ӵ�������
			scheduler.scheduleJob(job, trg);
			LOGGER.info("������ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("������ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "]=> [ʧ��]");
		}
	}
	
	/**
	 * @param name
	 * @param group
	 * @param clazz
	 * @param startTime
	 * @param RepeatSeconds ѭ��ʱ�������룩
	 * @param RepeatCount  �ܹ���ִ�д���
	 * @param maps
	 */
	public void addJob2(String name, String group, Class<? extends Job> clazz,
			Date startTime, int RepeatSeconds, int RepeatCount,
			Map<String, Object> maps) {
		try {

			// ��������
			JobDetail job = newJob(clazz).withIdentity(name, group).build();

			if (null != maps && !maps.isEmpty()) {
				for (String key : maps.keySet()) {
					job.getJobDataMap().put(key, maps.get(key));
				}
			}
			SimpleTrigger trg = null;

			// ִֻ��һ��
			if (new Integer(RepeatSeconds) == 0
					&& new Integer(RepeatCount) == 0) {
				trg = (SimpleTrigger) newTrigger().withIdentity(name, group)
						.startAt(startTime).build();

			} else {
				// ����ִ��(ÿ����ִ��һ��)
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
			// ����ҵ��ӵ�������
			scheduler.scheduleJob(job, trg);
		/*	LOGGER.info("������ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "����ʱ�䣺] "
					+ DateUtils.formatDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		*/} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("������ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "]=> [ʧ��]");
		}
	}

	public void removeJob(String name, String group) {
		try {
			TriggerKey tk = TriggerKey.triggerKey(name, group);
			scheduler.pauseTrigger(tk);// ֹͣ������
			scheduler.unscheduleJob(tk);// �Ƴ�������
			JobKey jobKey = JobKey.jobKey(name, group);
			scheduler.deleteJob(jobKey);// ɾ����ҵ
			LOGGER.info("ɾ����ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("ɾ����ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "]=> [ʧ��]");
		}
	}

	public void pauseJob(String name, String group) {
		try {
			JobKey jobKey = JobKey.jobKey(name, group);
			scheduler.pauseJob(jobKey);
			LOGGER.info("��ͣ��ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("��ͣ��ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "]=> [ʧ��]");
		}
	}

	public boolean resumeJob(String name, String group) {
		try {
			JobKey jobKey = JobKey.jobKey(name, group);
			if (scheduler.checkExists(jobKey)) {
				scheduler.resumeJob(jobKey);
				LOGGER.info("�ָ���ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "] ");
			} else {
				return false;
			}

		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("�ָ���ҵ=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "]=> [ʧ��]");
		}
		return true;
	}

	public void modifyTime(String name, String group, String cronExpression) {
		try {
			TriggerKey tk = TriggerKey.triggerKey(name, group);
			// �������񴥷���
			Trigger trg = newTrigger().withIdentity(name, group)
					.withSchedule(cronSchedule(cronExpression)).build();
			scheduler.rescheduleJob(tk, trg);
			LOGGER.info("�޸���ҵ����ʱ��=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("�޸���ҵ����ʱ��=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "]=> [ʧ��]");
		}
	}

	public void modifyTime2(String name, String group, int RepeatSeconds,
			int RepeatCount, Date startTime) {
		try {
			TriggerKey tk = TriggerKey.triggerKey(name, group);
			Trigger trg;

			// �������񴥷���
			if (new Integer(RepeatSeconds) == 0
					&& new Integer(RepeatCount) == 0) {
				trg = (SimpleTrigger) newTrigger().withIdentity(name, group)
						.startAt(startTime).build();
/*				LOGGER.info("�޸���ҵ����ʱ��=> [��ҵ���ƣ�"
						+ name
						+ " ��ҵ�飺"
						+ group
						+ " ���´���ʱ�䣺 "
						+ DateUtils
								.formatDate(startTime, "yyyy-MM-dd HH:mm:ss")
						+ " ]");*/
				// ����ִ��(ÿ��ִ��һ��)
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
			// LOGGER.info("�޸���ҵ����ʱ��=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "] ");
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("�޸���ҵ����ʱ��=> [��ҵ���ƣ�" + name + " ��ҵ�飺" + group + "]=> [ʧ��]");
		}
	}
	/**
	 * @param ifWait �Ƿ����Ϲر�
	 */
	public void shutDown(boolean ifNotWait){
		try {
			scheduler.shutdown(ifNotWait);
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error("�ر�Quartz��������",e);
		}
	}
	
}
