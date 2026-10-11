"use strict";

/* ================= SAMPLE PROPERTY DATA ================= */

/*
   These are demonstration properties.
   Real project data should eventually come from your
   Spring Boot backend and MySQL database.
*/

const properties = [
    {
        id: 101,
        name: "Modern Apartment",
        price: 25000000,
        location: "Colombo",
        type: "Apartment",
        description:
            "A modern apartment located close to schools, shops and public transport.",
        features: [
            "2 Bedrooms",
            "2 Bathrooms",
            "Parking Available"
        ],
        status: "Available",
        image:
            "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=800&q=80"
    },

    {
        id: 102,
        name: "Luxury Family House",
        price: 45000000,
        location: "Nugegoda",
        type: "House",
        description:
            "A spacious family house with a comfortable living area and garden.",
        features: [
            "4 Bedrooms",
            "2 Bathrooms",
            "Garden",
            "Parking Available"
        ],
        status: "Available",
        image:
            "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?auto=format&fit=crop&w=800&q=80"
    },

    {
        id: 103,
        name: "Residential Land",
        price: 12000000,
        location: "Gampaha",
        type: "Land",
        description:
            "A residential land plot suitable for building a new home.",
        features: [
            "Residential Area",
            "Road Access",
            "Electricity Nearby"
        ],
        status: "Available",
        image:
            "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80"
    },

    {
        id: 104,
        name: "Commercial Building",
        price: 65000000,
        location: "Kandy",
        type: "Commercial",
        description:
            "A commercial property suitable for offices or business operations.",
        features: [
            "Multiple Floors",
            "Roadside Location",
            "Parking Area"
        ],
        status: "Available",
        image:
            "https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=800&q=80"
    }
];


/* ================= ELEMENT REFERENCES ================= */

const propertyGrid = document.getElementById("propertyGrid");
const emptyMessage = document.getElementById("emptyMessage");

const searchForm = document.getElementById("searchForm");
const searchId = document.getElementById("searchId");
const searchLocation = document.getElementById("searchLocation");
const searchType = document.getElementById("searchType");

const resetSearch = document.getElementById("resetSearch");
const showAllButton = document.getElementById("showAllButton");

const propertyModal = document.getElementById("propertyModal");
const modalDetails = document.getElementById("modalDetails");
const closeModal = document.getElementById("closeModal");

const menuButton = document.getElementById("menuButton");
const navLinks = document.getElementById("navLinks");


/* ================= HELPER FUNCTIONS ================= */

function escapeHTML(value) {
    return String(value).replace(/[&<>"']/g, function (character) {
        const entities = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        };

        return entities[character];
    });
}


function formatPrice(price) {
    return "Rs. " + Number(price).toLocaleString("en-LK");
}


/* ================= DISPLAY PROPERTIES ================= */

function displayProperties(list) {

    propertyGrid.innerHTML = "";

    if (list.length === 0) {

        emptyMessage.hidden = false;

        return;
    }

    emptyMessage.hidden = true;

    list.forEach(function (property) {

        const card = document.createElement("article");

        card.className = "property-card";

        const safeName = escapeHTML(property.name);
        const safeLocation = escapeHTML(property.location);
        const safeType = escapeHTML(property.type);
        const safeStatus = escapeHTML(property.status);
        const safeImage = escapeHTML(property.image);

        const statusClass =
            property.status.toLowerCase() === "sold"
                ? "sold"
                : "";

        card.innerHTML = `
            <img
                class="property-image"
                src="${safeImage}"
                alt="${safeName}"
                loading="lazy"
            >

            <div class="property-info">

                <span class="property-id">
                    Property ID: ${property.id}
                </span>

                <h3>${safeName}</h3>

                <p class="property-location">
                    📍 ${safeLocation}
                </p>

                <p class="property-price">
                    ${formatPrice(property.price)}
                </p>

                <span class="property-type">
                    ${safeType}
                </span>

                <span class="property-status ${statusClass}">
                    ${safeStatus}
                </span>

                <div class="property-actions">

                    <button
                        type="button"
                        class="view-button"
                        data-property-id="${property.id}"
                    >
                        View Details
                    </button>

                </div>

            </div>
        `;

        propertyGrid.appendChild(card);

    });
}


