import re

files = [
    ('user/profile.html', 'user/dashboard.html', 'Customer Portal > Dashboard', 'Customer Dashboard'),
    ('agent/profile.html', 'agent/dashboard.html', 'Agent Portal > Dashboard', 'Agent Dashboard'),
    ('admin/users.html', 'admin/dashboard.html', 'Admin Portal > Dashboard', 'Admin Dashboard')
]

for src, dest, breadcrumb, title in files:
    with open(src, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Remove 'active' from the currently active item
    content = re.sub(r'class="sidebar-item active"', 'class="sidebar-item"', content)
    
    # Add 'active' to the Dashboard item
    content = re.sub(r'<a href="#" class="sidebar-item">(\s*<svg[^>]*>.*?</svg>\s*Dashboard\s*)</a>', 
                     r'<a href="dashboard.html" class="sidebar-item active">\1</a>', content, flags=re.DOTALL)
    
    # Also fix the topbar title and breadcrumb
    content = re.sub(r'<div class="topbar-breadcrumb">.*?</div>', f'<div class="topbar-breadcrumb">{breadcrumb}</div>', content)
    content = re.sub(r'<h1 class="topbar-title">.*?</h1>', f'<h1 class="topbar-title">{title}</h1>', content)
    
    # Replace the main content
    pattern = re.compile(r'(<main class="dashboard-content">).*?(</main>)', re.DOTALL)
    replacement = r'\1\n        <div class="card" style="text-align: center; padding: 48px;">\n          <h3 style="color: var(--text-muted);">Dashboard Placeholder</h3>\n          <p>This page is out of scope. Please navigate to the Profile or User Management pages using the sidebar.</p>\n        </div>\n      \2'
    content = pattern.sub(replacement, content)
    
    with open(dest, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'Created {dest}')
