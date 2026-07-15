(() => {
  const animateTargets = document.querySelectorAll(
    ".cmp-services-card, .cmp-blog-listing, .cmp-contact-form, .cmp-author-info"
  );

  animateTargets.forEach((node) => node.setAttribute("data-animate-on-scroll", "true"));

  if ("IntersectionObserver" in window) {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("is-visible");
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.2 }
    );

    animateTargets.forEach((node) => observer.observe(node));
  } else {
    animateTargets.forEach((node) => node.classList.add("is-visible"));
  }

  const blogListings = document.querySelectorAll(".cmp-blog-listing[data-dynamic='true']");
  blogListings.forEach((listing) => {
    const rootPath = listing.getAttribute("data-blog-root");
    const limit = listing.getAttribute("data-limit") || "3";
    const list = listing.querySelector(".cmp-blog-listing__items");
    if (!rootPath || !list) {
      return;
    }

    fetch(`/bin/company/blogsearch?rootPath=${encodeURIComponent(rootPath)}&limit=${encodeURIComponent(limit)}`)
      .then((response) => response.json())
      .then((payload) => {
        if (!payload || !Array.isArray(payload.results) || !payload.results.length) {
          return;
        }

        list.innerHTML = payload.results
          .map(
            (item) => `
              <li class="cmp-blog-listing__item">
                <a class="cmp-blog-listing__link" href="${item.path}">${item.title}</a>
                <p class="cmp-blog-listing__description">${item.description || ""}</p>
              </li>`
          )
          .join("");
      })
      .catch(() => {
        // Keep authored fallback list when search endpoint is unavailable.
      });
  });
})();
