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
 * ��ִ�еĹ�����  ��Ҫ���µĸ���JobDataMap ��ִ����֮��  ���µĴ���
 * JobDataMap 
 */
@PersistJobDataAfterExecution
/*
 * 1.��ֹ���־�̬����(������ִ�����û5�봥��һ�� ��������ȴ��Ҫִ��10��  ���ʱ��ͻ���־�̬����
 * ����ͻ��ֹ�������ĳ���) 
 * 2.��ǰ�İ汾��ʵ��StatefulJob  ����һ����״̬��Job 
 * 3.����������ע��Ļ�  ��ô������ר״̬��Job ��ô�Ϳ���ͬ����ִ��
 */
@DisallowConcurrentExecution
/**
 * @author Hotusm     <br/>
 * @date 2016��9��18��     <br/>
 * @description  
 * Job:����Ԫ 
 * ��ʵ����Job�ӿڵ������ʹ��execute(JobExecutionContext context)����.��ʾ�ľ���
 * ִ�е��߼�  Ĭ�ϵ�ʵ���� @see InterruptableJob ��ʾ���ǿ��жϵ����� 
 * ����״̬��Job����˼��˵�����������й�ϵ�� ���ܹ�������ִ��(ֻ����һ�ε�ִ�����Ժ�  ��һ�β���
 * ִ��)
 * ��һ��Job���Թ������Tigger ����һ��Tiggerֻ�ܹ���һ��Job
 * �ݻ����ļ�������
 * scheduler��
 *	���������
	trigger��
	�����������ڶ����������ʱ�����
	job��
	���񣬼������ȵ�����
	misfire��
	����ģ�ָ����Ӧ�ñ�ִ�е�ʵ��û�б�ִ�е��������
 * <br/>
 * @link http://www.quartz-scheduler.org/api/2.2.1/index.html
 */
public class HelloJob implements Job{
	
	protected Logger logger=LoggerFactory.getLogger(HelloJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long startTime=System.currentTimeMillis();
		logger.info("execute HelloJob");
		//JobDataMap��Ϊ���ݵĽ���
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
