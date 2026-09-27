// Staff Directory Screen (Call Book for 14 Lobbies)
import { store } from '../store.js';
import { renderHeader } from '../components/Header.js';
import { showToast } from '../components/Toast.js';

export function renderStaffDirectoryView() {
  const container = document.createElement('div');
  container.className = 'fade-in';

  const header = renderHeader({
    title: 'Staff Directory',
    subtitle: 'Call Book of 14 Lobbies',
    showBack: true,
    onBack: () => window.location.hash = '#/menu'
  });
  container.appendChild(header);

  const content = document.createElement('div');
  content.className = 'app-content';

  const lobbies = store.getLobbies();
  let selectedLobbyCode = lobbies.length > 0 ? lobbies[0].code : 'KHS';
  let searchQuery = '';

  content.innerHTML = `
    <!-- Search Bar -->
    <div class="search-container">
      <svg class="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="11" cy="11" r="8"></circle>
        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
      </svg>
      <input 
        type="search" 
        id="directory-search" 
        class="search-input" 
        placeholder="Search staff by Name, Mobile, Designation, CUG..." 
      />
    </div>

    <!-- 14 Lobbies Horizontal Scroll Filter Bar -->
    <div class="tabs-header" id="lobby-tabs">
      <button class="tab-btn ${!selectedLobbyCode ? 'active' : ''}" data-lobby="">All Lobbies (14)</button>
      ${lobbies.map(l => `
        <button class="tab-btn ${selectedLobbyCode === l.code ? 'active' : ''}" data-lobby="${l.code}">
          ${l.name} (${l.code})
        </button>
      `).join('')}
    </div>

    <!-- Active Search Filter Indicator -->
    <div id="filter-status-bar" style="margin-bottom: 12px; font-size: 12px; color: #90A4AE; display: flex; justify-content: space-between; align-items: center;">
      <span id="results-count-text">Loading contacts...</span>
    </div>

    <!-- Contact List Container -->
    <div id="contacts-list" style="display: flex; flex-direction: column; gap: 12px;"></div>
  `;

  container.appendChild(content);

  const searchInput = content.querySelector('#directory-search');
  const tabsHeader = content.querySelector('#lobby-tabs');
  const contactsList = content.querySelector('#contacts-list');
  const countText = content.querySelector('#results-count-text');

  function renderContacts() {
    contactsList.innerHTML = '';
    const results = store.searchDirectory(searchQuery, selectedLobbyCode);
    countText.textContent = `Showing ${results.length} contacts ${selectedLobbyCode ? `in ${selectedLobbyCode}` : 'across all lobbies'}`;

    if (results.length === 0) {
      contactsList.innerHTML = `
        <div class="card" style="text-align: center; padding: 40px 20px;">
          <p style="color: #90A4AE; font-size: 15px; margin-bottom: 8px;">No contacts found matching "${searchQuery}"</p>
          <button class="btn btn-outline btn-sm" id="btn-clear-search">Clear Search</button>
        </div>
      `;
      const clearBtn = contactsList.querySelector('#btn-clear-search');
      if (clearBtn) {
        clearBtn.addEventListener('click', () => {
          searchInput.value = '';
          searchQuery = '';
          renderContacts();
        });
      }
      return;
    }

    // Limit initial DOM render to 50 for ultra-smooth responsiveness
    const visibleResults = results.slice(0, 60);

    visibleResults.forEach(c => {
      const card = document.createElement('div');
      card.className = 'card';
      card.style.padding = '14px 16px';
      
      const phoneToUse = c.cug || c.mobile || '';

      card.innerHTML = `
        <div style="display: flex; align-items: center; justify-content: space-between; gap: 12px;">
          <div style="display: flex; align-items: center; gap: 12px; flex: 1; min-width: 0;">
            <div style="width: 44px; height: 44px; border-radius: 50%; background: #13273F; border: 1px solid #1E4D7A; color: #64B5F6; font-size: 15px; font-weight: 800; display: flex; align-items: center; justify-content: center; flex-shrink: 0;">
              ${(c.name || 'R').charAt(0)}
            </div>
            <div style="flex: 1; min-width: 0;">
              <div style="display: flex; align-items: center; gap: 6px; flex-wrap: wrap;">
                <h4 style="font-size: 15px; font-weight: 700; color: #FFFFFF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">
                  ${c.name}
                </h4>
                <span style="font-size: 10px; background: rgba(241, 183, 72, 0.15); color: #F1B748; padding: 1px 6px; border-radius: 4px; font-weight: 700;">
                  ${c.lobbyCode || ''}
                </span>
              </div>
              <p style="font-size: 12px; color: #64B5F6; font-weight: 600; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">
                ${c.designation || c.category || 'Running Staff'}
              </p>
              <div style="display: flex; gap: 12px; font-size: 12px; color: #B0BEC5; margin-top: 2px;">
                ${c.cug ? `<span>CUG: <strong>${c.cug}</strong></span>` : ''}
                ${c.mobile && c.mobile !== c.cug ? `<span>Mob: <strong>${c.mobile}</strong></span>` : ''}
              </div>
            </div>
          </div>

          <div style="display: flex; align-items: center; gap: 6px; flex-shrink: 0;">
            ${phoneToUse ? `
              <a href="tel:${phoneToUse}" class="btn-icon" style="background: rgba(0, 230, 118, 0.15); color: #00E676;" title="Call ${c.name}">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                </svg>
              </a>
              <button class="btn-icon btn-copy-contact" data-phone="${phoneToUse}" style="background: rgba(41, 121, 255, 0.15); color: #64B5F6;" title="Copy Phone Number">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                  <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                </svg>
              </button>
            ` : ''}
          </div>
        </div>
      `;
      contactsList.appendChild(card);
    });

    if (results.length > 60) {
      const moreMsg = document.createElement('div');
      moreMsg.style.cssText = 'text-align: center; font-size: 12px; color: #90A4AE; padding: 12px;';
      moreMsg.textContent = `+ ${results.length - 60} more contacts. Use search to narrow down results.`;
      contactsList.appendChild(moreMsg);
    }

    // Attach copy button listeners
    contactsList.querySelectorAll('.btn-copy-contact').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const phone = e.currentTarget.getAttribute('data-phone');
        navigator.clipboard.writeText(phone).then(() => {
          showToast(`Copied ${phone} to clipboard`);
        }).catch(() => {
          showToast(`Number: ${phone}`);
        });
      });
    });
  }

  // Handle Tab Switch
  tabsHeader.addEventListener('click', (e) => {
    const target = e.target.closest('.tab-btn');
    if (!target) return;
    tabsHeader.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    target.classList.add('active');
    selectedLobbyCode = target.getAttribute('data-lobby');
    renderContacts();
  });

  // Handle Live Search
  let searchTimeout = null;
  searchInput.addEventListener('input', (e) => {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
      searchQuery = e.target.value.trim();
      renderContacts();
    }, 200);
  });

  renderContacts();
  return container;
}
