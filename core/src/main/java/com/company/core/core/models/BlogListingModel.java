package com.company.core.core.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = BlogListingModel.class,
        resourceType = BlogListingModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class BlogListingModel {

    public static final String RESOURCE_TYPE = "company/components/blog-listing";
    private static final String DEFAULT_BLOG_ROOT = "/content/company/us/en/blog";
    private static final int DEFAULT_MAX_ITEMS = 5;

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue
    @Via("resource")
    private String sectionTitle;

    @ValueMapValue
    @Via("resource")
    private String blogRootPath;

    @ValueMapValue
    @Via("resource")
    private Integer maxItems;

    private List<BlogItem> blogItems;

    @PostConstruct
    protected void init() {
        blogItems = new ArrayList<>();
        Resource componentResource = resource;
        if (componentResource == null && request != null) {
            componentResource = request.getResource();
        }

        ValueMap authoredValues = componentResource != null ? componentResource.getValueMap() : null;
        if (StringUtils.isBlank(sectionTitle) && authoredValues != null) {
            sectionTitle = authoredValues.get("sectionTitle", String.class);
        }
        if (StringUtils.isBlank(blogRootPath) && authoredValues != null) {
            blogRootPath = authoredValues.get("blogRootPath", String.class);
        }
        if (maxItems == null && authoredValues != null) {
            maxItems = authoredValues.get("maxItems", Integer.class);
        }

        if (currentPage == null && request != null) {
            ResourceResolver resolver = request.getResourceResolver();
            PageManager pageManager = resolver.adaptTo(PageManager.class);
            if (pageManager != null && request.getResource() != null) {
                currentPage = pageManager.getContainingPage(request.getResource());
            }
        }

        if (currentPage == null) {
            return;
        }

        ResourceResolver resolver = currentPage.getContentResource().getResourceResolver();
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return;
        }

        String rootPath = StringUtils.defaultIfBlank(blogRootPath, DEFAULT_BLOG_ROOT);
        Page blogRoot = pageManager.getPage(rootPath);
        if (blogRoot == null) {
            return;
        }

        int limit = maxItems == null || maxItems < 1 ? DEFAULT_MAX_ITEMS : maxItems;

        blogRoot.listChildren().forEachRemaining(page -> blogItems.add(toBlogItem(page)));
        blogItems = blogItems.stream()
                .sorted(Comparator.comparing(BlogItem::getSortDate, Comparator.nullsLast(Date::compareTo)).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private BlogItem toBlogItem(Page page) {
        // Prefer an authored publish date; fall back to last-modified, then created.
        Date sortDate = page.getProperties().get("publishDate", Date.class);
        if (sortDate == null) {
            sortDate = page.getLastModified() != null
                    ? page.getLastModified().getTime()
                    : page.getProperties().get("jcr:created", Date.class);
        }
        String title = StringUtils.defaultIfBlank(page.getTitle(), page.getName());
        String description = StringUtils.defaultIfBlank(page.getDescription(), StringUtils.EMPTY);
        return new BlogItem(title, description, page.getPath() + ".html", sortDate);
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public List<BlogItem> getBlogItems() {
        return blogItems;
    }

    public boolean isEmpty() {
        return blogItems == null || blogItems.isEmpty();
    }

    public static class BlogItem {
        private final String title;
        private final String description;
        private final String path;
        private final Date sortDate;

        public BlogItem(String title, String description, String path, Date sortDate) {
            this.title = title;
            this.description = description;
            this.path = path;
            this.sortDate = sortDate;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPath() {
            return path;
        }

        public Date getSortDate() {
            return sortDate;
        }
    }
}
