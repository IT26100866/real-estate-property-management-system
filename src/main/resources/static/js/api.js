// Small helpers shared by all EstateX admin pages

const $ = (id) => document.getElementById(id);

// Calls the Spring Boot API. Throws an Error with a readable message if it fails.
async function api(url, options = {}) {
  const res = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options
  });

  if (res.status === 204) return null;          // DELETE success (no body)

  let data = null;
  try { data = await res.json(); } catch (e) { /* no JSON body */ }

  if (!res.ok) {
    let message = (data && data.error) || "Something went wrong";
    if (data && data.fields) message += ":\n- " + Object.values(data.fields).join("\n- ");
    throw new Error(message);
  }
  return data;
}

// Stops names like <b>x</b> from being treated as HTML
function esc(text) {
  const div = document.createElement("div");
  div.textContent = text ?? "";
  return div.innerHTML;
}

// 7 -> "0007"
function pad(id) { return String(id).padStart(4, "0"); }

// "ACTIVE" -> "Active", "SUPER_ADMIN" -> "Super Admin"
function label(value) {
  if (!value) return "";
  return value.toLowerCase().split("_")
    .map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(" ");
}

// "agent" -> "/api/agents", anything else -> "/api/admins"
function endpointFor(type) {
  return String(type).toLowerCase() === "agent" ? "/api/agents" : "/api/admins";
}
