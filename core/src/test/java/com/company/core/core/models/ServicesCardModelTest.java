package com.company.core.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.company.core.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class ServicesCardModelTest {

    private final AemContext context = AppAemContext.newAemContext();
    private ServicesCardModel model;

    @BeforeEach
    void setUp() {
        context.create().resource("/content/test/services",
                "sling:resourceType", "company/components/services-card",
                "sectionTitle", "Our Services");
        context.create().resource("/content/test/services/services/item0",
                "title", "Consulting",
                "description", "Business consulting",
                "ctaLabel", "Know More",
                "ctaLink", "/content/company/us/en/our-services");

        Resource resource = context.resourceResolver().getResource("/content/test/services");
        model = resource.adaptTo(ServicesCardModel.class);
    }

    @Test
    void testServicesMapped() {
        assertEquals("Our Services", model.getSectionTitle());
        assertEquals(1, model.getServices().size());
        assertEquals("/content/company/us/en/our-services.html", model.getServices().get(0).getCtaLink());
        assertFalse(model.isEmpty());
    }
}
