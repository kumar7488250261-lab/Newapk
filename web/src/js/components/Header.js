// Top Navigation Bar Component
import { store } from '../store.js';

export function renderHeader({ title = 'Kharsia Lobby', subtitle = 'SECR Bilaspur Division', showBack = false, onBack = null } = {}) {
  const auth = store.getAuth();
  const backBtnHtml = showBack 
    ? `<button class="btn-icon" id="topbar-back-btn" aria-label="Go Back">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <line x1="19" y1="12" x2="5" y2="12"></line>
          <polyline points="12 19 5 12 12 5"></polyline>
        </svg>
      </button>`
    : `<img src="./icons/logo.png" alt="Kharsia Lobby Logo" width="34" height="34" class="kharsia-logo" />`;

  const userHtml = auth.isLoggedIn
    ? `<span class="user-badge">${auth.userId}</span>
       <button class="btn-icon" id="topbar-logout-btn" title="Logout">
         <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#FF5252" stroke-width="2">
           <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
           <polyline points="16 17 21 12 16 7"></polyline>
           <line x1="21" y1="12" x2="9" y2="12"></line>
         </svg>
       </button>`
    : `<a href="#/login" class="btn btn-sm btn-primary" style="padding: 6px 14px; min-height: auto; font-size: 12px;">Login</a>`;

  const header = document.createElement('header');
  header.className = 'app-topbar';
  header.innerHTML = `
    <div class="topbar-left">
      ${backBtnHtml}
      <div class="topbar-title">
        <h1>${title}</h1>
        <span>${subtitle}</span>
      </div>
    </div>
    <div class="topbar-right">
      ${userHtml}
    </div>
  `;

  if (showBack) {
    const backBtn = header.querySelector('#topbar-back-btn');
    if (backBtn) {
      backBtn.addEventListener('click', () => {
        if (onBack) onBack();
        else window.history.back();
      });
    }
  }

  const logoutBtn = header.querySelector('#topbar-logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', () => {
      if (confirm('Are you sure you want to sign out?')) {
        store.logout();
        window.location.hash = '#/landing';
      }
    });
  }

  return header;
}
