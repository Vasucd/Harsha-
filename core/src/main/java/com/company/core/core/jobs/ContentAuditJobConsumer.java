package com.company.core.core.jobs;

import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.event.jobs.Job;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = JobConsumer.class,
        property = JobConsumer.PROPERTY_TOPICS + "=" + ContentAuditScheduler.JOB_TOPIC
)
public class ContentAuditJobConsumer implements JobConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentAuditJobConsumer.class);

    @Override
    public JobResult process(Job job) {
        String rootPath = job.getProperty("rootPath", "/content/company");
        try {
            long triggeredAt = job.getProperty("triggeredAt", System.currentTimeMillis());
            LOGGER.info("Content audit completed for {}. TriggeredAt={}", rootPath, triggeredAt);
            return JobResult.OK;
        } catch (Exception e) {
            LOGGER.error("Failed content audit job for {}", rootPath, e);
            return JobResult.FAILED;
        }
    }
}
