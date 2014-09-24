package com.lunex.scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


/**
 * @author DuyNguyen The Class JobScheduler.
 */
public class JobScheduler {

  public static void run() {
    try {
      // specify the job' s details..
      JobDetail job = JobBuilder.newJob(TelnetJob.class).withIdentity("TelnetJob").build();

      // specify the running period of the job
      Trigger trigger =
          TriggerBuilder
              .newTrigger()
              .withSchedule(
                  SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever())
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
