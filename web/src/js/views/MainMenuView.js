// Step 4: Main Menu Screen
import { store } from '../store.js';
import { renderHeader } from '../components/Header.js';

export function renderMainMenuView() {
  const container = document.createElement('div');
  container.className = 'fade-in';

  const header = renderHeader({
    title: 'Kharsia Lobby',
    subtitle: 'SECR Bilaspur Division',
    showBack: false
  });
  container.appendChild(header);

  const content = document.createElement('div');
  content.className = 'app-content';

  // Calculate live statistics
  const lobbies = store.getLobbies();
  const prs = store.getPrRequests();
  const pendingPr = prs.filter(p => p.status === 'Pending').length;
  const stores = store.getStoreRecords();
  const activeIssued = stores.filter(s => s.status === 'ISSUED').length;
  const jeeps = store.getJeepAvailability();
  const availableJeeps = jeeps.filter(j => j.isAvailable).length;

  content.innerHTML = `
    <!-- Top Operations Overview Banner -->
    <div class="card" style="margin-bottom: 20px; background: linear-gradient(135deg, rgba(13, 27, 42, 0.95), rgba(11, 25, 42, 0.95)); border-color: #1E4D7A;">
      <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 14px;">
        <div style="display: flex; align-items: center; gap: 14px;">
          <img src="./icons/logo.png" alt="Kharsia Lobby" width="58" height="58" class="kharsia-logo" />
          <div>
            <h2 style="font-size: 19px; font-weight: 800; color: #FFFFFF; line-height: 1.2;">
              संयुक्त चालक एवं परिचालक लॉबी
            </h2>
            <p style="font-size: 12px; color: #F1B748; font-weight: 700; letter-spacing: 0.5px;">
              OPERATIONAL PORTAL • KHARSIA (KHS)
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Stats Grid -->
    <div class="stats-grid">
      <div class="stat-box">
        <span class="num">${lobbies.length || 14}</span>
        <span class="label">Lobbies In Directory</span>
      </div>
      <div class="stat-box">
        <span class="num" style="color: #64B5F6;">${pendingPr}</span>
        <span class="label">Pending PR Remarks</span>
      </div>
      <div class="stat-box">
        <span class="num" style="color: #BA68C8;">${activeIssued}</span>
        <span class="label">Fast Issued Items</span>
      </div>
      <div class="stat-box">
        <span class="num" style="color: #00E676;">${availableJeeps}</span>
        <span class="label">Available Jeeps</span>
      </div>
    </div>

    <!-- 7 Main Menu Navigation Cards (matching Android exactly) -->
    <div class="menu-grid">
      <!-- 1. Staff Directory -->
      <a href="#/directory" class="menu-card">
        <div class="menu-icon-wrap" style="background: rgba(0, 230, 118, 0.15); color: #00E676;">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
          </svg>
        </div>
        <div class="menu-info">
          <h3>Staff Directory</h3>
          <p>Call Book & Contact details of 14 lobbies</p>
        </div>
        <span class="menu-badge badge-active">ACTIVE &gt;</span>
      </a>

      <!-- 2. PR Remark -->
      <a href="#/pr" class="menu-card">
        <div class="menu-icon-wrap" style="background: rgba(41, 121, 255, 0.15); color: #2979FF;">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
            <polyline points="9 16 11 18 15 14"></polyline>
          </svg>
        </div>
        <div class="menu-info">
          <h3>PR remark</h3>
          <p>Mark PR, Auto-fetch Crew Details, Sign-off & Admin Approval</p>
        </div>
        <span class="menu-badge badge-active">ACTIVE &gt;</span>
      </a>

      <!-- 3. Store Register -->
      <a href="#/store" class="menu-card">
        <div class="menu-icon-wrap" style="background: rgba(186, 104, 200, 0.15); color: #BA68C8;">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"></path>
            <path d="m3.3 7 8.7 5 8.7-5"></path>
            <path d="M12 22V12"></path>
          </svg>
        </div>
        <div class="menu-info">
          <h3>Store register</h3>
          <p>Fast Issue & Fast Return / CHO Equipment Register</p>
        </div>
        <span class="menu-badge badge-active">ACTIVE &gt;</span>
      </a>

      <!-- 4. Long Hour Update -->
      <a href="#/longhour" class="menu-card">
        <div class="menu-icon-wrap" style="background: rgba(255, 183, 77, 0.15); color: #FFB74D;">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 22h14"></path>
            <path d="M5 2h14"></path>
            <path d="M17 22v-4.172a2 2 0 0 0-.586-1.414L12 12l-4.414 4.414A2 2 0 0 0 7 17.828V22"></path>
            <path d="M7 2v4.172a2 2 0 0 0 .586 1.414L12 12l4.414-4.414A2 2 0 0 0 17 6.172V2"></path>
          </svg>
        </div>
        <div class="menu-info">
          <h3>Long hour update</h3>
          <p>Crew duty hours & overtime monitoring</p>
        </div>
        <span class="menu-badge badge-active">ACTIVE &gt;</span>
      </a>

      <!-- 5. Roaster & TLC Update -->
      <a href="#/roster" class="menu-card">
        <div class="menu-icon-wrap" style="background: rgba(79, 195, 247, 0.15); color: #4FC3F7;">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
        </div>
        <div class="menu-info">
          <h3>Roaster &TLC update</h3>
          <p>Shift wise roaster & TLC updates, Admin portal entry</p>
        </div>
        <span class="menu-badge badge-active">ACTIVE &gt;</span>
      </a>

      <!-- 6. Jeep Movement -->
      <a href="#/jeep" class="menu-card">
        <div class="menu-icon-wrap" style="background: rgba(255, 64, 129, 0.15); color: #FF4081;">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.5 2.8C2.1 11.2 2 11.6 2 12v4c0 .6.4 1 1 1h2"></path>
            <circle cx="7" cy="17" r="2"></circle>
            <path d="M9 17h6"></path>
            <circle cx="17" cy="17" r="2"></circle>
          </svg>
        </div>
        <div class="menu-info">
          <h3>Jeep movement</h3>
          <p>Jeep Availability & Entry (जीप उपलब्धता एवं एंट्री)</p>
        </div>
        <span class="menu-badge badge-active">ACTIVE &gt;</span>
      </a>

      <!-- 7. Pdd & Pad (Coming Soon) -->
      <div class="menu-card" style="opacity: 0.75; cursor: default;">
        <div class="menu-icon-wrap" style="background: rgba(77, 182, 172, 0.15); color: #4DB6AC;">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="4" y="3" width="16" height="16" rx="2"></rect>
            <path d="M4 11h16"></path>
            <path d="M12 3v8"></path>
            <path d="m8 19-2 3"></path>
            <path d="m16 19 2 3"></path>
          </svg>
        </div>
        <div class="menu-info">
          <h3>Pdd &pad</h3>
          <p>Pre-departure detention & arrival logs</p>
        </div>
        <span class="menu-badge badge-coming">Coming Soon</span>
      </div>
    </div>
  `;

  container.appendChild(content);
  return container;
}
