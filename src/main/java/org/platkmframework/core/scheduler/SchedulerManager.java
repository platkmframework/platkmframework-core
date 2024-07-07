/*******************************************************************************
 * Copyright(c) 2023 the original author Eduardo Iglesias Taylor.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	 https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * 	Eduardo Iglesias Taylor - initial API and implementation
 *******************************************************************************/
package org.platkmframework.core.scheduler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.platkmframework.annotation.timer.TimerFixeDelayScheduler;
import org.platkmframework.annotation.timer.TimerFixeRateScheduler;
import org.platkmframework.annotation.timer.TimerScheduler;
import org.platkmframework.content.ObjectContainer;
import org.platkmframework.content.project.ProjectContent;
import org.platkmframework.doi.data.BeanMethodInfo;

public class SchedulerManager {
	
	private static Logger logger = LoggerFactory.getLogger(SchedulerManager.class);
	
	private static final String C_DATE_TIME_NOW = "now";
	
	
	private static SchedulerManager schedulerManager;

	List<Class<? extends Annotation>> scheduleAnnotations;
	Map<String, BeanMethodInfo> map;
	
	private SchedulerManager() {
		map =  new HashMap<>();
		scheduleAnnotations = new ArrayList<>();
		scheduleAnnotations.add(TimerFixeDelayScheduler.class);
		scheduleAnnotations.add(TimerFixeRateScheduler.class);
		scheduleAnnotations.add(TimerScheduler.class);
	}
	
	public static SchedulerManager instance() {
		if(schedulerManager == null) schedulerManager = new SchedulerManager();
		return schedulerManager;
	}
	
	
	public void run(String name) throws SchedulerException {
		BeanMethodInfo beanMethodInfo = map.get(name);
		if(beanMethodInfo == null) throw new SchedulerException("No se encontró el schedule con nombre -> " + name);
		
		
		if(beanMethodInfo.getMethod().isAnnotationPresent(TimerFixeDelayScheduler.class) && 
				isRunOnStart( beanMethodInfo.getMethod().getAnnotation(TimerFixeDelayScheduler.class).runOnStart()))
			runTimerFixedDelayScheduler(beanMethodInfo.getObj(), beanMethodInfo.getMethod());
		
		if(beanMethodInfo.getMethod().isAnnotationPresent(TimerFixeRateScheduler.class) && 
				isRunOnStart( beanMethodInfo.getMethod().getAnnotation(TimerFixeRateScheduler.class).runOnStart()))  
			runTimerFixedRateScheduler(beanMethodInfo.getObj(), beanMethodInfo.getMethod());
		
		if(beanMethodInfo.getMethod().isAnnotationPresent(TimerScheduler.class) && 
				isRunOnStart( beanMethodInfo.getMethod().getAnnotation(TimerScheduler.class).runOnStart())) 
			runTimerScheduler(beanMethodInfo.getObj(), beanMethodInfo.getMethod());
		 
	}
	
	
	public void runAllOnStart() {
		
		List<BeanMethodInfo> list = ObjectContainer.instance().getBeansMethodByAnnotations(scheduleAnnotations);
		for (BeanMethodInfo beanMethodInfo : list) {
			
			if(beanMethodInfo.getMethod().isAnnotationPresent(TimerFixeDelayScheduler.class) && 
					isRunOnStart( beanMethodInfo.getMethod().getAnnotation(TimerFixeDelayScheduler.class).runOnStart()))
				runTimerFixedDelayScheduler(beanMethodInfo.getObj(), beanMethodInfo.getMethod());
			
			if(beanMethodInfo.getMethod().isAnnotationPresent(TimerFixeRateScheduler.class) && 
					isRunOnStart( beanMethodInfo.getMethod().getAnnotation(TimerFixeRateScheduler.class).runOnStart()))  
				runTimerFixedRateScheduler(beanMethodInfo.getObj(), beanMethodInfo.getMethod());
			
			if(beanMethodInfo.getMethod().isAnnotationPresent(TimerScheduler.class) && 
					isRunOnStart( beanMethodInfo.getMethod().getAnnotation(TimerScheduler.class).runOnStart())) 
				runTimerScheduler(beanMethodInfo.getObj(), beanMethodInfo.getMethod());
		}
	} 
	
	private boolean isRunOnStart(String runOnStart) {
		return StringUtils.isNotBlank(runOnStart) && Boolean.TRUE.toString().toLowerCase().equals(runOnStart.toLowerCase().trim());
	}


