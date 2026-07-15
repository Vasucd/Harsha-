package com.company.core.core.services.contact;

import org.apache.commons.lang3.StringUtils;

public class ContactFormRequest {

    private final String name;
    private final String email;
    private final String subject;
    private final String message;

    public ContactFormRequest(String name, String email, String subject, String message) {
        this.name = StringUtils.trimToEmpty(name);
        this.email = StringUtils.trimToEmpty(email);
        this.subject = StringUtils.trimToEmpty(subject);
        this.message = StringUtils.trimToEmpty(message);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }
}
