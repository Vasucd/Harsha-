package com.company.core.core.services.config.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.company.core.core.services.config.SiteConfigurationService;

@Component(service = SiteConfigurationService.class, immediate = true)
@Designate(ocd = SiteConfigurationServiceImpl.Config.class)
public class SiteConfigurationServiceImpl implements SiteConfigurationService {

    @ObjectClassDefinition(name = "Company Site Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Company Name")
        String companyName() default "Company";

        @AttributeDefinition(name = "Footer Text")
        String footerText() default "All rights reserved.";

        @AttributeDefinition(name = "Support Email")
        String supportEmail() default "support@company.com";
    }

    private String companyName;
    private String footerText;
    private String supportEmail;

    @org.osgi.service.component.annotations.Activate
    protected void activate(Config config) {
        companyName = config.companyName();
        footerText = config.footerText();
        supportEmail = config.supportEmail();
    }

    @Override
    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String getFooterText() {
        return footerText;
    }

    @Override
    public String getSupportEmail() {
        return supportEmail;
    }
}
