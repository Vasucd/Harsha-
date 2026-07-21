package com.company.core.core.models;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        adapters = ServicesCardModel.class,
        resourceType = ServicesCardModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ServicesCardModel {

    public static final String RESOURCE_TYPE = "company/components/services-card";

    @ValueMapValue
    private String sectionTitle;

    @ChildResource(name = "services")
    private List<ServiceItem> services;

    public String getSectionTitle() {
        return sectionTitle;
    }

    public List<ServiceItem> getServices() {
        return services == null ? Collections.emptyList() : services;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(sectionTitle) && (services == null || services.isEmpty());
    }

    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class ServiceItem {

        @ValueMapValue
        private String icon;

        @ValueMapValue
        private String title;

        @ValueMapValue
        private String description;

        @ValueMapValue
        private String ctaLabel;

        @ValueMapValue
        private String ctaLink;

        public String getIcon() {
            return StringUtils.isBlank(icon) ? StringUtils.EMPTY : icon;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
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
    }
}
