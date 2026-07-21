package com.company.core.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        adapters = CtaBandModel.class,
        resourceType = CtaBandModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CtaBandModel {

    public static final String RESOURCE_TYPE = "company/components/cta-band";

    @ValueMapValue
    private String heading;

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String ctaLabel;

    @ValueMapValue
    private String ctaLink;

    @ValueMapValue
    private boolean openInNewTab;

    public String getHeading() {
        return heading;
    }

    public String getText() {
        return text;
    }

    public String getCtaLabel() {
        return ctaLabel;
    }

    public String getCtaLink() {
        if (StringUtils.isBlank(ctaLink)) {
            return StringUtils.EMPTY;
        }
        return ctaLink.startsWith("/content/") && !ctaLink.endsWith(".html")
                ? ctaLink + ".html"
                : ctaLink;
    }

    public boolean isOpenInNewTab() {
        return openInNewTab;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(heading)
                && StringUtils.isBlank(text)
                && StringUtils.isBlank(ctaLabel);
    }
}