/* ================= SEARCH PROPERTIES ================= */

searchForm.addEventListener("submit", function (event) {

    event.preventDefault();

    const idValue = searchId.value.trim();

    const locationValue =
        searchLocation.value.trim().toLowerCase();

    const typeValue = searchType.value;

    const filteredProperties = properties.filter(function (property) {

        const matchesId =
            idValue === "" ||
            property.id === Number(idValue);

        const matchesLocation =
            locationValue === "" ||
            property.location.toLowerCase().includes(locationValue);

        const matchesType =
            typeValue === "" ||
            property.type === typeValue;

        return matchesId && matchesLocation && matchesType;

    });

    displayProperties(filteredProperties);

    document.getElementById("featured").scrollIntoView({
        behavior: "smooth"
    });

});


/* ================= RESET SEARCH ================= */

resetSearch.addEventListener("click", function () {

    searchForm.reset();

    displayProperties(properties);

});


/* ================= SHOW ALL PROPERTIES ================= */

showAllButton.addEventListener("click", function () {

    searchForm.reset();

    displayProperties(properties);

});


/* ================= CATEGORY FILTER ================= */

document.querySelectorAll(".category-card").forEach(function (button) {

    button.addEventListener("click", function () {

        const category = button.dataset.category;

        searchForm.reset();

        searchType.value = category;

        const filteredProperties = properties.filter(function (property) {

            return property.type === category;

        });

        displayProperties(filteredProperties);

        document.getElementById("featured").scrollIntoView({
            behavior: "smooth"
        });

    });

});


/* ================= VIEW PROPERTY DETAILS ================= */

propertyGrid.addEventListener("click", function (event) {

    const button = event.target.closest("[data-property-id]");

    if (!button) {
        return;
    }

    const propertyId = Number(button.dataset.propertyId);

    const property = properties.find(function (item) {

        return item.id === propertyId;

    });

    if (!property) {
        return;
    }

    const safeName = escapeHTML(property.name);
    const safeLocation = escapeHTML(property.location);
    const safeType = escapeHTML(property.type);
    const safeStatus = escapeHTML(property.status);
    const safeDescription = escapeHTML(property.description);
    const safeImage = escapeHTML(property.image);

    const featuresHTML = property.features.map(function (feature) {

        return `<li>${escapeHTML(feature)}</li>`;

    }).join("");

    modalDetails.innerHTML = `

        <img
            class="modal-property-image"
            src="${safeImage}"
            alt="${safeName}"
        >

        <h2 id="modalTitle">${safeName}</h2>

        <p><strong>Property ID:</strong> ${property.id}</p>

        <p><strong>Location:</strong> ${safeLocation}</p>

        <p><strong>Type:</strong> ${safeType}</p>

        <p><strong>Price:</strong> ${formatPrice(property.price)}</p>

        <p><strong>Status:</strong> ${safeStatus}</p>

        <p><strong>Description:</strong> ${safeDescription}</p>

        <p><strong>Features:</strong></p>

        <ul>${featuresHTML}</ul>

    `;

    propertyModal.hidden = false;

    closeModal.focus();

});


/* ================= CLOSE DETAILS MODAL ================= */

closeModal.addEventListener("click", function () {

    propertyModal.hidden = true;

});


propertyModal.addEventListener("click", function (event) {

    if (event.target === propertyModal) {

        propertyModal.hidden = true;

    }

});


document.addEventListener("keydown", function (event) {

    if (event.key === "Escape") {

        propertyModal.hidden = true;

    }

});


/* ================= MOBILE NAVIGATION ================= */

menuButton.addEventListener("click", function () {

    const isOpen = navLinks.classList.toggle("active");

    menuButton.setAttribute("aria-expanded", String(isOpen));

});


navLinks.querySelectorAll("a").forEach(function (link) {

    link.addEventListener("click", function () {

        navLinks.classList.remove("active");

        menuButton.setAttribute("aria-expanded", "false");

    });

});


/* ================= CURRENT YEAR ================= */

document.getElementById("currentYear").textContent =
    new Date().getFullYear();


/* ================= INITIAL DISPLAY ================= */

displayProperties(properties);