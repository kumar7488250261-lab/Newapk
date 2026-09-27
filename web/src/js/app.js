// Kharsia Lobby - Main Application Entry & Hash Router
import { store } from './store.js';
import { renderWelcomeView } from './views/WelcomeView.js';
import { renderLandingView } from './views/LandingView.js';
import { renderLoginView } from './views/LoginView.js';
import { renderMainMenuView } from './views/MainMenuView.js';
import { renderStaffDirectoryView } from './views/StaffDirectoryView.js';
import { renderPrRemarkView } from './views/PrRemarkView.js';
import { renderStoreRegisterView } from './views/StoreRegisterView.js';
import { renderLongHourView } from './views/LongHourView.js';
import { renderRosterTlcView } from './views/RosterTlcView.js';
import { renderJeepMovementView } from './views/JeepMovementView.js';

const routes = {
  '#/welcome': renderWelcomeView,
  '#/landing': renderLandingView,
  '#/login': renderLoginView,
  '#/menu': renderMainMenuView,
  '#/directory': renderStaffDirectoryView,
  '#/pr': renderPrRemarkView,
  '#/store': renderStoreRegisterView,
  '#/longhour': renderLongHourView,
  '#/roster': renderRosterTlcView,
  '#/jeep': renderJeepMovementView
};

async function router() {
  const appContainer = document.getElementById('app');
  if (!appContainer) return;

  const rawHash = window.location.hash || '#/welcome';
  const cleanHash = rawHash.split('?')[0];

  // Route matching
  const viewFn = routes[cleanHash];

  appContainer.innerHTML = '';
  window.scrollTo(0, 0);

  if (viewFn) {
    const viewEl = viewFn();
    appContainer.appendChild(viewEl);
  } else {
    // Default fallback
    const auth = store.getAuth();
    if (auth.isLoggedIn) {
      window.location.hash = '#/menu';
    } else {
      window.location.hash = '#/welcome';
    }
  }
}

// Service Worker Registration for Offline PWA
function registerServiceWorker() {
  if ('serviceWorker' in navigator && window.location.protocol.startsWith('http')) {
    window.addEventListener('load', () => {
      navigator.serviceWorker.register('./service-worker.js')
        .then((reg) => {
          console.log('Kharsia Lobby PWA ServiceWorker active:', reg.scope);
        })
        .catch((err) => {
          console.warn('ServiceWorker registration error:', err);
        });
    });
  }
}

// Bootstrap
window.addEventListener('DOMContentLoaded', async () => {
  await store.init();
  registerServiceWorker();

  window.addEventListener('hashchange', router);
  router();
});
