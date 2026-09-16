/**
 * KHARSIA LOBBY - SECR BILASPUR DIVISION
 * Responsive Web Application & PWA Logic
 */

(function () {
  'use strict';

  // ==========================================================
  // 1. APP STATE & CONSTANTS
  // ==========================================================
  const STORAGE_KEY_AUTH = 'kharsia_is_logged_in';
  const STORAGE_KEY_USER = 'kharsia_user_id';
  const DEFAULT_USER_ID = 'KHS1234';

  let appState = {
    currentScreen: 'screen-splash',
    screenHistory: [],
    isLoggedIn: false,
    currentUserId: DEFAULT_USER_ID,
    lobbies: [],
    totalStaffCount: 0,
    activeLobby: null,
    activeDetailCategory: 'All',
    globalSearchQuery: '',
    lobbySearchQuery: '',
    selectedStationFilter: 'ALL'
  };

  // ==========================================================
  // 2. DOM ELEMENTS
  // ==========================================================
  const screens = {
    splash: document.getElementById('screen-splash'),
    landing: document.getElementById('screen-landing'),
    login: document.getElementById('screen-login'),
    mainMenu: document.getElementById('screen-main-menu'),
    staffDirectory: document.getElementById('screen-staff-directory'),
    lobbyDetail: document.getElementById('screen-lobby-detail')
  };

  const toastContainer = document.getElementById('toast-container');

  // Login elements
  const formLogin = document.getElementById('form-login');
  const inputUserId = document.getElementById('input-userid');
  const inputPassword = document.getElementById('input-password');
  const btnTogglePassword = document.getElementById('btn-toggle-password');
  const eyeIconShow = document.getElementById('eye-icon-show');
  const eyeIconHide = document.getElementById('eye-icon-hide');
  const errorUserId = document.getElementById('error-userid');
  const errorPassword = document.getElementById('error-password');
  const btnLoginBack = document.getElementById('btn-login-back');
  const btnGotoLogin = document.getElementById('btn-goto-login');

  // Main menu elements
  const menuUserIdText = document.getElementById('menu-user-id-text');
  const btnLogout = document.getElementById('btn-logout');
  const modStaffDirectory = document.getElementById('mod-staff-directory');

  // Staff directory elements
  const btnDirectoryBack = document.getElementById('btn-directory-back');
  const statStaffCount = document.getElementById('stat-staff-count');
  const statLobbiesCount = document.getElementById('stat-lobbies-count');
  const inputGlobalSearch = document.getElementById('input-global-search');
  const btnClearSearch = document.getElementById('btn-clear-search');
  const stationPillsList = document.getElementById('station-pills-list');
  const lobbiesGrid = document.getElementById('lobbies-grid');
  const lobbiesSection = document.getElementById('lobbies-section');
  const searchResultsSection = document.getElementById('search-results-section');
  const searchResultsList = document.getElementById('search-results-list');
  const searchResultsCount = document.getElementById('search-results-count');

  // Lobby detail elements
  const btnDetailBack = document.getElementById('btn-detail-back');
  const detailLobbyCode = document.getElementById('detail-lobby-code');
  const detailLobbyName = document.getElementById('detail-lobby-name');
  const inputLobbySearch = document.getElementById('input-lobby-search');
  const btnClearLobbySearch = document.getElementById('btn-clear-lobby-search');
  const categoryTabsList = document.getElementById('category-tabs-list');
  const lobbyContactsList = document.getElementById('lobby-contacts-list');
  const currentCategoryLabel = document.getElementById('current-category-label');
  const currentContactsCount = document.getElementById('current-contacts-count');

  // ==========================================================
  // 3. TOAST NOTIFICATION UTILITY
  // ==========================================================
  function showToast(message, type = 'info') {
    if (!toastContainer) return;

    const toast = document.createElement('div');
    toast.className = `toast-msg ${type === 'success' ? 'toast-success' : type === 'error' ? 'toast-error' : ''}`;
    
    // Icon
    let iconSvg = '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/></svg>';
    if (type === 'success') {
      iconSvg = '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>';
    } else if (type === 'error') {
      iconSvg = '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>';
    }

    toast.innerHTML = `${iconSvg}<span>${message}</span>`;
    toastContainer.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(8px)';
      toast.style.transition = 'opacity 0.25s ease, transform 0.25s ease';
      setTimeout(() => {
        if (toast.parentNode) toast.parentNode.removeChild(toast);
      }, 250);
    }, 2800);
  }

  // ==========================================================
  // 4. SCREEN NAVIGATION WITH BROWSER BACK SUPPORT
  // ==========================================================
  function navigateTo(screenId, pushHistory = true) {
    if (!screens[screenId]) return;

    // Hide all screens
    Object.values(screens).forEach((el) => {
      if (el) el.classList.remove('active');
    });

    // Show target screen
    screens[screenId].classList.add('active');

    if (pushHistory && appState.currentScreen !== screenId) {
      window.history.pushState({ screen: screenId }, '', '');
    }

    appState.currentScreen = screenId;
    window.scrollTo(0, 0);
  }

  window.addEventListener('popstate', (event) => {
    if (event.state && event.state.screen) {
      navigateTo(event.state.screen, false);
    } else {
      // Default fallback
      if (appState.currentScreen === 'lobbyDetail') {
        navigateTo('staffDirectory', false);
      } else if (appState.currentScreen === 'staffDirectory') {
        navigateTo('mainMenu', false);
      } else if (appState.currentScreen === 'login') {
        navigateTo('landing', false);
      }
    }
  });

  // ==========================================================
  // 5. DATA FETCHING & PARSING (staff_directory.json)
  // ==========================================================
  async function loadStaffDirectory() {
    try {
      const response = await fetch('assets/staff_directory.json');
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      const data = await response.json();
      if (data && Array.isArray(data.lobbies)) {
        appState.lobbies = data.lobbies;

        // Calculate total contacts
        let total = 0;
        appState.lobbies.forEach((lobby) => {
          let lobbyTotal = 0;
          if (Array.isArray(lobby.categories)) {
            lobby.categories.forEach((cat) => {
              if (Array.isArray(cat.contacts)) {
                lobbyTotal += cat.contacts.length;
              }
            });
          }
          lobby.totalContacts = lobbyTotal;
          total += lobbyTotal;
        });

        appState.totalStaffCount = total;

        if (statStaffCount) {
          statStaffCount.textContent = total.toLocaleString();
        }
        if (statLobbiesCount) {
          statLobbiesCount.textContent = appState.lobbies.length;
        }

        renderStationFilterPills();
        renderLobbiesList();
      }
    } catch (err) {
      console.warn('Could not fetch staff directory JSON:', err);
      showToast('Offline Mode: Staff directory cached', 'info');
    }
  }

  // ==========================================================
  // 6. RENDER STATION FILTER PILLS & LOBBIES
  // ==========================================================
  function renderStationFilterPills() {
    if (!stationPillsList) return;
    stationPillsList.innerHTML = '';

    // "All Lobbies" pill
    const allPill = document.createElement('button');
    allPill.type = 'button';
    allPill.className = `filter-pill ${appState.selectedStationFilter === 'ALL' ? 'active' : ''}`;
    allPill.textContent = 'All Lobbies (14)';
    allPill.addEventListener('click', () => {
      appState.selectedStationFilter = 'ALL';
      renderStationFilterPills();
      renderLobbiesList();
    });
    stationPillsList.appendChild(allPill);

    // Individual Station pills
    appState.lobbies.forEach((lobby) => {
      const pill = document.createElement('button');
      pill.type = 'button';
      pill.className = `filter-pill ${appState.selectedStationFilter === lobby.code ? 'active' : ''}`;
      pill.textContent = `${lobby.code} (${lobby.totalContacts})`;
      pill.addEventListener('click', () => {
        appState.selectedStationFilter = lobby.code;
        renderStationFilterPills();
        renderLobbiesList();
      });
      stationPillsList.appendChild(pill);
    });
  }

  function renderLobbiesList() {
    if (!lobbiesGrid) return;
    lobbiesGrid.innerHTML = '';

    const filtered = appState.selectedStationFilter === 'ALL'
      ? appState.lobbies
      : appState.lobbies.filter((l) => l.code === appState.selectedStationFilter);

    if (filtered.length === 0) {
      lobbiesGrid.innerHTML = `
        <div class="empty-state">
          <p>No lobbies match the selected filter.</p>
        </div>
      `;
      return;
    }

    filtered.forEach((lobby) => {
      const card = document.createElement('div');
      card.className = 'lobby-card';
      card.setAttribute('role', 'button');
      card.setAttribute('tabindex', '0');

      // Category names preview
      const catNames = lobby.categories.map((c) => c.name).slice(0, 3).join(' • ');

      const isKharsia = lobby.code.toUpperCase() === 'KHS';

      card.innerHTML = `
        <div class="lobby-card-left">
          <div class="station-avatar ${isKharsia ? 'highlight' : ''}">${lobby.code}</div>
          <div class="lobby-info">
            <div class="lobby-name-row">
              <span class="lobby-name">${lobby.name}</span>
            </div>
            <span class="lobby-categories-preview">${catNames}</span>
          </div>
        </div>
        <div class="lobby-card-right">
          <span class="lobby-staff-count">${lobby.totalContacts} Staff</span>
          <svg class="lobby-arrow" viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/>
          </svg>
        </div>
      `;

      card.addEventListener('click', () => openLobbyDetail(lobby));
      lobbiesGrid.appendChild(card);
    });
  }

  // ==========================================================
  // 7. GLOBAL SEARCH IN ALL LOBBIES
  // ==========================================================
  function handleGlobalSearch(query) {
    const q = query.trim().toLowerCase();
    appState.globalSearchQuery = q;

    if (!btnClearSearch || !searchResultsSection || !lobbiesSection) return;

    if (q.length > 0) {
      btnClearSearch.classList.remove('hidden');
      searchResultsSection.classList.remove('hidden');
      lobbiesSection.classList.add('hidden');

      const matches = [];
      appState.lobbies.forEach((lobby) => {
        if (Array.isArray(lobby.categories)) {
          lobby.categories.forEach((cat) => {
            if (Array.isArray(cat.contacts)) {
              cat.contacts.forEach((contact) => {
                const name = (contact.name || '').toLowerCase();
                const mobile = (contact.mobile || '');
                const category = (cat.name || '').toLowerCase();
                const lobbyName = (lobby.name || '').toLowerCase();
                const lobbyCode = (lobby.code || '').toLowerCase();

                if (
                  name.includes(q) ||
                  mobile.includes(q) ||
                  category.includes(q) ||
                  lobbyName.includes(q) ||
                  lobbyCode.includes(q)
                ) {
                  matches.push({
                    name: contact.name,
                    mobile: contact.mobile,
                    category: cat.name,
                    lobbyCode: lobby.code,
                    lobbyName: lobby.name
                  });
                }
              });
            }
          });
        }
      });

      searchResultsCount.textContent = `${matches.length} found`;
      renderContactCards(searchResultsList, matches);
    } else {
      btnClearSearch.classList.add('hidden');
      searchResultsSection.classList.add('hidden');
      lobbiesSection.classList.remove('hidden');
    }
  }

  // ==========================================================
  // 8. CONTACT CARDS RENDERER & ACTIONS (Call, WhatsApp, Copy)
  // ==========================================================
  function renderContactCards(container, contacts) {
    if (!container) return;
    container.innerHTML = '';

    if (contacts.length === 0) {
      container.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" width="40" height="40" fill="currentColor">
              <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
          </div>
          <p>No staff contacts found matching your query.</p>
        </div>
      `;
      return;
    }

    contacts.forEach((contact) => {
      const cleanPhone = (contact.mobile || '').replace(/[^0-9]/g, '');
      const waNumber = cleanPhone.length === 10 ? `91${cleanPhone}` : cleanPhone;

      const card = document.createElement('div');
      card.className = 'contact-card';

      card.innerHTML = `
        <div class="contact-top-row">
          <div class="contact-name-group">
            <span class="contact-name">${escapeHtml(contact.name || 'Staff')}</span>
            <div class="contact-tags-row">
              <span class="tag-category">${escapeHtml(contact.category || 'General')}</span>
              <span class="tag-lobby">${escapeHtml(contact.lobbyCode || '')} • ${escapeHtml(contact.lobbyName || '')}</span>
            </div>
          </div>
          <div class="contact-phone-display">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="#38BDF8">
              <path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/>
            </svg>
            <span>${escapeHtml(contact.mobile || '')}</span>
          </div>
        </div>

        <div class="contact-actions-row">
          <a href="tel:${cleanPhone}" class="btn-contact-action btn-call" title="Call staff">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/>
            </svg>
            <span>Call</span>
          </a>
          <a href="https://wa.me/${waNumber}" target="_blank" rel="noopener noreferrer" class="btn-contact-action btn-whatsapp" title="WhatsApp message">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M12.04 2c-5.46 0-9.91 4.45-9.91 9.91 0 1.75.46 3.45 1.32 4.95L2.05 22l5.25-1.38c1.45.79 3.08 1.21 4.74 1.21 5.46 0 9.91-4.45 9.91-9.91 0-2.65-1.03-5.14-2.9-7.01A9.816 9.816 0 0 0 12.04 2m.01 1.67c2.2 0 4.26.86 5.82 2.42a8.225 8.225 0 0 1 2.41 5.83c0 4.54-3.7 8.24-8.24 8.24-1.48 0-2.93-.4-4.2-1.15l-.3-.18-3.12.82.83-3.04-.2-.31a8.19 8.19 0 0 1-1.26-4.38c0-4.54 3.7-8.24 8.24-8.24m4.52 11.66c-.25-.13-1.47-.72-1.7-.81-.23-.08-.39-.13-.56.13-.17.25-.64.81-.79.97-.14.17-.29.19-.54.06-.25-.13-1.06-.39-2.03-1.25-.75-.67-1.26-1.5-1.41-1.75-.15-.25-.02-.39.11-.51.11-.11.25-.29.37-.43.13-.15.17-.25.25-.42.08-.17.04-.31-.02-.44-.06-.13-.56-1.35-.77-1.85-.2-.49-.4-.42-.56-.43h-.47c-.17 0-.44.06-.67.31-.23.25-.88.86-.88 2.1 0 1.24.9 2.44 1.03 2.61.13.17 1.77 2.7 4.29 3.79.6.26 1.07.41 1.44.53.6.19 1.15.16 1.58.1.48-.07 1.47-.6 1.68-1.18.21-.58.21-1.07.15-1.18-.07-.12-.23-.19-.48-.31z"/>
            </svg>
            <span>WhatsApp</span>
          </a>
          <button type="button" class="btn-contact-action btn-copy" data-phone="${escapeHtml(contact.mobile || '')}" title="Copy number">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
            </svg>
            <span>Copy</span>
          </button>
        </div>
      `;

      // Copy click handler
      const copyBtn = card.querySelector('.btn-copy');
      if (copyBtn) {
        copyBtn.addEventListener('click', () => {
          const text = copyBtn.getAttribute('data-phone');
          copyToClipboard(text);
        });
      }

      container.appendChild(card);
    });
  }

  function copyToClipboard(text) {
    if (!text) return;
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(text).then(() => {
        showToast(`Copied: ${text}`, 'success');
      }).catch(() => {
        fallbackCopy(text);
      });
    } else {
      fallbackCopy(text);
    }
  }

  function fallbackCopy(text) {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    textArea.style.position = 'fixed';
    textArea.style.left = '-999999px';
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    try {
      document.execCommand('copy');
      showToast(`Copied: ${text}`, 'success');
    } catch (err) {
      showToast('Could not copy number', 'error');
    }
    document.body.removeChild(textArea);
  }

  // ==========================================================
  // 9. LOBBY DETAIL VIEW
  // ==========================================================
  function openLobbyDetail(lobby) {
    appState.activeLobby = lobby;
    appState.activeDetailCategory = 'All';
    appState.lobbySearchQuery = '';

    if (detailLobbyCode) detailLobbyCode.textContent = lobby.code;
    if (detailLobbyName) detailLobbyName.textContent = `${lobby.name} (${lobby.code})`;
    if (inputLobbySearch) inputLobbySearch.value = '';
    if (btnClearLobbySearch) btnClearLobbySearch.classList.add('hidden');

    renderLobbyCategoryTabs();
    renderLobbyContacts();
    navigateTo('lobbyDetail');
  }

  function renderLobbyCategoryTabs() {
    if (!categoryTabsList || !appState.activeLobby) return;
    categoryTabsList.innerHTML = '';

    // "All Staff" tab
    const allTab = document.createElement('button');
    allTab.type = 'button';
    allTab.className = `category-pill ${appState.activeDetailCategory === 'All' ? 'active' : ''}`;
    allTab.textContent = `All (${appState.activeLobby.totalContacts})`;
    allTab.addEventListener('click', () => {
      appState.activeDetailCategory = 'All';
      renderLobbyCategoryTabs();
      renderLobbyContacts();
    });
    categoryTabsList.appendChild(allTab);

    // Categories tabs
    if (Array.isArray(appState.activeLobby.categories)) {
      appState.activeLobby.categories.forEach((cat) => {
        const tab = document.createElement('button');
        tab.type = 'button';
        tab.className = `category-pill ${appState.activeDetailCategory === cat.name ? 'active' : ''}`;
        const count = Array.isArray(cat.contacts) ? cat.contacts.length : 0;
        tab.textContent = `${cat.name} (${count})`;
        tab.addEventListener('click', () => {
          appState.activeDetailCategory = cat.name;
          renderLobbyCategoryTabs();
          renderLobbyContacts();
        });
        categoryTabsList.appendChild(tab);
      });
    }
  }

  function renderLobbyContacts() {
    if (!appState.activeLobby) return;

    let contacts = [];
    if (appState.activeDetailCategory === 'All') {
      appState.activeLobby.categories.forEach((cat) => {
        if (Array.isArray(cat.contacts)) {
          cat.contacts.forEach((c) => {
            contacts.push({
              name: c.name,
              mobile: c.mobile,
              category: cat.name,
              lobbyCode: appState.activeLobby.code,
              lobbyName: appState.activeLobby.name
            });
          });
        }
      });
    } else {
      const selectedCat = appState.activeLobby.categories.find((c) => c.name === appState.activeDetailCategory);
      if (selectedCat && Array.isArray(selectedCat.contacts)) {
        selectedCat.contacts.forEach((c) => {
          contacts.push({
            name: c.name,
            mobile: c.mobile,
            category: selectedCat.name,
            lobbyCode: appState.activeLobby.code,
            lobbyName: appState.activeLobby.name
          });
        });
      }
    }

    // Apply in-lobby search filter if present
    const q = appState.lobbySearchQuery.trim().toLowerCase();
    if (q.length > 0) {
      contacts = contacts.filter((c) => {
        return (
          (c.name || '').toLowerCase().includes(q) ||
          (c.mobile || '').includes(q) ||
          (c.category || '').toLowerCase().includes(q)
        );
      });
    }

    if (currentCategoryLabel) {
      currentCategoryLabel.textContent = appState.activeDetailCategory === 'All' ? 'All Staff' : appState.activeDetailCategory;
    }
    if (currentContactsCount) {
      currentContactsCount.textContent = `${contacts.length} Contacts`;
    }

    renderContactCards(lobbyContactsList, contacts);
  }

  // ==========================================================
  // 10. LOGIN & AUTHENTICATION LOGIC
  // ==========================================================
  function initAuth() {
    try {
      const storedLoggedIn = localStorage.getItem(STORAGE_KEY_AUTH);
      const storedUser = localStorage.getItem(STORAGE_KEY_USER);
      if (storedLoggedIn === 'true' && storedUser) {
        appState.isLoggedIn = true;
        appState.currentUserId = storedUser;
      }
    } catch (e) {
      console.warn('LocalStorage error:', e);
    }

    if (menuUserIdText) {
      menuUserIdText.textContent = appState.currentUserId;
    }
  }

  function handleLoginSubmit(event) {
    event.preventDefault();

    const rawId = (inputUserId ? inputUserId.value : '').trim().toUpperCase();
    const rawPassword = (inputPassword ? inputPassword.value : '').trim();

    let hasError = false;

    // Validate User ID: Must be 'KHS' followed by exactly 4 digits (e.g. KHS1234)
    const idRegex = /^KHS[0-9]{4}$/;
    if (!rawId) {
      setFieldError(errorUserId, inputUserId, 'Please enter User ID');
      hasError = true;
    } else if (!rawId.startsWith('KHS')) {
      setFieldError(errorUserId, inputUserId, "User ID must start with 'KHS'");
      hasError = true;
    } else if (rawId.length !== 7 || !idRegex.test(rawId)) {
      setFieldError(errorUserId, inputUserId, 'User ID must be KHS followed by exactly 4 digits (e.g. KHS1234)');
      hasError = true;
    } else {
      clearFieldError(errorUserId, inputUserId);
    }

    // Validate Password: default is 1234. (No hint shown as requested!)
    if (!rawPassword) {
      setFieldError(errorPassword, inputPassword, 'Please enter Password');
      hasError = true;
    } else if (rawPassword !== '1234') {
      setFieldError(errorPassword, inputPassword, 'Incorrect password! Please check and try again');
      hasError = true;
    } else {
      clearFieldError(errorPassword, inputPassword);
    }

    if (hasError) return;

    // Login successful
    appState.isLoggedIn = true;
    appState.currentUserId = rawId;

    try {
      localStorage.setItem(STORAGE_KEY_AUTH, 'true');
      localStorage.setItem(STORAGE_KEY_USER, rawId);
    } catch (e) {
      console.warn('LocalStorage error:', e);
    }

    if (menuUserIdText) {
      menuUserIdText.textContent = rawId;
    }

    showToast(`Welcome ${rawId}! Logged in successfully`, 'success');

    // Reset login form fields
    if (inputPassword) inputPassword.value = '';

    navigateTo('mainMenu');
  }

  function setFieldError(errorEl, inputEl, message) {
    if (errorEl) {
      errorEl.textContent = message;
      errorEl.classList.add('visible');
    }
    if (inputEl && inputEl.closest('.input-wrapper')) {
      inputEl.closest('.input-wrapper').classList.add('has-error');
    }
  }

  function clearFieldError(errorEl, inputEl) {
    if (errorEl) {
      errorEl.textContent = '';
      errorEl.classList.remove('visible');
    }
    if (inputEl && inputEl.closest('.input-wrapper')) {
      inputEl.closest('.input-wrapper').classList.remove('has-error');
    }
  }

  function handleLogout() {
    appState.isLoggedIn = false;
    appState.currentUserId = DEFAULT_USER_ID;

    try {
      localStorage.removeItem(STORAGE_KEY_AUTH);
      localStorage.removeItem(STORAGE_KEY_USER);
    } catch (e) {
      console.warn('LocalStorage error:', e);
    }

    showToast('Logged out successfully', 'info');
    navigateTo('landing');
  }

  // ==========================================================
  // 11. EVENT LISTENERS
  // ==========================================================
  function setupEventListeners() {
    // Navigation to Login
    if (btnGotoLogin) {
      btnGotoLogin.addEventListener('click', () => {
        navigateTo('login');
      });
    }

    if (btnLoginBack) {
      btnLoginBack.addEventListener('click', () => {
        navigateTo('landing');
      });
    }

    // Toggle Password Visibility
    if (btnTogglePassword && inputPassword) {
      btnTogglePassword.addEventListener('click', () => {
        const isPassword = inputPassword.type === 'password';
        inputPassword.type = isPassword ? 'text' : 'password';
        if (eyeIconShow) eyeIconShow.classList.toggle('hidden', isPassword);
        if (eyeIconHide) eyeIconHide.classList.toggle('hidden', !isPassword);
      });
    }

    // Auto uppercase User ID
    if (inputUserId) {
      inputUserId.addEventListener('input', (e) => {
        const start = e.target.selectionStart;
        e.target.value = e.target.value.toUpperCase();
        e.target.setSelectionRange(start, start);
        clearFieldError(errorUserId, inputUserId);
      });
    }

    if (inputPassword) {
      inputPassword.addEventListener('input', () => {
        clearFieldError(errorPassword, inputPassword);
      });
    }

    // Form submit
    if (formLogin) {
      formLogin.addEventListener('submit', handleLoginSubmit);
    }

    // Main Menu actions
    if (btnLogout) {
      btnLogout.addEventListener('click', handleLogout);
    }

    if (modStaffDirectory) {
      modStaffDirectory.addEventListener('click', () => {
        navigateTo('staffDirectory');
      });
    }

    // "Coming soon" module cards
    const comingSoonCards = document.querySelectorAll('.menu-card-disabled');
    comingSoonCards.forEach((card) => {
      card.addEventListener('click', () => {
        const title = card.getAttribute('data-title') || 'This module';
        showToast(`Module '${title}' will be available in the upcoming update.`, 'info');
      });
    });

    // Staff Directory actions
    if (btnDirectoryBack) {
      btnDirectoryBack.addEventListener('click', () => {
        navigateTo('mainMenu');
      });
    }

    if (inputGlobalSearch) {
      inputGlobalSearch.addEventListener('input', (e) => {
        handleGlobalSearch(e.target.value);
      });
    }

    if (btnClearSearch && inputGlobalSearch) {
      btnClearSearch.addEventListener('click', () => {
        inputGlobalSearch.value = '';
        handleGlobalSearch('');
        inputGlobalSearch.focus();
      });
    }

    // Lobby Detail actions
    if (btnDetailBack) {
      btnDetailBack.addEventListener('click', () => {
        navigateTo('staffDirectory');
      });
    }

    if (inputLobbySearch) {
      inputLobbySearch.addEventListener('input', (e) => {
        appState.lobbySearchQuery = e.target.value;
        if (btnClearLobbySearch) {
          btnClearLobbySearch.classList.toggle('hidden', e.target.value.length === 0);
        }
        renderLobbyContacts();
      });
    }

    if (btnClearLobbySearch && inputLobbySearch) {
      btnClearLobbySearch.addEventListener('click', () => {
        inputLobbySearch.value = '';
        appState.lobbySearchQuery = '';
        btnClearLobbySearch.classList.add('hidden');
        renderLobbyContacts();
        inputLobbySearch.focus();
      });
    }
  }

  // ==========================================================
  // 12. PWA SERVICE WORKER REGISTRATION
  // ==========================================================
  function registerServiceWorker() {
    if ('serviceWorker' in navigator) {
      window.addEventListener('load', () => {
        // Use relative path for GitHub Pages subpath compatibility
        navigator.serviceWorker
          .register('./service-worker.js', { scope: './' })
          .then((registration) => {
            console.log('Kharsia Lobby PWA ServiceWorker registered with scope:', registration.scope);
          })
          .catch((err) => {
            console.warn('PWA ServiceWorker registration failed:', err);
          });
      });
    }
  }

  // ==========================================================
  // 13. UTILITIES & INITIALIZATION
  // ==========================================================
  function escapeHtml(str) {
    if (!str) return '';
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function init() {
    initAuth();
    setupEventListeners();
    loadStaffDirectory();
    registerServiceWorker();

    // Auto-transition from Splash screen after 1.5 seconds
    setTimeout(() => {
      if (appState.isLoggedIn) {
        navigateTo('mainMenu', false);
      } else {
        navigateTo('landing', false);
      }
    }, 1500);
  }

  // Boot on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
