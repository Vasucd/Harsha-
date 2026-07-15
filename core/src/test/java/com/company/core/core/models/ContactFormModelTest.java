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
class ContactFormModelTest {

    private final AemContext context = AppAemContext.newAemContext();
    private ContactFormModel model;

    @BeforeEach
    void setUp() {
        Resource resource = context.create().resource("/content/test/contact-form",
                "sling:resourceType", "company/components/contact-form",
                "title", "Contact Us",
                "successMessage", "Submitted",
                "errorMessage", "Failed");
        model = resource.adaptTo(ContactFormModel.class);
    }

    @Test
    void testContactFormModelValues() {
        assertEquals("Contact Us", model.getTitle());
        assertEquals("/bin/company/contact", model.getActionPath());
        assertFalse(model.isEmpty());
    }
}
