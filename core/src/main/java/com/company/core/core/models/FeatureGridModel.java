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
        adapters = FeatureGridModel.class,
        resourceType = FeatureGridModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FeatureGridModel {

    public static final String RESOURCE_TYPE = "company/components/feature-grid";

    @ValueMapValue
    private String sectionTitle;

    @ValueMapValue
    private String sectionIntro;

    @ChildResource(name = "features")
    private List<FeatureItem> features;

    public String getSectionTitle() {
        return sectionTitle;
    }

    public String getSectionIntro() {
        return sectionIntro;
    }

    public List<FeatureItem> getFeatures() {
        return features == null ? Collections.emptyList() : features;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(sectionTitle)
                && StringUtils.isBlank(sectionIntro)
                && (features == null || features.isEmpty());
    }

    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class FeatureItem {

        @ValueMapValue
        private String icon;

        @ValueMapValue
        private String title;

        @ValueMapValue
        private String description;

        public String getIcon() {
            return StringUtils.isBlank(icon) ? StringUtils.EMPTY : icon;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }
}
