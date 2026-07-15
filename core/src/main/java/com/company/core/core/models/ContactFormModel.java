package com.company.core.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        adapters = ContactFormModel.class,
        resourceType = ContactFormModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ContactFormModel {

    public static final String RESOURCE_TYPE = "company/components/contact-form";

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String successMessage;

    @ValueMapValue
    private String errorMessage;

    public String getTitle() {
        return title;
    }

    public String getSuccessMessage() {
        return StringUtils.defaultIfBlank(successMessage, "Your request has been submitted.");
    }

    public String getErrorMessage() {
        return StringUtils.defaultIfBlank(errorMessage, "Unable to submit request. Please try again.");
    }

    public String getActionPath() {
        return "/bin/company/contact";
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(title);
    }
}
