package com.lunex.httpproxy.scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.lunex.httpproxy.util.Configuration;

// TODO: Auto-generated Javadoc
/**
 * The Class JobScheduler.
 */
public class JobScheduler {

  /**
   * Run.
   */
  public static void run() {
    try {
      // specify the job' s details..
      JobDetail job = JobBuilder.newJob(TelnetJob.class).withIdentity("TelnetJob").build();

      // specify the running period of the job
      Trigger trigger =
          TriggerBuilder
              .newTrigger()
              .withSchedule(
                  SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(Configuration.getScheduleTime()).repeatForever())
              .build();

      // schedule the job
      SchedulerFactory schFactory = new StdSchedulerFactory();
      Scheduler sch = schFactory.getScheduler();
      sch.start();
      sch.scheduleJob(job, trigger);

    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

}
