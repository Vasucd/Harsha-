package com.company.core.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.Page;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = AuthorInfoModel.class,
        resourceType = AuthorInfoModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class AuthorInfoModel {

    public static final String RESOURCE_TYPE = "company/components/author-info";

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private Resource resource;

    private String authorName;
    private String authorRole;
    private String authorBio;

    @javax.annotation.PostConstruct
    protected void init() {
        if (currentPage == null) {
            return;
        }

        String fragmentPath = currentPage.getProperties().get("authorFragmentPath", String.class);
        if (StringUtils.isBlank(fragmentPath) && resource != null) {
            fragmentPath = resource.getValueMap().get("authorFragmentPath", String.class);
        }

        if (StringUtils.isBlank(fragmentPath)) {
            return;
        }

        Resource fragmentData = currentPage.getContentResource().getResourceResolver()
                .getResource(fragmentPath + "/jcr:content/data/master");
        if (fragmentData == null) {
            return;
        }

        authorName = fragmentData.getValueMap().get("authorName", String.class);
        authorRole = fragmentData.getValueMap().get("authorRole", String.class);
        authorBio = fragmentData.getValueMap().get("authorBio", String.class);
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public String getAuthorBio() {
        return authorBio;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(authorName) && StringUtils.isBlank(authorRole) && StringUtils.isBlank(authorBio);
    }
}
