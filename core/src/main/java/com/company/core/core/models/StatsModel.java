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
        adapters = StatsModel.class,
        resourceType = StatsModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class StatsModel {

    public static final String RESOURCE_TYPE = "company/components/stats";

    @ChildResource(name = "items")
    private List<StatItem> items;

    public List<StatItem> getItems() {
        return items == null ? Collections.emptyList() : items;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class StatItem {

        @ValueMapValue
        private String number;

        @ValueMapValue
        private String label;

        public String getNumber() {
            return StringUtils.defaultString(number);
        }

        public String getLabel() {
            return StringUtils.defaultString(label);
        }
    }
}