	protected void runTimerScheduler(Object obj, Method method) {
		
		TimerScheduler timerScheduler = method.getAnnotation(TimerScheduler.class);
		String time       = ProjectContent.instance().parseValue(timerScheduler.time());
		String timeformat = ProjectContent.instance().parseValue(timerScheduler.timeformat());
		String delay      = ProjectContent.instance().parseValue(timerScheduler.delay());
		
		long delayValue = StringUtils.isNotBlank(delay)?Long.valueOf(delay):0;
		if(StringUtils.isNotBlank(time)){ 
			if(StringUtils.isBlank(timeformat)) timeformat = ProjectContent.instance().getDateTimeFormat();
				 
				try {
					DateFormat formatter = new SimpleDateFormat(timeformat);
					Date date = formatter.parse(time);
					
			 
					new Timer().schedule(new TimerTask(){
						
						@Override
						public void run() {
							try {
								Thread.sleep(delayValue);
								method.invoke(obj);
							} catch (IllegalAccessException | InvocationTargetException | InterruptedException e) { 
								logger.error(e.getMessage()); 
							} 
						}
					}
					, date);
				} catch (ParseException e) {
					logger.error(e.getMessage());
				} 
		}else{ 
			
			new Timer().schedule(new TimerTask(){
				
				@Override
				public void run() {
					try {
						method.invoke(obj);
					} catch (IllegalAccessException | InvocationTargetException e) { 
						logger.error(e.getMessage()); 
					} 
				}
			}
			, delayValue); 
		}
	}
 
	protected void runTimerFixedRateScheduler(Object obj, Method method) {
		TimerFixeRateScheduler timerFixeDelayScheduler = method.getAnnotation(TimerFixeRateScheduler.class);
		String firtTime   = ProjectContent.instance().parseValue(timerFixeDelayScheduler.firstTime()); 
		String timeformat = ProjectContent.instance().parseValue(timerFixeDelayScheduler.dateformat());
		String period     = ProjectContent.instance().parseValue(timerFixeDelayScheduler.period());
		String delay 	  = ProjectContent.instance().parseValue(timerFixeDelayScheduler.delay());
		
		long delayValue = StringUtils.isNotBlank(delay)?Long.valueOf(delay):0;
		long periodValue = StringUtils.isNotBlank(period)?Long.valueOf(period):0;
		
		if(StringUtils.isNotBlank(firtTime)){ 
			if(StringUtils.isBlank(timeformat)) timeformat = ProjectContent.instance().getDateTimeFormat();
			
			try {
				DateFormat formatter = new SimpleDateFormat(timeformat);
				Date date = formatter.parse(firtTime);
				
				new Timer().scheduleAtFixedRate(new TimerTask(){
					
					@Override
					public void run() {
						try {
							Thread.sleep(delayValue);
							method.invoke(obj);
						} catch (IllegalAccessException | InvocationTargetException | InterruptedException e) { 
							logger.error(e.getMessage()); 
						} 
					}
				}
				, date, periodValue); 
			
			} catch (ParseException e) {
				logger.error(e.getMessage()); 
			} 
		}else {
			
			new Timer().scheduleAtFixedRate(new TimerTask(){
				
				@Override
				public void run() {
					try { 
						Thread.sleep(delayValue);
						method.invoke(obj);
					} catch (IllegalAccessException | InvocationTargetException | InterruptedException e) { 
						logger.error(e.getMessage()); 
					} 
				}
			}
			, delayValue, periodValue); 
		}		 
		
	}


	protected void runTimerFixedDelayScheduler(Object obj, Method method) {
		TimerFixeDelayScheduler timerFixeDelayScheduler = method.getAnnotation(TimerFixeDelayScheduler.class);
		String firtTime   = ProjectContent.instance().parseValue(timerFixeDelayScheduler.firstTime()); 
		String timeformat = ProjectContent.instance().parseValue(timerFixeDelayScheduler.dateformat());
		String period     = ProjectContent.instance().parseValue(timerFixeDelayScheduler.period());
		String delay 	  = ProjectContent.instance().parseValue(timerFixeDelayScheduler.delay());
		
		long delayValue  = StringUtils.isNotBlank(delay)?Long.valueOf(delay):0;
		long periodValue = StringUtils.isNotBlank(period)?Long.valueOf(period):0;
		
		if(StringUtils.isNotBlank(firtTime)){
			try {
				Date date;
				if(C_DATE_TIME_NOW.equalsIgnoreCase(firtTime)){
					date = new Date();
				}else {
					if(StringUtils.isBlank(timeformat)) { 
							timeformat = ProjectContent.instance().getDateTimeFormat();
						}
						DateFormat formatter = new SimpleDateFormat(timeformat);
						date = formatter.parse(firtTime);
					}
				
				new Timer().schedule(new TimerTask(){
					
					@Override
					public void run() {
						try {
							Thread.sleep(delayValue);
							method.invoke(obj);
						} catch (IllegalAccessException | InvocationTargetException | InterruptedException e) { 
							logger.error(e.getMessage()); 
						} 
					}
				}
				, date, periodValue); 
			
			} catch (ParseException e) {
				logger.error(e.getMessage()); 
			} 
		}else {
			
			new Timer().schedule(new TimerTask(){
				
				@Override
				public void run() {
					try { 
						method.invoke(obj);
					} catch (IllegalAccessException | InvocationTargetException  e) { 
						logger.error(e.getMessage()); 
					} 
				}
			}
			, delayValue, periodValue); 
		}
	}
 
	
	
	
}
