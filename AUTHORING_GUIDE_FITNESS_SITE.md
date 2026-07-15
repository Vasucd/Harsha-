# Fitness Site Authoring Guide (AEM)

This guide helps new authors update the fitness website without any coding.

## 1) Open the Site in AEM Author

1. Go to **Sites**.
2. Open: `/content/company/us/en`.
3. Select a page (`Home`, `About Us`, `Our Services`, `Blog`, `Contact Us`).
4. Click **Edit**.

## 2) Insert Components on a Page

1. In page editor, open the left rail and select **Components**.
2. Drag required component into the page container:
   - `Hero Banner`
   - `Services Card`
   - `Blog Listing`
   - `Contact Form`
   - `Author Info`
   - `Text`
3. Click the component and use the **wrench icon (Configure)** to edit fields.

## 3) Hero Banner (Background Image + CTA)

1. Add `Hero Banner`.
2. Configure:
   - **Title**
   - **Description**
   - **Background Image** (DAM path)
   - **CTA Label**
   - **CTA Link**
3. Save and refresh.  
   The banner now uses direct `background-image`, so selected DAM image renders correctly.

## 4) Services Card (Program Tiles)

1. Add `Services Card`.
2. Set **Section Title**.
3. In **Services** multifield, add one item per program:
   - Title
   - Description
   - CTA Label
   - CTA Link
4. Reorder items by dragging multifield rows.

## 5) Blog Listing (Dynamic + Authored)

1. Add `Blog Listing`.
2. Set:
   - **Blog Root Path** (example: `/content/company/us/en/blog`)
   - **Maximum Items**
3. Component renders latest blog pages by date and can dynamically refresh via search servlet.

## 6) Create a New Blog Post

1. In **Sites**, create page under `/content/company/us/en/blog`.
2. Use blog template.
3. Fill:
   - Page Title
   - Description
4. Add content with `Text` + `Author Info` component.
5. (Optional) set page property `authorFragmentPath` pointing to DAM author data.

## 7) Contact Form

1. Add `Contact Form` on Contact page.
2. Configure title and success/error messages.
3. Submissions are sent to `/bin/company/contact` and persisted by service.

## 8) Publish Checklist

Before publishing:

- Verify hero image path exists in DAM.
- Check CTA links resolve (`/content/...` internal links).
- Confirm blog root path is correct.
- Preview desktop + mobile layout.
- Publish page and associated references (images/fragments).

## 9) Troubleshooting

- **Hero image black/blank**: verify image path and permissions in DAM.
- **No blogs shown**: check blog root path and ensure child pages exist.
- **Author block empty**: ensure `authorFragmentPath` points to valid DAM data node.

