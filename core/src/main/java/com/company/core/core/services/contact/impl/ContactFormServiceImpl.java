package com.company.core.core.services.contact.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.company.core.core.services.contact.ContactFormRequest;
import com.company.core.core.services.contact.ContactFormService;

@Component(service = ContactFormService.class)
@Designate(ocd = ContactFormServiceImpl.Config.class)
public class ContactFormServiceImpl implements ContactFormService {

    @ObjectClassDefinition(name = "Company Contact Form Service")
    public @interface Config {
        @AttributeDefinition(name = "Submission storage root")
        String storageRootPath() default "/var/company/contact-submissions";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactFormServiceImpl.class);
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private String storageRootPath;

    @org.osgi.service.component.annotations.Activate
    protected void activate(Config config) {
        this.storageRootPath = config.storageRootPath();
    }

    @Override
    public SubmissionResult submit(ContactFormRequest request, ResourceResolver resolver) {
        String validationError = validate(request);
        if (validationError != null) {
            return new SubmissionResult(false, validationError);
        }

        try {
            String folderPath = StringUtils.defaultIfBlank(storageRootPath, "/var/company/contact-submissions");
            Resource folder = ResourceUtil.getOrCreateResource(
                    resolver,
                    folderPath,
                    JcrConstants.NT_UNSTRUCTURED,
                    JcrConstants.NT_UNSTRUCTURED,
                    true);

            Map<String, Object> data = new HashMap<>();
            data.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
            data.put("name", request.getName());
            data.put("email", request.getEmail());
            data.put("subject", request.getSubject());
            data.put("message", request.getMessage());
            data.put("submittedAt", Calendar.getInstance());

            resolver.create(folder, "submission-" + UUID.randomUUID(), data);
            resolver.commit();

            LOGGER.info("Contact form submitted by '{}' <{}>", request.getName(), request.getEmail());
            return new SubmissionResult(true, "Your request has been submitted successfully.");
        } catch (PersistenceException e) {
            LOGGER.error("Unable to persist contact form submission", e);
            return new SubmissionResult(false, "Unable to submit your request right now.");
        }
    }

    private String validate(ContactFormRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            return "Name is required.";
        }
        if (StringUtils.isBlank(request.getEmail()) || !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            return "A valid email is required.";
        }
        if (StringUtils.isBlank(request.getMessage())) {
            return "Message is required.";
        }
        return null;
    }
}
