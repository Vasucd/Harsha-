package com.company.core.core.models;

import java.util.ArrayList;
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
        adapters = PricingModel.class,
        resourceType = PricingModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class PricingModel {

    public static final String RESOURCE_TYPE = "company/components/pricing";

    @ValueMapValue
    private String sectionTitle;

    @ValueMapValue
    private String subtitle;

    @ChildResource(name = "plans")
    private List<Plan> plans;

    public String getSectionTitle() {
        return sectionTitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<Plan> getPlans() {
        return plans == null ? Collections.emptyList() : plans;
    }

    public boolean isEmpty() {
        return (plans == null || plans.isEmpty()) && StringUtils.isBlank(sectionTitle);
    }

    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class Plan {

        @ValueMapValue
        private String name;

        @ValueMapValue
        private String price;

        @ValueMapValue
        private String period;

        @ValueMapValue
        private boolean featured;

        @ValueMapValue
        private String badge;

        @ValueMapValue
        private String intro;

        /** Preferred: the "Features" multifield (stored as a multi-value property). */
        @ValueMapValue
        private String[] features;

        @ValueMapValue
        private String feature1;

        @ValueMapValue
        private String feature2;

        @ValueMapValue
        private String feature3;

        @ValueMapValue
        private String feature4;

        @ValueMapValue
        private String feature5;

        @ValueMapValue
        private String feature6;

        @ValueMapValue
        private String ctaLabel;

        @ValueMapValue
        private String ctaLink;

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public String getPeriod() {
            return period;
        }

        public boolean isFeatured() {
            return featured;
        }

        public String getBadge() {
            return badge;
        }

        public String getIntro() {
            return intro;
        }

        public List<String> getFeatures() {
            List<String> list = new ArrayList<>();
            // the dialog's Feature 1..6 fields are the authoritative source
            for (String f : new String[]{feature1, feature2, feature3, feature4, feature5, feature6}) {
                if (StringUtils.isNotBlank(f)) {
                    list.add(f.trim());
                }
            }
            // fall back to a multi-value "features" property if those are empty
            if (list.isEmpty() && features != null) {
                for (String f : features) {
                    if (StringUtils.isNotBlank(f)) {
                        list.add(f.trim());
                    }
                }
            }
            return list;
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
