package com.company.core.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.company.core.core.services.contact.ContactFormRequest;
import com.company.core.core.services.contact.ContactFormService;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths=/bin/company/contact"
        }
)
public class ContactFormServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @org.osgi.service.component.annotations.Reference
    private transient ContactFormService contactFormService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        ContactFormRequest contactRequest = new ContactFormRequest(
                request.getParameter("name"),
                request.getParameter("email"),
                request.getParameter("subject"),
                request.getParameter("message"));

        ContactFormService.SubmissionResult result =
                contactFormService.submit(contactRequest, request.getResourceResolver());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(result.isSuccess() ? SlingHttpServletResponse.SC_OK : SlingHttpServletResponse.SC_BAD_REQUEST);

        String message = result.getMessage() == null ? "" : result.getMessage().replace("\\", "\\\\").replace("\"", "\\\"");
        response.getWriter().write("{\"success\":" + result.isSuccess() + ",\"message\":\"" + message + "\"}");
    }
}
