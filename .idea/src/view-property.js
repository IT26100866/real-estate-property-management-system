"use strict";

document.addEventListener("DOMContentLoaded", function () {

    /* ==================================
       PROPERTY DATA
    ================================== */

    const property = {
        id: 101,
        name: "Modern Apartment",
        category: "Apartment",
        location: "Colombo, Sri Lanka",
        price: 25000000,
        bedrooms: 2,
        bathrooms: 2,
        area: 1200,
        status: "Available",

        description:
            "A beautiful modern apartment located in the heart of Colombo. " +
            "Close to shopping malls, schools and public transport. " +
            "Perfect for small families or professionals.",

        agentName: "John Perera",
        agentPhone: "071 0000000",
        agentEmail: "agent@example.com"
    };


    /* ==================================
       GET HTML ELEMENTS
    ================================== */

    const bookButton = document.getElementById("bookButton");
    const inquiryButton = document.getElementById("inquiryButton");
    const reviewButton = document.getElementById("reviewButton");

    const backButton = document.getElementById("backButton");

    const favoriteButton = document.getElementById("favoriteButton");

    const modal = document.getElementById("actionModal");
    const modalTitle = document.getElementById("modalTitle");
    const modalSubtitle = document.getElementById("modalSubtitle");
    const modalIcon = document.getElementById("modalIcon");

    const closeModalButton = document.getElementById("closeModal");

    const actionForm = document.getElementById("actionForm");
    const formFields = document.getElementById("formFields");

    const submitButton = document.getElementById("submitButton");
    const formMessage = document.getElementById("formMessage");


    let currentAction = "";
    let previousFocus = null;


    /* ==================================
       DISPLAY PROPERTY INFORMATION
    ================================== */

    function displayProperty() {

        document.getElementById("propertyName").textContent =
            property.name;

        document.getElementById("propertyCategory").textContent =
            property.category.toUpperCase();

        document.getElementById("propertyLocation").textContent =
            property.location;

        document.getElementById("propertyPrice").textContent =
            "Rs. " + property.price.toLocaleString("en-LK");

        document.getElementById("bedroomCount").textContent =
            property.bedrooms;

        document.getElementById("bathroomCount").textContent =
            property.bathrooms;

        document.getElementById("propertyArea").textContent =
            Number(property.area).toLocaleString("en-LK");

        document.getElementById("propertyDescription").textContent =
            property.description;

        document.getElementById("agentName").textContent =
            property.agentName;

        document.getElementById("agentPhone").textContent =
            property.agentPhone;

        document.getElementById("agentEmail").textContent =
            property.agentEmail;

        const statusBadge = document.getElementById("propertyStatus");

        statusBadge.replaceChildren();

        const statusIcon = document.createElement("i");

        statusIcon.className = property.status === "Available"
            ? "fa-solid fa-circle-check"
            : "fa-solid fa-circle-exclamation";

        statusBadge.appendChild(statusIcon);

        statusBadge.appendChild(
            document.createTextNode(" " + property.status)
        );

    }


    /* ==================================
       PROPERTY IMAGE GALLERY
    ================================== */

    const galleryImages = [
        {
            full: "https://images.unsplash.com/photo-1460317442991-0ec209397118?auto=format&fit=crop&w=1200&q=85",
            alt: "Modern apartment building"
        },
        {
            full: "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=1200&q=85",
            alt: "Modern living room"
        },
        {
            full: "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1200&q=85",
            alt: "Modern property interior"
        },
        {
            full: "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?auto=format&fit=crop&w=1200&q=85",
            alt: "Modern bedroom"
        }
    ];

    let currentImageIndex = 0;

    const mainImage = document.getElementById("mainPropertyImage");

    const thumbnails = document.querySelectorAll(".thumbnail");


    function showImage(index) {

        currentImageIndex =
            (index + galleryImages.length) % galleryImages.length;

        mainImage.style.opacity = "0.45";

        const newImage = galleryImages[currentImageIndex];

        mainImage.onload = function () {
            mainImage.style.opacity = "1";
        };

        mainImage.onerror = function () {
            mainImage.style.opacity = "1";
        };

        mainImage.src = newImage.full;
        mainImage.alt = newImage.alt;

        thumbnails.forEach(function (thumbnail, i) {

            thumbnail.classList.toggle(
                "active",
                i === currentImageIndex
            );

        });

    }


    thumbnails.forEach(function (thumbnail) {

        thumbnail.addEventListener("click", function () {

            const index = Number(thumbnail.dataset.image);

            if (Number.isInteger(index)) {
                showImage(index);
            }

        });

    });


    document.getElementById("prevImage").addEventListener(
        "click",
        function () {
            showImage(currentImageIndex - 1);
        }
    );


    document.getElementById("nextImage").addEventListener(
        "click",
        function () {
            showImage(currentImageIndex + 1);
        }
    );


    /* ==================================
       FAVORITE BUTTON
    ================================== */

    favoriteButton.addEventListener("click", function () {

        const isFavorite =
            favoriteButton.getAttribute("aria-pressed") === "true";

        const newState = !isFavorite;

        favoriteButton.setAttribute(
            "aria-pressed",
            String(newState)
        );

        favoriteButton.classList.toggle(
            "is-favorite",
            newState
        );

        const icon = favoriteButton.querySelector("i");

        icon.className = newState
            ? "fa-solid fa-heart"
            : "fa-regular fa-heart";

        favoriteButton.setAttribute(
            "aria-label",
            newState
                ? "Remove from favorites"
                : "Add to favorites"
        );

        showToast(
            newState
                ? "Property added to favorites."
                : "Property removed from favorites."
        );

    });


    /* ==================================
       TOAST MESSAGE
    ================================== */

    function showToast(message) {

        let toast = document.getElementById("pageToast");

        if (!toast) {

            toast = document.createElement("div");

            toast.id = "pageToast";

            toast.style.cssText = `
                position: fixed;
                right: 24px;
                bottom: 24px;
                z-index: 3000;
                max-width: calc(100% - 48px);
                padding: 14px 20px;
                border-radius: 9px;
                background: #101a2b;
                color: white;
                font-size: 13px;
                box-shadow: 0 8px 25px rgba(0,0,0,0.15);
            `;

            document.body.appendChild(toast);

        }

        toast.textContent = message;

        toast.hidden = false;

        clearTimeout(toast.hideTimer);

        toast.hideTimer = setTimeout(function () {
            toast.hidden = true;
        }, 2800);

    }


    /* ==================================
       CLOSE MODAL (වෙනස් කළ කොටස: බටන් 3 ක්‍රියා විරහිත කර ඇත)
    ================================== */

    // Book Viewing, Inquiry, සහ Review බටන් වල ක්‍රියාකාරීත්වය (event listeners)
    // සම්පූර්ණයෙන්ම ඉවත් කර ඇති බැවින්, දැන් ඒවා පෙනී සිටියද ක්ලික් කළම කිසිවක් සිදු නොවේ.


    closeModalButton.addEventListener("click", closeModal);


    // Close when clicking outside the popup

    modal.addEventListener("click", function (event) {

        if (event.target === modal) {
            closeModal();
        }

    });


    // Close with Escape key

    document.addEventListener("keydown", function (event) {

        if (event.key === "Escape" && !modal.hidden) {
            closeModal();
        }

    });


    /* ==================================
       BACK BUTTON
    ================================== */

    backButton.addEventListener("click", function () {

        if (
            document.referrer &&
            new URL(document.referrer).origin === window.location.origin
        ) {

            window.history.back();

        } else {

            window.location.href = "index.html";

        }

    });


    /* ==================================
       INITIALIZE PAGE
    ================================== */

    displayProperty();

});