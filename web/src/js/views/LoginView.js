// Step 3: Login Screen
import { store } from '../store.js';
import { showToast } from '../components/Toast.js';

export function renderLoginView() {
  const container = document.createElement('div');
  container.className = 'fade-in';
  container.style.cssText = `
    min-height: 100vh;
    position: relative;
    display: flex;
    flex-direction: column;
  `;

  container.innerHTML = `
    <div class="building-backdrop">
      <div class="building-overlay"></div>
    </div>

    <div class="backdrop-content" style="flex: 1; display: flex; flex-direction: column; max-width: 480px; width: 100%; margin: 0 auto; padding: 16px 20px;">
      <!-- Top Bar with Back Arrow -->
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
        <a href="#/landing" class="btn-icon" aria-label="Go Back">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ffffff" stroke-width="2.5">
            <line x1="19" y1="12" x2="5" y2="12"></line>
            <polyline points="12 19 5 12 12 5"></polyline>
          </svg>
        </a>
      </div>

      <!-- Identity Header -->
      <div style="display: flex; flex-direction: column; align-items: center; text-align: center; margin-bottom: 20px;">
        <div style="width: 88px; height: 88px; border-radius: 50%; background: rgba(12, 36, 59, 0.85); display: flex; align-items: center; justify-content: center; margin-bottom: 12px; border: 1.5px solid #1E4D7A;">
          <img src="./icons/logo.png" alt="Kharsia Lobby Emblem" width="76" height="76" class="kharsia-logo" />
        </div>
        <h2 style="color: #F1B748; font-size: 17px; font-weight: 700; line-height: 1.3;">
          संयुक्त चालक एवं परिचालक लॉबी खरसिया
        </h2>
        <span style="color: #90A4AE; font-size: 12px; font-weight: 600; letter-spacing: 0.5px;">
          SECR BILASPUR DIVISION
        </span>
      </div>

      <!-- Login Form Card -->
      <div class="card" style="margin-bottom: 20px;">
        <form id="login-form">
          <div class="form-group">
            <label class="form-label" for="login-username">USERNAME / CREW ID</label>
            <input 
              type="text" 
              id="login-username" 
              class="form-input" 
              placeholder="e.g. KHS1001 or KHS1234" 
              autocomplete="username" 
              required 
            />
          </div>

          <div class="form-group">
            <label class="form-label" for="login-password">PASSWORD</label>
            <div style="position: relative;">
              <input 
                type="password" 
                id="login-password" 
                class="form-input" 
                placeholder="Enter your password" 
                autocomplete="current-password" 
                required 
                style="padding-right: 44px;"
              />
              <button type="button" id="toggle-password" class="btn-icon" style="position: absolute; right: 4px; top: 50%; transform: translateY(-50%); color: #64B5F6;" title="Toggle Password">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                  <circle cx="12" cy="12" r="3"></circle>
                </svg>
              </button>
            </div>
          </div>

          <div id="login-error" style="display: none; background: rgba(231, 76, 60, 0.15); border: 1px solid #E74C3C; color: #FF8A80; font-size: 12px; font-weight: 600; padding: 8px 12px; border-radius: var(--radius-sm); margin-bottom: 16px;"></div>

          <button type="submit" id="btn-submit-login" class="btn btn-primary btn-full">
            <span>Sign In</span>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="5" y1="12" x2="19" y2="12"></line>
              <polyline points="12 19 5 12 12 19"></polyline>
            </svg>
          </button>
        </form>
      </div>

      <!-- Security Notice Card -->
      <div class="card" style="padding: 14px 18px; display: flex; align-items: center; gap: 12px; background: rgba(11, 25, 42, 0.8);">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#F1B748" stroke-width="2" style="flex-shrink: 0;">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
        </svg>
        <div style="font-size: 11px; color: #90A4AE; line-height: 1.3;">
          <strong style="color: #FFFFFF;">Authorized Railway Personnel Only.</strong> Access is logged and monitored per Indian Railways IT Security guidelines.
        </div>
      </div>
    </div>
  `;

  const form = container.querySelector('#login-form');
  const usernameInput = container.querySelector('#login-username');
  const passwordInput = container.querySelector('#login-password');
  const togglePassBtn = container.querySelector('#toggle-password');
  const errorBox = container.querySelector('#login-error');
  const submitBtn = container.querySelector('#btn-submit-login');

  togglePassBtn.addEventListener('click', () => {
    const isPass = passwordInput.type === 'password';
    passwordInput.type = isPass ? 'text' : 'password';
  });

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const user = usernameInput.value.trim();
    const pass = passwordInput.value.trim();

    if (!user) {
      errorBox.textContent = 'Please enter your Username or Crew ID';
      errorBox.style.display = 'block';
      return;
    }
    if (!pass) {
      errorBox.textContent = 'Please enter your Password';
      errorBox.style.display = 'block';
      return;
    }

    errorBox.style.display = 'none';
    submitBtn.disabled = true;
    submitBtn.innerHTML = `<span>Signing In...</span>`;

    setTimeout(() => {
      store.login(user);
      showToast(`Welcome, ${user.toUpperCase()}`);
      window.location.hash = '#/menu';
    }, 400);
  });

  return container;
}
