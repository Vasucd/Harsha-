package com.company.core.core.models;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        adapters = HeroBannerModel.class,
        resourceType = HeroBannerModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class HeroBannerModel {

    public static final String RESOURCE_TYPE =
            "company/components/hero-banner";

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String image;

    @ValueMapValue
    private String altText;

    @ValueMapValue
    private String ctaLabel;

    @ValueMapValue
    private String ctaLink;

    @ValueMapValue
    private boolean openInNewTab;

    private String resolvedCtaLink;

    @PostConstruct
    protected void init() {
        resolvedCtaLink = normalizeLink(ctaLink);
    }

    private String normalizeLink(String link) {
        if (StringUtils.isBlank(link)) {
            return StringUtils.EMPTY;
        }

        if (link.startsWith("/content/") && !link.endsWith(".html")) {
            return link + ".html";
        }

        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getAltText() {
        return altText;
    }

    public String getCtaLabel() {
        return ctaLabel;
    }

    public String getCtaLink() {
        return resolvedCtaLink;
    }

    public boolean isOpenInNewTab() {
        return openInNewTab;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(title)
                && StringUtils.isBlank(description)
                && StringUtils.isBlank(image);
    }
}