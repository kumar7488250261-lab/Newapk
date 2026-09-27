// Step 1: Welcome Splash Screen
export function renderWelcomeView() {
  const container = document.createElement('div');
  container.className = 'fade-in';
  container.style.cssText = `
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    padding: 44px 24px;
    background: #060D18;
    text-align: center;
  `;

  container.innerHTML = `
    <div></div>

    <div style="display: flex; flex-direction: column; align-items: center; max-width: 440px; width: 100%;">
      <!-- Official Circular Kharsia Lobby Emblem -->
      <img src="./icons/logo.png" alt="Kharsia Lobby Emblem" width="140" height="140" class="kharsia-logo" style="margin-bottom: 24px;" />

      <h2 style="font-size: 24px; font-weight: 700; color: #ffffff; margin-bottom: 4px;">Welcome to</h2>
      <h1 style="font-size: 32px; font-weight: 800; color: #FFB74D; margin-bottom: 14px;">Kharsia Lobby</h1>

      <p style="font-size: 12px; font-weight: 700; color: #90A4AE; letter-spacing: 1.8px; margin-bottom: 4px; text-transform: uppercase;">
        SOUTH EAST CENTRAL RAILWAY
      </p>
      <p style="font-size: 11px; font-weight: 600; color: #607D8B; letter-spacing: 1.2px; margin-bottom: 36px; text-transform: uppercase;">
        CREW MANAGEMENT & OPERATIONAL PORTAL
      </p>

      <!-- Gradient Progress Bar -->
      <div class="progress-bar-container" style="max-width: 260px; margin-bottom: 28px;">
        <div id="welcome-progress-bar" class="progress-bar-fill" style="width: 0%;"></div>
      </div>

      <!-- Enter Portal Now Pill Button -->
      <a href="#/landing" class="btn btn-secondary btn-pill" style="padding: 12px 28px; font-size: 14px; font-weight: 700; display: inline-flex; align-items: center; gap: 8px;">
        <span>Enter Portal Now</span>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <line x1="5" y1="12" x2="19" y2="12"></line>
          <polyline points="12 5 19 12 12 19"></polyline>
        </svg>
      </a>
    </div>

    <!-- Division Footer Label -->
    <div style="font-size: 11px; color: #546E7A; letter-spacing: 1.5px; font-weight: 500;">
      KHS • BILASPUR DIVISION • SECR
    </div>
  `;

  // Animate progress bar and auto-advance after 2.5 seconds if user doesn't click
  const progressBar = container.querySelector('#welcome-progress-bar');
  let startTime = null;
  const duration = 2400;

  function step(timestamp) {
    if (!startTime) startTime = timestamp;
    const elapsed = timestamp - startTime;
    const pct = Math.min(100, (elapsed / duration) * 100);
    if (progressBar) progressBar.style.width = `${pct}%`;

    if (elapsed < duration) {
      requestAnimationFrame(step);
    } else {
      // Auto transition to landing
      if (window.location.hash === '#/welcome' || window.location.hash === '' || window.location.hash === '#/') {
        window.location.hash = '#/landing';
      }
    }
  }

  requestAnimationFrame(step);

  return container;
}
