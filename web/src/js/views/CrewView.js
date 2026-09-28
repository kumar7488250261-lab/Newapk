// Crew Master Directory View - Running Crew Verification without Public Phone Numbers
import { store } from '../store.js';
import { renderHeader } from '../components/Header.js';

export function renderCrewView() {
  const container = document.createElement('div');
  container.className = 'fade-in';

  const header = renderHeader({
    title: 'Kharsia Crew Master',
    subtitle: 'Running Crew Roster Cadre & Designations',
    showBack: true,
    onBack: () => window.location.hash = '#/dashboard'
  });
  container.appendChild(header);

  const content = document.createElement('div');
  content.className = 'app-content';

  let selectedCategory = 'ALL'; // ALL, LP, ALP, GUARD
  let searchQuery = '';

  function render() {
    const allCrew = store.getAllCrew();
    const filtered = allCrew.filter(c => {
      if (selectedCategory !== 'ALL' && c.category !== selectedCategory) return false;
      if (searchQuery) {
        const q = searchQuery.toLowerCase();
        return (c.crewId && c.crewId.toLowerCase().includes(q)) ||
               (c.name && c.name.toLowerCase().includes(q)) ||
               (c.designation && c.designation.toLowerCase().includes(q)) ||
               (c.category && c.category.toLowerCase().includes(q));
      }
      return true;
    });

    const countLP = allCrew.filter(c => c.category === 'LP').length;
    const countALP = allCrew.filter(c => c.category === 'ALP').length;
    const countGuard = allCrew.filter(c => c.category === 'GUARD').length;

    content.innerHTML = `
      <!-- Toolbar & Category Filter -->
      <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px; margin-bottom: 16px;">
        <div style="display: flex; gap: 8px; flex-wrap: wrap;">
          <button class="btn btn-sm ${selectedCategory === 'ALL' ? 'btn-primary' : 'btn-outline'}" id="c-cat-all">All (${allCrew.length})</button>
          <button class="btn btn-sm ${selectedCategory === 'LP' ? 'btn-primary' : 'btn-outline'}" id="c-cat-lp">Loco Pilots (${countLP})</button>
          <button class="btn btn-sm ${selectedCategory === 'ALP' ? 'btn-primary' : 'btn-outline'}" id="c-cat-alp">ALPs (${countALP})</button>
          <button class="btn btn-sm ${selectedCategory === 'GUARD' ? 'btn-primary' : 'btn-outline'}" id="c-cat-guard">Train Managers (${countGuard})</button>
        </div>

        <span style="font-size: 12px; color: #94A3B8; font-family: var(--font-mono);">
          Showing ${filtered.length} of ${allCrew.length} crew
        </span>
      </div>

      <!-- Search Input -->
      <div style="margin-bottom: 16px;">
        <input type="text" 
               id="input-crew-search" 
               class="input-field" 
               placeholder="Search by Crew ID (e.g. KHS1001), Name, or Designation..." 
               value="${searchQuery}" 
               style="font-family: var(--font-mono);">
      </div>

      <!-- Crew Table Container -->
      <div class="card" style="padding: 0; overflow: hidden; border-color: #1E293B;">
        <div style="overflow-x: auto;">
          <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 13.5px;">
            <thead>
              <tr style="background: #090E17; border-bottom: 1px solid #1E293B; color: #F59E0B; font-family: var(--font-mono); font-size: 11.5px; text-transform: uppercase;">
                <th style="padding: 12px 16px;">Crew ID</th>
                <th style="padding: 12px 16px;">Full Name</th>
                <th style="padding: 12px 16px;">Designation</th>
                <th style="padding: 12px 16px;">Category</th>
                <th style="padding: 12px 16px;">Cadre</th>
              </tr>
            </thead>
            <tbody>
              ${filtered.length === 0 ? `
                <tr><td colspan="5" style="padding: 30px; text-align: center; color: #94A3B8;">No crew members match your search.</td></tr>
              ` : filtered.slice(0, 100).map(c => `
                <tr style="border-bottom: 1px solid #111A29; transition: background 0.1s ease;">
                  <td style="padding: 10px 16px;">
                    <span class="id-pill">${c.crewId}</span>
                  </td>
                  <td style="padding: 10px 16px; font-weight: 600; color: #FFFFFF;">
                    ${c.name}
                  </td>
                  <td style="padding: 10px 16px; color: #94A3B8; font-size: 12.5px;">
                    ${c.designation}
                  </td>
                  <td style="padding: 10px 16px;">
                    <span class="role-pill">${c.category}</span>
                  </td>
                  <td style="padding: 10px 16px; color: #64748B; font-size: 12px;">
                    ${c.cadre || 'Running (Loco)'}
                  </td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
        ${filtered.length > 100 ? `
          <div style="padding: 10px; text-align: center; color: #94A3B8; font-size: 12px; background: #090E17; border-top: 1px solid #1E293B;">
            Showing first 100 results. Type in search to narrow down.
          </div>
        ` : ''}
      </div>
    `;

    // Filter bindings
    content.querySelector('#c-cat-all')?.addEventListener('click', () => { selectedCategory = 'ALL'; render(); });
    content.querySelector('#c-cat-lp')?.addEventListener('click', () => { selectedCategory = 'LP'; render(); });
    content.querySelector('#c-cat-alp')?.addEventListener('click', () => { selectedCategory = 'ALP'; render(); });
    content.querySelector('#c-cat-guard')?.addEventListener('click', () => { selectedCategory = 'GUARD'; render(); });

    content.querySelector('#input-crew-search')?.addEventListener('input', (e) => {
      searchQuery = e.target.value.trim();
      render();
    });
  }

  render();
  container.appendChild(content);
  return container;
}
