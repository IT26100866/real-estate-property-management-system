/**
 * EstateX – Real Estate Property Listings Portal
 * Core Client-Side Layout & UI Interactive Behaviors
 */

document.addEventListener('DOMContentLoaded', () => {
  initModals();
  initDropdowns();
  initToast();
  initAuth();
});

// Modal System
function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
  }
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.remove('active');
    document.body.style.overflow = '';
  }
}

function initModals() {
  // Close modals on overlay backdrop click
  document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', (e) => {
      if (e.target === overlay) {
        overlay.classList.remove('active');
        document.body.style.overflow = '';
      }
    });
  });

  // Close modals on Escape key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      document.querySelectorAll('.modal-overlay.active').forEach(modal => {
        modal.classList.remove('active');
      });
      document.body.style.overflow = '';
    }
  });

  // Attach data-modal-close listeners
  document.querySelectorAll('[data-modal-close]').forEach(btn => {
    btn.addEventListener('click', () => {
      const modalId = btn.getAttribute('data-modal-close');
      if (modalId) {
        closeModal(modalId);
      } else {
        const parentModal = btn.closest('.modal-overlay');
        if (parentModal) parentModal.classList.remove('active');
        document.body.style.overflow = '';
      }
    });
  });

  // Attach data-modal-target listeners
  document.querySelectorAll('[data-modal-target]').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      const targetId = btn.getAttribute('data-modal-target');
      if (targetId) openModal(targetId);
    });
  });
}

// Dropdowns
function initDropdowns() {
  document.addEventListener('click', (e) => {
    const isDropdownButton = e.target.closest('.dropdown-trigger');
    const currentDropdown = isDropdownButton ? isDropdownButton.nextElementSibling : null;

    document.querySelectorAll('.dropdown-menu.active').forEach(menu => {
      if (menu !== currentDropdown) {
        menu.classList.remove('active');
      }
    });

    if (currentDropdown) {
      currentDropdown.classList.toggle('active');
    }
  });
}

// Simple Toast Notification for UI feedback
function showToast(message, type = 'info') {
  let toastContainer = document.getElementById('toast-container');
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toast-container';
    toastContainer.style.position = 'fixed';
    toastContainer.style.bottom = '24px';
    toastContainer.style.right = '24px';
    toastContainer.style.zIndex = '9999';
    toastContainer.style.display = 'flex';
    toastContainer.style.flexDirection = 'column';
    toastContainer.style.gap = '8px';
    document.body.appendChild(toastContainer);
  }

  const toast = document.createElement('div');
  toast.style.background = '#0E1627';
  toast.style.color = '#FFEDD8';
  toast.style.padding = '12px 20px';
  toast.style.borderRadius = '8px';
  toast.style.fontSize = '0.9rem';
  toast.style.fontWeight = '500';
  toast.style.boxShadow = '0 6px 18px rgba(14,22,39,0.2)';
  toast.style.borderLeft = type === 'success' ? '4px solid #10B981' : type === 'danger' ? '4px solid #EF4444' : '4px solid #CFAB8D';
  toast.textContent = message;

  toastContainer.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

function initToast() {
  // Demo action buttons can trigger prototype notifications
  document.querySelectorAll('[data-demo-toast]').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const msg = btn.getAttribute('data-demo-toast');
      const type = btn.getAttribute('data-demo-type') || 'info';
      showToast(msg, type);
    });
  });
}

// Authentication & Role-Based Navigation Sync
function getAuthUser() {
  try {
    const raw = sessionStorage.getItem('estatex_auth_user');
    return raw ? JSON.parse(raw) : null;
  } catch (e) {
    return null;
  }
}

function logoutEstateX() {
  sessionStorage.removeItem('estatex_auth_user');
  sessionStorage.removeItem('estatex_jwt_token');
  showToast('You have been signed out successfully.', 'info');
  const path = window.location.pathname;
  const isSub = path.includes('/public/') || path.includes('/user/') || path.includes('/agent/') || path.includes('/admin/');
  const target = isSub ? '../public/login.html' : 'public/login.html';
  setTimeout(() => {
    window.location.href = target;
  }, 400);
}

