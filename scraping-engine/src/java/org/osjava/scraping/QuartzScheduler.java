package org.osjava.scraping;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

import org.quartz.impl.StdSchedulerFactory;
import org.quartz.JobDetail;
import org.quartz.JobDataMap;
import org.quartz.Job;
//import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;

import java.text.ParseException;

import org.apache.log4j.Logger;

/// BEWARE: Name clash. Scheduler and org.quartz.Scheduler
public class QuartzScheduler implements Scheduler {

    private static Logger logger = Logger.getLogger(QuartzScheduler.class);

    private org.quartz.Scheduler quartz;

    public QuartzScheduler() {
        try {
            quartz = new StdSchedulerFactory().getScheduler();
            quartz.start();
        } catch(SchedulerException se) {
            throw new RuntimeException("Failed to schedule job. " + se);
        }
    }

    public void schedule(Config cfg, Session session, Runner runner) {
        String schedule = cfg.getString("schedule");

        // set default
        if(StringUtils.isEmpty(schedule)) {
            schedule = "startup";
        } 

        // this should be one of the following:
        // a cron-like line or a 'startup' option 
        if("startup".equalsIgnoreCase(schedule)) { 
            Scheduler sch = new SimpleScheduler();
            sch.schedule(cfg, session, runner);
        } else {
            // assume it's a cron statement
            try {
                JobDetail detail = new JobDetail(cfg.getContext()+"job",
                    quartz.DEFAULT_GROUP, QuartzJob.class);
                JobDataMap map = detail.getJobDataMap();
                map.put("cfg", cfg);
                map.put("session", session);
                map.put("runner", runner);

                logger.debug("SCHEDULE: "+schedule);
                Trigger trigger = null;
                if("cron".equalsIgnoreCase(schedule)) {
                    String cronTxt = cfg.getString("schedule.cron");
                    CronTrigger cron = new CronTrigger(cfg.getContext()+"trigger", quartz.DEFAULT_GROUP);
                    cron.setCronExpression(cronTxt);
                    trigger = cron;
                } else {
//                    Date start = cfg.getDate("start");
//                    Date end = cfg.getDate("end");
                    int repeat = cfg.getInt("schedule.repeat");
                    if(repeat == -1) {
                        repeat = SimpleTrigger.REPEAT_INDEFINITELY;
                    }
                    int interval = cfg.getInt("schedule.interval");
                    logger.debug("Re:"+repeat);
                    logger.debug("In:"+interval);
                    SimpleTrigger simp = new SimpleTrigger(cfg.getContext()+"trigger",
                    quartz.DEFAULT_GROUP, new Date(), null, repeat, interval);
                    trigger = simp;
                }

                quartz.scheduleJob(detail, trigger);
            } catch(ParseException pe) {
                throw new RuntimeException("Failed to parse cron. " + pe);
            } catch(SchedulerException se) {
                throw new RuntimeException("Failed to schedule job. " + se);
            }
        }
    }

}
