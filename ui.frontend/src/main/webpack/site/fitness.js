(() => {
  const animateTargets = document.querySelectorAll(
    "[data-animate-on-scroll], .cmp-services-card, .cmp-blog-listing, .cmp-contact-form, .cmp-author-info"
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
    const limit = listing.getAttribute("data-limit") || "12";
    const list = listing.querySelector(".cmp-blog-listing__items");
    if (!rootPath || !list) {
      return;
    }

    const input = listing.querySelector(".cmp-blog-search__input");
    const clearBtn = listing.querySelector(".cmp-blog-search__clear");
    const status = listing.querySelector(".cmp-blog-listing__status");

    const escapeHtml = (value) =>
      String(value == null ? "" : value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");

    const cardMarkup = (item) => {
      const date = escapeHtml(item.date);
      const author = escapeHtml(item.author);
      let eyebrow = "";
      if (date || author) {
        eyebrow =
          '<p class="cmp-blog-listing__eyebrow">' +
          (date ? `<span class="cmp-blog-listing__date">${date}</span>` : "") +
          (date && author ? " · " : "") +
          (author ? `<span class="cmp-blog-listing__author">${author}</span>` : "") +
          "</p>";
      }
      return `
        <li class="cmp-blog-listing__item">
          <a class="cmp-blog-listing__link" href="${escapeHtml(item.path)}">
            ${eyebrow}
            <h3 class="cmp-blog-listing__item-title">${escapeHtml(item.title)}</h3>
            <p class="cmp-blog-listing__description">${escapeHtml(item.description)}</p>
            <span class="cmp-blog-listing__more" aria-hidden="true">
              <span class="cmp-blog-listing__more-label">Read article</span>
            </span>
          </a>
        </li>`;
    };

    const replay = () => {
      listing.classList.remove("is-visible");
      void listing.offsetWidth;
      listing.classList.add("is-visible");
    };

    const setStatus = (message) => {
      if (!status) return;
      if (message) {
        status.textContent = message;
        status.hidden = false;
      } else {
        status.textContent = "";
        status.hidden = true;
      }
    };

    const render = (query) => {
      const q = (query || "").trim();
      const url =
        `/bin/company/blogsearch?rootPath=${encodeURIComponent(rootPath)}` +
        `&limit=${encodeURIComponent(limit)}` +
        (q ? `&q=${encodeURIComponent(q)}` : "");

      listing.classList.add("is-searching");

      fetch(url)
        .then((response) => response.json())
        .then((payload) => {
          const results = payload && Array.isArray(payload.results) ? payload.results : [];
          listing.classList.remove("is-searching");

          if (!results.length) {
            list.innerHTML = "";
            setStatus(q ? `No articles match "${q}".` : "No articles found.");
            return;
          }

          list.innerHTML = results.map(cardMarkup).join("");
          setStatus(
            q ? `${results.length} article${results.length === 1 ? "" : "s"} for "${q}".` : ""
          );
          replay();
        })
        .catch(() => {
          listing.classList.remove("is-searching");
          // Keep authored fallback list when search endpoint is unavailable.
        });
    };

    // Initial dynamic load (latest articles, newest first).
    render("");

    if (input) {
      let debounce;
      const onInput = () => {
        const hasValue = input.value.trim().length > 0;
        if (clearBtn) clearBtn.hidden = !hasValue;
        window.clearTimeout(debounce);
        debounce = window.setTimeout(() => render(input.value), 220);
      };
      input.addEventListener("input", onInput);
      input.addEventListener("search", onInput);

      if (clearBtn) {
        clearBtn.addEventListener("click", () => {
          input.value = "";
          clearBtn.hidden = true;
          input.focus();
          render("");
        });
      }
    }
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
        '  <h3 class="cmp-modal__heading"></h3>' +
        '  <p class="cmp-modal__message"></p>' +
        '  <button type="button" class="cmp-modal__close">Close</button>' +
        "</div>";
      document.body.appendChild(modal);
      const close = () => { modal.classList.remove("is-open"); };
      modal.querySelector(".cmp-modal__backdrop").addEventListener("click", close);
      modal.querySelector(".cmp-modal__close").addEventListener("click", close);
      document.addEventListener("keydown", (e) => { if (e.key === "Escape") { close(); } });
    }
    const successIcon =
      '<svg class="cmp-modal__mark" viewBox="0 0 52 52" aria-hidden="true">' +
      '<circle class="cmp-modal__mark-circle" cx="26" cy="26" r="24"/>' +
      '<path class="cmp-modal__mark-path" d="M15 27 l7 7 l15 -16"/></svg>';
    const errorIcon =
      '<svg class="cmp-modal__mark" viewBox="0 0 52 52" aria-hidden="true">' +
      '<circle class="cmp-modal__mark-circle" cx="26" cy="26" r="24"/>' +
      '<path class="cmp-modal__mark-path" d="M18 18 L34 34 M34 18 L18 34"/></svg>';
    modal.classList.toggle("cmp-modal--success", !!success);
    modal.classList.toggle("cmp-modal--error", !success);
    modal.querySelector(".cmp-modal__icon").innerHTML = success ? successIcon : errorIcon;
    modal.querySelector(".cmp-modal__heading").textContent = success ? "Message sent!" : "Something went wrong";
    modal.querySelector(".cmp-modal__message").textContent = message;
    modal.classList.remove("is-open");
    void modal.offsetWidth;
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


  /* ---- Header: scroll state + scroll progress (Apple-style) ---- */
  const siteHeader = document.querySelector(".cmp-experiencefragment--header");
  if (siteHeader) {
    const onHeaderScroll = () => {
      const y = window.scrollY || document.documentElement.scrollTop;
      if (y > 16) {
        siteHeader.classList.add("is-scrolled");
      } else {
        siteHeader.classList.remove("is-scrolled");
      }
      const doc = document.documentElement;
      const max = (doc.scrollHeight - doc.clientHeight) || 1;
      const pct = Math.min(100, Math.max(0, (y / max) * 100));
      siteHeader.style.setProperty("--scroll-progress", pct + "%");
    };
    window.addEventListener("scroll", onHeaderScroll, { passive: true });
    window.addEventListener("resize", onHeaderScroll, { passive: true });
    onHeaderScroll();
  }

})();
