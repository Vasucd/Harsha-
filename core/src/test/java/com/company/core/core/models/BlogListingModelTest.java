package com.company.core.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.company.core.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class BlogListingModelTest {

    private final AemContext context = AppAemContext.newAemContext();
    private BlogListingModel model;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(BlogListingModel.class);

        context.create().page("/content/company/us/en/blog");
        context.create().page("/content/company/us/en/blog/post-one");
        context.create().page("/content/company/us/en/blog/post-two");

        context.resourceResolver().getResource("/content/company/us/en/blog/post-one/jcr:content")
                .adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("jcr:title", "Post One");
        context.resourceResolver().getResource("/content/company/us/en/blog/post-one/jcr:content")
                .adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("jcr:description", "First post");
        context.resourceResolver().getResource("/content/company/us/en/blog/post-two/jcr:content")
                .adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("jcr:title", "Post Two");
        context.resourceResolver().getResource("/content/company/us/en/blog/post-two/jcr:content")
                .adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("jcr:description", "Second post");

        Calendar now = Calendar.getInstance();
        context.resourceResolver().getResource("/content/company/us/en/blog/post-one/jcr:content")
                .adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("cq:lastModified", now);
        context.resourceResolver().getResource("/content/company/us/en/blog/post-two/jcr:content")
                .adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("cq:lastModified", now);

        context.create().page("/content/test");
        Resource listingResource = context.create().resource("/content/test/jcr:content/bloglisting",
                "sling:resourceType", "company/components/blog-listing",
                "sectionTitle", "Latest Blogs",
                "blogRootPath", "/content/company/us/en/blog",
                "maxItems", 1);
        context.currentResource(listingResource);
        context.request().setResource(listingResource);
        context.currentPage(context.pageManager().getPage("/content/test"));
        model = context.request().adaptTo(BlogListingModel.class);
    }

    @Test
    void testBlogListingReturnsLimitedItems() {
        assertEquals("Latest Blogs", model.getSectionTitle());
        assertEquals(1, model.getBlogItems().size());
        assertFalse(model.isEmpty());
    }
}
