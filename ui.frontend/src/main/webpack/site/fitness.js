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

  /* ---- Contact form: AJAX submit + success/error popup ---- */
  const contactForms = document.querySelectorAll(".cmp-contact-form__form");

  function showFormModal(success, message) {
    let modal = document.querySelector(".cmp-modal");
    if (!modal) {
      modal = document.createElement("div");
      modal.className = "cmp-modal";
      modal.innerHTML =
        '<div class="cmp-modal__backdrop"></div>' +
        '<div class="cmp-modal__dialog" role="dialog" aria-modal="true" aria-live="polite">' +
        '  <div class="cmp-modal__icon"></div>' +
        '  <p class="cmp-modal__message"></p>' +
        '  <button type="button" class="cmp-modal__close">Close</button>' +
        "</div>";
      document.body.appendChild(modal);
      const close = () => { modal.classList.remove("is-open"); };
      modal.querySelector(".cmp-modal__backdrop").addEventListener("click", close);
      modal.querySelector(".cmp-modal__close").addEventListener("click", close);
      document.addEventListener("keydown", (e) => { if (e.key === "Escape") { close(); } });
    }
    modal.classList.toggle("cmp-modal--success", !!success);
    modal.classList.toggle("cmp-modal--error", !success);
    modal.querySelector(".cmp-modal__message").textContent = message;
    modal.classList.add("is-open");
  }

  contactForms.forEach(function (form) {
    form.addEventListener("submit", function (e) {
      e.preventDefault();
      const btn = form.querySelector("button[type=submit]");
      const okMsg = form.getAttribute("data-success") || "Thank you! Your message has been sent.";
      const errMsg = form.getAttribute("data-error") || "Something went wrong. Please try again.";
      if (btn) { btn.classList.add("is-loading"); btn.disabled = true; }
      fetch(form.getAttribute("action"), { method: "POST", body: new FormData(form) })
        .then(function (r) { return r.json().then(function (j) { return { ok: r.ok, body: j }; }).catch(function () { return { ok: r.ok, body: {} }; }); })
        .then(function (res) {
          const success = res.body && typeof res.body.success !== "undefined" ? res.body.success : res.ok;
          const message = (res.body && res.body.message) || (success ? okMsg : errMsg);
          showFormModal(success, message);
          if (success) { form.reset(); }
        })
        .catch(function () { showFormModal(false, errMsg); })
        .finally(function () { if (btn) { btn.classList.remove("is-loading"); btn.disabled = false; } });
    });
  });

})();
