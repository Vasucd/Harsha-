package com.company.core.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=/bin/company/blogsearch"
        }
)
public class BlogSearchServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ROOT = "/content/company/us/en/blog";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String query = StringUtils.lowerCase(StringUtils.trimToEmpty(request.getParameter("q")));
        String rootPath = StringUtils.defaultIfBlank(request.getParameter("rootPath"), DEFAULT_ROOT);
        int limit = parseLimit(request.getParameter("limit"));

        ResourceResolver resolver = request.getResourceResolver();
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        List<Page> matches = new ArrayList<>();
        if (pageManager != null) {
            Page rootPage = pageManager.getPage(rootPath);
            if (rootPage != null) {
                rootPage.listChildren().forEachRemaining(page -> {
                    String title = StringUtils.defaultString(page.getTitle(), page.getName());
                    String description = StringUtils.defaultString(page.getDescription());
                    String searchable = (title + " " + description).toLowerCase();
                    if (StringUtils.isBlank(query) || searchable.contains(query)) {
                        matches.add(page);
                    }
                });
            }
        }

        matches.sort(Comparator.comparing(Page::getLastModified, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        if (matches.size() > limit) {
            matches = matches.subList(0, limit);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(toJson(matches));
    }

    private int parseLimit(String limitParam) {
        try {
            int limit = Integer.parseInt(limitParam);
            return limit > 0 ? limit : 10;
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    private String toJson(List<Page> pages) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"results\":[");
        for (int i = 0; i < pages.size(); i++) {
            Page page = pages.get(i);
            if (i > 0) {
                builder.append(',');
            }
            builder.append('{')
                    .append("\"title\":\"").append(escapeJson(StringUtils.defaultString(page.getTitle(), page.getName()))).append("\",")
                    .append("\"description\":\"").append(escapeJson(StringUtils.defaultString(page.getDescription()))).append("\",")
                    .append("\"path\":\"").append(escapeJson(page.getPath())).append(".html\"")
                    .append('}');
        }
        builder.append("]}");
        return builder.toString();
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
