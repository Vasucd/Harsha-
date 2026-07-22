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
        adapters = TestimonialsModel.class,
        resourceType = TestimonialsModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class TestimonialsModel {

    public static final String RESOURCE_TYPE = "company/components/testimonials";

    @ValueMapValue
    private String sectionTitle;

    @ChildResource(name = "items")
    private List<TestimonialItem> items;

    public String getSectionTitle() {
        return sectionTitle;
    }

    public List<TestimonialItem> getItems() {
        return items == null ? Collections.emptyList() : items;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class TestimonialItem {

        @ValueMapValue(name = "rating")
        private String ratingValue;

        @ValueMapValue
        private String quote;

        @ValueMapValue
        private String name;

        @ValueMapValue
        private String program;

        public int getRating() {
            int r = 5;
            if (StringUtils.isNotBlank(ratingValue)) {
                try {
                    r = Integer.parseInt(ratingValue.trim());
                } catch (NumberFormatException e) {
                    r = 5;
                }
            }
            return Math.max(0, Math.min(5, r));
        }

        public String getStars() {
            int r = getRating();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                sb.append(i < r ? '★' : '☆');
            }
            return sb.toString();
        }

        public String getQuote() {
            return quote;
        }

        public String getName() {
            return name;
        }

        public String getProgram() {
            return program;
        }
    }
}
