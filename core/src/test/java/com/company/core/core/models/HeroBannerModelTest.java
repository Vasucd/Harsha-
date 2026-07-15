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
class HeroBannerModelTest {

    private final AemContext context = AppAemContext.newAemContext();
    private HeroBannerModel model;

    @BeforeEach
    void setUp() {
        Resource resource = context.create().resource("/content/test/hero",
                "sling:resourceType", "company/components/hero-banner",
                "title", "Hero title",
                "description", "Hero description",
                "image", "/content/dam/company/hero.jpg",
                "ctaLabel", "Read More",
                "ctaLink", "/content/company/us/en/about-us");
        model = resource.adaptTo(HeroBannerModel.class);
    }

    @Test
    void testLinkNormalization() {
        assertEquals("/content/company/us/en/about-us.html", model.getCtaLink());
        assertFalse(model.isEmpty());
    }
}
