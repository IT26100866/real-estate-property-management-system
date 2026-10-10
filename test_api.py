import urllib.request
import urllib.error
import json
import time

API_URL = "http://localhost:8081/api"

def request(endpoint, method, payload=None, token=None):
    url = f"{API_URL}{endpoint}"
    req = urllib.request.Request(url, method=method)
    if payload:
        req.add_header('Content-Type', 'application/json')
        data = json.dumps(payload).encode('utf-8')
    else:
        data = None
    if token:
        req.add_header('Authorization', f'Bearer {token}')
    try:
        with urllib.request.urlopen(req, data=data) as response:
            return response.status, json.loads(response.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        body = e.read().decode('utf-8')
        try:
            return e.code, json.loads(body)
        except:
            return e.code, body

print("Waiting for server to start...")
time.sleep(3)

print("\n--- 1. Testing BCrypt Logins ---")
for u, p in [('admin@admin', 'adminPass_master123'), ('chaminda@agent', 'agentPass_chaminda123'), ('dilshan@user', 'userPass_dilshan123')]:
    status, res = request('/auth/login', 'POST', {'username': u, 'password': p})
    print(f"Login {u}: Status {status}, Success={res.get('success', False)}")
    if u == 'admin@admin': admin_token = res.get('token')
    if u == 'chaminda@agent': agent_token = res.get('token')
    if u == 'dilshan@user': customer_token = res.get('token')

print("\n--- 2. Testing Invalid Password ---")
status, res = request('/auth/login', 'POST', {'username': 'admin@admin', 'password': 'wrongpassword'})
print(f"Login admin@admin (wrong pass): Status {status}, Success={res.get('success', False)}, Msg={res.get('message', '')}")

print("\n--- 3. Testing Legacy SHA-256 Fallback & Upgrade ---")
status, res = request('/auth/login', 'POST', {'username': 'gayan@user', 'password': 'gayan123'})
print(f"Login gayan@user: Status {status}, Success={res.get('success', False)}")
gayan_token = res.get('token')
# Login again to test the upgraded BCrypt hash
if gayan_token:
    status, res = request('/auth/login', 'POST', {'username': 'gayan@user', 'password': 'gayan123'})
    print(f"Login gayan@user (2nd time, should be BCrypt): Status {status}, Success={res.get('success', False)}")

print("\n--- 4. Testing Authorization Rules ---")
for t, name in [(admin_token, 'Admin'), (agent_token, 'Agent'), (customer_token, 'Customer')]:
    if t:
        status, res = request('/users', 'GET', token=t)
        print(f"GET /users as {name}: Status {status}")

print("\n--- 5. Testing Change Password ---")
if admin_token:
    # Change to a new password
    status, res = request('/users/me/password', 'PUT', {'currentPassword': 'adminPass_master123', 'newPassword': 'newAdminPassword123'}, admin_token)
    print(f"Change Password (Admin, valid current): Status {status}")
    # Change back with the new password
    status, res = request('/users/me/password', 'PUT', {'currentPassword': 'newAdminPassword123', 'newPassword': 'adminPass_master123'}, admin_token)
    print(f"Change Password (Admin, revert): Status {status}")
    # Attempt change with wrong current password
    status, res = request('/users/me/password', 'PUT', {'currentPassword': 'wrongpassword', 'newPassword': 'somePassword'}, admin_token)
    print(f"Change Password (Admin, invalid current): Status {status}")

