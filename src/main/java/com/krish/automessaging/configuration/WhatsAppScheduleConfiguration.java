package com.krish.automessaging.configuration;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import com.krish.automessaging.datamodel.pojo.WhatsAppMessaging;
import com.krish.automessaging.schedule.WhatsAppAutoJobSchedule;
import com.krish.automessaging.schedule.WhatsAppManualJobSchedule;

/**
 * The Class WhatsAppScheduleConfiguration.
 */
@Configuration
public class WhatsAppScheduleConfiguration {

    /**
     * WhatsApp manual job detail factory bean.
     *
     * @return the job detail factory bean
     */
    @Bean
    public JobDetailFactoryBean whatsAppManualJobDetailFactoryBean() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(WhatsAppManualJobSchedule.class);
        factoryBean.setDurability(true);
        return factoryBean;
    }

    /**
     * Trigger manual WhatsApp for every minute
     *
     * @param jobDetail
     *            the job detail
     *
     * @return the simple trigger factory bean
     */
    @Bean
    public SimpleTriggerFactoryBean triggerManualWhatsAppMinute(
            @Qualifier("whatsAppManualJobDetailFactoryBean") JobDetail jobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setRepeatInterval(WhatsAppMessaging.MINUTE_INTERVAL);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }

    /**
     * WhatsApp auto job detail factory bean.
     *
     * @return the job detail factory bean
     */
    @Bean
    public JobDetailFactoryBean whatsAppAutoJobDetailFactoryBean() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(WhatsAppAutoJobSchedule.class);
        factoryBean.setDurability(true);
        return factoryBean;
    }

    /**
     * Trigger auto WhatsApp for every minute
     *
     * @param jobDetail
     *            the job detail
     *
     * @return the simple trigger factory bean
     */
    @Bean
    public SimpleTriggerFactoryBean triggerAutoWhatsAppMinute(
            @Qualifier("whatsAppAutoJobDetailFactoryBean") JobDetail jobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setRepeatInterval(WhatsAppMessaging.MINUTE_INTERVAL);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }

    /**
     * Trigger auto WhatsApp for every hour
     *
     * @param jobDetail
     *            the job detail
     *
     * @return the simple trigger factory bean
     */
    @Bean
    public SimpleTriggerFactoryBean triggerAutoWhatsAppHourly(
            @Qualifier("whatsAppAutoJobDetailFactoryBean") JobDetail jobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setRepeatInterval(WhatsAppMessaging.HOURLY_INTERVAL);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }

    /**
     * Trigger auto WhatsApp for every Day
     *
     * @param jobDetail
     *            the job detail
     *
     * @return the simple trigger factory bean
     */
    @Bean
    public SimpleTriggerFactoryBean triggerAutoWhatsAppDaily(
            @Qualifier("whatsAppAutoJobDetailFactoryBean") JobDetail jobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setRepeatInterval(WhatsAppMessaging.DAILY_INTERVAL);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }

    /**
     * Trigger auto WhatsApp for every Week
     *
     * @param jobDetail
     *            the job detail
     *
     * @return the simple trigger factory bean
     */
    @Bean
    public SimpleTriggerFactoryBean triggerAutoWhatsAppWeekly(
            @Qualifier("whatsAppAutoJobDetailFactoryBean") JobDetail jobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setRepeatInterval(WhatsAppMessaging.WEEKLY_INTERVAL);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }

    /**
     * Trigger auto WhatsApp for every Month
     *
     * @param jobDetail
     *            the job detail
     *
     * @return the simple trigger factory bean
     */
    @Bean
    public SimpleTriggerFactoryBean triggerAutoWhatsAppMonthly(
            @Qualifier("whatsAppAutoJobDetailFactoryBean") JobDetail jobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setRepeatInterval(WhatsAppMessaging.MONTHLY_INTERVAL);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }

    /**
     * Trigger auto WhatsApp for every Year
     *
     * @param jobDetail
     *            the job detail
     *
     * @return the simple trigger factory bean
     */
    @Bean
    public SimpleTriggerFactoryBean triggerAutoWhatsAppYearly(
            @Qualifier("whatsAppAutoJobDetailFactoryBean") JobDetail jobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setRepeatInterval(WhatsAppMessaging.YEARLY_INTERVAL);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }
}
