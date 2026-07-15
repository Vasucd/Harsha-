package com.company.core.core.services.contact;

import org.apache.sling.api.resource.ResourceResolver;

public interface ContactFormService {

    SubmissionResult submit(ContactFormRequest request, ResourceResolver resolver);

    class SubmissionResult {
        private final boolean success;
        private final String message;

        public SubmissionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
