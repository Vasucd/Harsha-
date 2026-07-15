package com.company.core.core.jobs;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Runnable.class, immediate = true, property = {
        "scheduler.expression=0 0/30 * * * ?",
        "scheduler.concurrent=false"
})
@Designate(ocd = ContentAuditScheduler.Config.class)
public class ContentAuditScheduler implements Runnable {

    @ObjectClassDefinition(name = "Company Content Audit Scheduler")
    public @interface Config {
        @AttributeDefinition(name = "Audit root path")
        String auditRootPath() default "/content/company";
    }

    public static final String JOB_TOPIC = "company/jobs/content-audit";
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentAuditScheduler.class);

    @org.osgi.service.component.annotations.Reference
    private transient JobManager jobManager;

    private String auditRootPath;

    @org.osgi.service.component.annotations.Activate
    protected void activate(Config config) {
        this.auditRootPath = config.auditRootPath();
    }

    @Override
    public void run() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("rootPath", auditRootPath);
        properties.put("triggeredAt", System.currentTimeMillis());
        jobManager.addJob(JOB_TOPIC, properties);
        LOGGER.info("Queued content audit job for root path {}", auditRootPath);
    }
}
