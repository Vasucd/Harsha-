package com.company.core.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.company.core.core.services.config.SiteConfigurationService;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = FooterConfigurationModel.class,
        resourceType = FooterConfigurationModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FooterConfigurationModel {

    public static final String RESOURCE_TYPE = "company/components/footer-config";

    @OSGiService
    private SiteConfigurationService siteConfigurationService;

    public String getCompanyName() {
        return siteConfigurationService != null ? siteConfigurationService.getCompanyName() : "Company";
    }

    public String getFooterText() {
        return siteConfigurationService != null ? siteConfigurationService.getFooterText() : "";
    }

    public String getSupportEmail() {
        return siteConfigurationService != null ? siteConfigurationService.getSupportEmail() : "";
    }
}