async function deleteAccount() {
  const user = getAuthUser();
  const token = sessionStorage.getItem('estatex_jwt_token');
  if (!user || !token) return;

  const btn = document.querySelector('#deleteAccountModal .btn-danger');
  if (btn) btn.disabled = true;

  try {
    const API_BASE_URL = window.location.port === '8081' ? '' : 'http://localhost:8081';
    const response = await fetch(`${API_BASE_URL}/api/users/${user.id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      showToast('Account successfully deleted.', 'danger');
      closeModal('deleteAccountModal');
      setTimeout(() => {
        logoutEstateX();
      }, 1000);
    } else {
      const errorData = await response.json().catch(() => ({}));
      showToast(errorData.message || 'Failed to delete account (Requires Admin).', 'danger');
      if (btn) btn.disabled = false;
    }
  } catch (err) {
    console.warn('Backend unavailable', err);
    showToast('Failed to connect to backend.', 'danger');
    if (btn) btn.disabled = false;
  }
}

async function handleChangePassword(event) {
  event.preventDefault();
  const user = getAuthUser();
  if (!user) return;

  const currentPassword = document.getElementById('currentPassword').value;
  const newPassword = document.getElementById('newPassword').value;
  const confirmPassword = document.getElementById('confirmPassword').value;

  if (newPassword !== confirmPassword) {
    showToast('New passwords do not match!', 'danger');
    return;
  }

  const submitBtn = event.target.querySelector('button[type="submit"]');
  const originalText = submitBtn.innerHTML;
  submitBtn.disabled = true;
  submitBtn.innerHTML = 'Updating...';

  try {
    const API_BASE_URL = window.location.port === '8081' ? '' : 'http://localhost:8081';
    const token = sessionStorage.getItem('estatex_jwt_token');
    const response = await fetch(`${API_BASE_URL}/api/users/me/password`, {
      method: 'PUT',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ currentPassword, newPassword })
    });

    if (response.ok) {
      showToast('Password changed successfully! Please log in again.', 'success');
      setTimeout(() => {
        logoutEstateX();
      }, 2000);
    } else {
      const errorData = await response.json().catch(() => ({}));
      showToast(errorData.message || 'Failed to change password.', 'danger');
      submitBtn.disabled = false;
      submitBtn.innerHTML = originalText;
    }
  } catch (err) {
    showToast('Unable to connect to the server.', 'danger');
    submitBtn.disabled = false;
    submitBtn.innerHTML = originalText;
  }
}

function getProfilePhotoUrl(user) {
  if (user && user.profilePhotoUrl) {
    return user.profilePhotoUrl;
  }
  return "data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHZpZXdCb3g9JzAgMCAyNCAyNCcgZmlsbD0nI2NiZDVlMSc+PHBhdGggZD0nTTEyIDEyYzIuMjEgMCA0LTEuNzkgNC00cy0xLjc5LTQtNC00LTQgMS43OS00IDQgMS43OSA0IDQgNHptMCAyYy0yLjY3IDAtOCAxLjM0LTggNHYyaDE2di0yYzAtMi42Ni01LjMzLTQtOC00eicvPjwvc3ZnPg==";
}

function handlePhotoUpload(event) {
  const file = event.target.files[0];
  if (!file) return;

  // Validate photo size (e.g. max 2MB) to prevent sessionStorage quota exceeded errors
  if (file.size > 2 * 1024 * 1024) {
    showToast('Photo is too large. Please select an image under 2MB.', 'danger');
    event.target.value = '';
    return;
  }

  const reader = new FileReader();
  reader.onload = function(e) {
    const dataUrl = e.target.result;
    
    // Update active user session
    const userStr = sessionStorage.getItem('estatex_auth_user');
    if (userStr) {
      const user = JSON.parse(userStr);
      user.profilePhotoUrl = dataUrl;
      sessionStorage.setItem('estatex_auth_user', JSON.stringify(user));
    }
    
    // Update UI elements immediately
    document.querySelectorAll('.user-avatar-circle img, #profilePreviewImage').forEach(img => {
      img.src = dataUrl;
    });
    
    showToast('Profile photo updated locally!', 'success');
  };
  reader.readAsDataURL(file);
}

function initAuth() {
  // Attach signout handler to all sidebar and footer logout links
  document.querySelectorAll('a[href*="login.html"]').forEach(link => {
    if (link.textContent.toLowerCase().includes('logout') || link.classList.contains('sidebar-item-logout-user')) {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        logoutEstateX();
      });
    }
  });

  const user = getAuthUser();
  const path = window.location.pathname;
  const isSub = path.includes('/public/') || path.includes('/user/') || path.includes('/agent/') || path.includes('/admin/');
  const prefix = isSub ? '../' : '';

  // If user is logged in, update public navigation header & dropdown menu
  if (user) {
    const role = (user.role || '').toLowerCase();
    let dashboardPath = prefix + 'user/dashboard.html';
    let profilePath = prefix + 'user/profile.html';
    let roleBadge = '👤 Customer';

    if (role.includes('admin')) {
      dashboardPath = prefix + 'admin/dashboard.html';
      profilePath = prefix + 'admin/profile.html';
      roleBadge = '⚙️ Administrator';
    } else if (role.includes('agent')) {
      dashboardPath = prefix + 'agent/dashboard.html';
      profilePath = prefix + 'agent/profile.html';
      roleBadge = '🏢 Real Estate Agent';
    }

    // Update public profile dropdown
    document.querySelectorAll('.profile-dropdown-menu').forEach(menu => {
      menu.innerHTML = `
        <div class="profile-dropdown-header">
          <div class="user-name">${user.name || 'User'}</div>
          <div class="user-role">${roleBadge}</div>
        </div>
        <a href="${dashboardPath}">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
          My Dashboard
        </a>
        <a href="${profilePath}">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
          Profile Settings
        </a>
        <div class="profile-dropdown-divider" style="margin: 6px 0; border-top: 1px solid var(--border-light);"></div>
        <a href="javascript:void(0)" onclick="logoutEstateX()" style="color: #EF4444;">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
          Sign Out
        </a>
      `;
    });

    // Update public nav actions buttons
    document.querySelectorAll('.public-navbar .nav-actions').forEach(actions => {
      const loginLink = actions.querySelector('a[href*="login.html"]');
      const registerLink = actions.querySelector('a[href*="register.html"]');
      if (loginLink && registerLink) {
        loginLink.href = dashboardPath;
        loginLink.textContent = 'Dashboard';
        registerLink.textContent = 'Sign Out';
        registerLink.removeAttribute('href');
        registerLink.style.cursor = 'pointer';
        registerLink.onclick = logoutEstateX;
      }
    });

    // Update sidebar / header user info on dashboard pages if element exists
    document.querySelectorAll('.user-info-name').forEach(el => {
      el.textContent = user.name;
    });

    // Dynamically update dashboard topbar titles and meta names
    document.querySelectorAll('.user-meta-name').forEach(el => {
      el.textContent = user.name || user.username;
    });

    document.querySelectorAll('.topbar-title').forEach(el => {
      if (el.textContent.includes('Welcome back')) {
        const firstName = (user.name || user.username).split(' ')[0];
        el.textContent = `Welcome back, ${firstName}!`;
      }
    });

    // Dynamically update profile photos globally
    document.querySelectorAll('.user-avatar-circle img, .profile-dropdown-header img, #profilePreviewImage').forEach(img => {
      img.src = getProfilePhotoUrl(user);
    });
  }
}
