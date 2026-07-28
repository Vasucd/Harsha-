package com.company.core.core.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private static final int DEFAULT_LIMIT = 10;

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        final String query = StringUtils
                .trimToEmpty(request.getParameter("q"))
                .toLowerCase(Locale.ROOT);

        final String rootPath = StringUtils.defaultIfBlank(
                request.getParameter("rootPath"),
                DEFAULT_ROOT
        );

        final int limit = parseLimit(request.getParameter("limit"));

        ResourceResolver resolver = request.getResourceResolver();
        PageManager pageManager = resolver.adaptTo(PageManager.class);

        final List<Page> matches = new ArrayList<>();

        if (pageManager != null) {
            Page rootPage = pageManager.getPage(rootPath);

            if (rootPage != null) {
                rootPage.listChildren().forEachRemaining(page -> {
                    String title = StringUtils.defaultString(
                            page.getTitle(),
                            page.getName()
                    );

                    String description = StringUtils.defaultString(
                            page.getDescription()
                    );

                    String searchable = (title + " " + description)
                            .toLowerCase(Locale.ROOT);

                    if (StringUtils.isBlank(query)
                            || searchable.contains(query)) {
                        matches.add(page);
                    }
                });
            }
        }

        matches.sort(
                Comparator.comparing(
                        BlogSearchServlet::sortDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()
        );

        List<Page> results;

        if (matches.size() > limit) {
            results = matches.subList(0, limit);
        } else {
            results = matches;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(toJson(results));
    }

    private static Date sortDate(Page page) {
        Date publishDate = page.getProperties().get("publishDate", Date.class);
        if (publishDate != null) {
            return publishDate;
        }
        Calendar lastMod = page.getLastModified();
        if (lastMod != null) {
            return lastMod.getTime();
        }
        return page.getProperties().get("jcr:created", Date.class);
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(date);
    }

    private int parseLimit(String limitParam) {
        try {
            int limit = Integer.parseInt(limitParam);
            return limit > 0 ? limit : DEFAULT_LIMIT;
        } catch (NumberFormatException e) {
            return DEFAULT_LIMIT;
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

            String title = StringUtils.defaultString(
                    page.getTitle(),
                    page.getName()
            );

            String description = StringUtils.defaultString(
                    page.getDescription()
            );

            String author = StringUtils.defaultString(
                    page.getProperties().get("authorName", String.class)
            );

            String date = formatDate(sortDate(page));

            builder.append('{')
                    .append("\"title\":\"")
                    .append(escapeJson(title))
                    .append("\",")
                    .append("\"description\":\"")
                    .append(escapeJson(description))
                    .append("\",")
                    .append("\"author\":\"")
                    .append(escapeJson(author))
                    .append("\",")
                    .append("\"date\":\"")
                    .append(escapeJson(date))
                    .append("\",")
                    .append("\"path\":\"")
                    .append(escapeJson(page.getPath()))
                    .append(".html\"")
                    .append('}');
        }

        builder.append("]}");

        return builder.toString();
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}