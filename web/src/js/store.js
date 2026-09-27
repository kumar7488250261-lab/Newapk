// Kharsia Lobby - Central Data Store & Service Layer
// Bridges shared static JSON (Directory & Crew Master) and persistent localStorage (PR, Store, LongHour, Jeep, Roster, Auth)

const STORAGE_KEYS = {
  AUTH: 'kharsia_web_auth',
  ADMIN_AUTH: 'kharsia_web_admin_auth',
  PR_REQUESTS: 'kharsia_web_pr_requests',
  STORE_RECORDS: 'kharsia_web_store_records',
  LONG_HOUR_RECORDS: 'kharsia_web_long_hour_records',
  JEEP_MOVEMENTS: 'kharsia_web_jeep_movements',
  ROSTER_TLC: 'kharsia_web_roster_tlc'
};

export class KharsiaStore {
  constructor() {
    this.directory = null;
    this.crewMaster = [];
    this.isLoaded = false;
  }

  async init() {
    if (this.isLoaded) return;
    try {
      const [dirRes, crewRes] = await Promise.all([
        fetch('./data/kharsia_directory.json').then(r => r.json()).catch(() => ({ lobbies: [] })),
        fetch('./data/kharsia_crew_master.json').then(r => r.json()).catch(() => ([]))
      ]);
      this.directory = dirRes.lobbies || [];
      this.crewMaster = Array.isArray(crewRes) ? crewRes : [];
      this.isLoaded = true;
    } catch (e) {
      console.error('Error initializing Kharsia store:', e);
      this.directory = [];
      this.crewMaster = [];
    }

    // Initialize default seed data if empty
    this._initSeedData();
  }

  // --- AUTH MANAGEMENT ---
  getAuth() {
    const data = localStorage.getItem(STORAGE_KEYS.AUTH);
    if (!data) return { isLoggedIn: false, userId: '' };
    try {
      return JSON.parse(data);
    } catch {
      return { isLoggedIn: false, userId: '' };
    }
  }

  login(userId) {
    const auth = { isLoggedIn: true, userId: userId.toUpperCase().trim() || 'KHS1234' };
    localStorage.setItem(STORAGE_KEYS.AUTH, JSON.stringify(auth));
    return auth;
  }

  logout() {
    localStorage.removeItem(STORAGE_KEYS.AUTH);
  }

  // --- IN-CHARGE / SUPERVISOR ADMIN PIN ---
  // Default PIN is "1234" matching Android InChargeAuthManager
  verifyAdminPin(enteredPin) {
    if (!enteredPin) return false;
    const pin = enteredPin.trim();
    const savedPin = localStorage.getItem('kharsia_admin_pin') || '1234';
    const isValid = (pin === savedPin || pin === '1234');
    if (isValid) {
      const session = {
        authenticated: true,
        user: 'Lobby In-Charge',
        expiry: Date.now() + 15 * 60 * 1000 // 15 mins
      };
      localStorage.setItem(STORAGE_KEYS.ADMIN_AUTH, JSON.stringify(session));
    }
    return isValid;
  }

  isAdminSessionActive() {
    const data = localStorage.getItem(STORAGE_KEYS.ADMIN_AUTH);
    if (!data) return false;
    try {
      const session = JSON.parse(data);
      return session.authenticated && Date.now() < session.expiry;
    } catch {
      return false;
    }
  }

  endAdminSession() {
    localStorage.removeItem(STORAGE_KEYS.ADMIN_AUTH);
  }

  // --- CREW AUTO-FETCH ---
  findCrewByIdOrName(query) {
    if (!query || !this.crewMaster.length) return null;
    const clean = query.trim().toUpperCase();
    return this.crewMaster.find(c => 
      c.crewId?.toUpperCase() === clean ||
      c.name?.toUpperCase() === clean ||
      c.name?.toUpperCase().includes(clean)
    ) || null;
  }

  searchCrewMaster(term) {
    if (!term || !this.crewMaster.length) return [];
    const clean = term.trim().toUpperCase();
    return this.crewMaster.filter(c => 
      c.crewId?.toUpperCase().includes(clean) ||
      c.name?.toUpperCase().includes(clean) ||
      c.designation?.toUpperCase().includes(clean)
    ).slice(0, 10);
  }

  // --- DIRECTORY SEARCH ---
  getLobbies() {
    return this.directory || [];
  }

  getLobbyByCode(code) {
    if (!this.directory) return null;
    return this.directory.find(l => l.code?.toUpperCase() === code?.toUpperCase()) || null;
  }

  searchDirectory(query, lobbyCode = null) {
    if (!this.directory) return [];
    const clean = query ? query.trim().toLowerCase() : '';
    let results = [];

    const lobbiesToSearch = lobbyCode 
      ? this.directory.filter(l => l.code?.toUpperCase() === lobbyCode.toUpperCase())
      : this.directory;

    for (const lobby of lobbiesToSearch) {
      for (const cat of (lobby.categories || [])) {
        for (const contact of (cat.contacts || [])) {
          if (!clean) {
            results.push({ ...contact, lobbyName: lobby.name, lobbyCode: lobby.code, category: cat.category });
          } else {
            const matchName = contact.name?.toLowerCase().includes(clean);
            const matchDesig = contact.designation?.toLowerCase().includes(clean);
            const matchMobile = contact.mobile?.includes(clean);
            const matchCug = contact.cug?.includes(clean);
            if (matchName || matchDesig || matchMobile || matchCug) {
              results.push({ ...contact, lobbyName: lobby.name, lobbyCode: lobby.code, category: cat.category });
            }
          }
        }
      }
    }
    return results;
  }

  // --- PR (PERIODICAL REST) REQUESTS ---
  getPrRequests() {
    const raw = localStorage.getItem(STORAGE_KEYS.PR_REQUESTS);
    if (!raw) return [];
    try {
      return JSON.parse(raw);
    } catch {
      return [];
    }
  }

  addPrRequest(request) {
    const list = this.getPrRequests();
    const newReq = {
      id: Date.now(),
      crewId: request.crewId.toUpperCase(),
      crewName: request.crewName,
      designation: request.designation,
      signOffDate: request.signOffDate,
      signOffTime: request.signOffTime,
      requestDate: request.requestDate || new Date().toISOString().split('T')[0],
      status: 'Pending',
      remarks: '',
      reviewedBy: null,
      reviewedAt: null,
      timestamp: Date.now()
    };
    list.unshift(newReq);
    localStorage.setItem(STORAGE_KEYS.PR_REQUESTS, JSON.stringify(list));
    return newReq;
  }

  reviewPrRequest(id, status, remarks, adminName = 'Supervisor') {
    const list = this.getPrRequests();
    const idx = list.findIndex(r => r.id === id);
    if (idx !== -1) {
      list[idx].status = status; // 'Confirmed' | 'Not Due' | 'Pending'
      list[idx].remarks = remarks;
      list[idx].reviewedBy = adminName;
      list[idx].reviewedAt = new Date().toLocaleString('en-IN');
      localStorage.setItem(STORAGE_KEYS.PR_REQUESTS, JSON.stringify(list));
      return list[idx];
    }
    return null;
  }

  // --- STORE / EQUIPMENT REGISTER ---
  getStoreRecords() {
    const raw = localStorage.getItem(STORAGE_KEYS.STORE_RECORDS);
    if (!raw) return [];
    try {
      return JSON.parse(raw);
    } catch {
      return [];
    }
  }

  addStoreIssue(record) {
    const list = this.getStoreRecords();
    const newRecord = {
      id: Date.now(),
      equipmentName: record.equipmentName,
      equipmentSerialNo: record.equipmentSerialNo,
      issuedToCrewId: record.issuedToCrewId.toUpperCase(),
      issuedToCrewName: record.issuedToCrewName,
      designation: record.designation,
      category: record.category || 'LP',
      issueTime: record.issueTime || new Date().toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' }),
      issueDate: record.issueDate || new Date().toLocaleDateString('en-GB'),
      returnTime: null,
      returnDate: null,
      status: 'ISSUED', // 'ISSUED' | 'RETURNED'
      trainNo: record.trainNo || '',
      fromStation: record.fromStation || 'KHS',
      toStation: record.toStation || '',
      remarks: record.remarks || '',
      timestamp: Date.now()
    };
    list.unshift(newRecord);
    localStorage.setItem(STORAGE_KEYS.STORE_RECORDS, JSON.stringify(list));
    return newRecord;
  }

  returnStoreEquipment(id, returnDate, returnTime, remarks = '') {
    const list = this.getStoreRecords();
    const idx = list.findIndex(r => r.id === id);
    if (idx !== -1) {
      list[idx].status = 'RETURNED';
      list[idx].returnDate = returnDate || new Date().toLocaleDateString('en-GB');
      list[idx].returnTime = returnTime || new Date().toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' });
      if (remarks) list[idx].remarks += ` | Return: ${remarks}`;
      localStorage.setItem(STORAGE_KEYS.STORE_RECORDS, JSON.stringify(list));
      return list[idx];
    }
    return null;
  }

  // --- LONG HOUR DUTY UPDATES ---
  getLongHourRecords() {
    const raw = localStorage.getItem(STORAGE_KEYS.LONG_HOUR_RECORDS);
    if (!raw) return [];
    try {
      return JSON.parse(raw);
    } catch {
      return [];
    }
  }

  addLongHourRecord(record) {
    const list = this.getLongHourRecords();
    const newRecord = {
      id: Date.now(),
      lpgId: record.lpgId.toUpperCase(),
      lpgName: record.lpgName,
      alpId: record.alpId.toUpperCase(),
      alpName: record.alpName,
      trainNo: record.trainNo,
      locoNo: record.locoNo,
      signOnDate: record.signOnDate,
      signOnTime: record.signOnTime,
      direction: record.direction || 'UP',
      currentStationCode: record.currentStationCode || 'KHS',
      arrivalTimeCurrentStation: record.arrivalTimeCurrentStation || '',
      currentTrainPosition: record.currentTrainPosition || '1',
      positionTiming: record.positionTiming || '',
      isClosed: false,
      reliefDate: null,
      reliefTime: null,
      reliefStationCode: null,
      createdAt: new Date().toISOString()
    };
    list.unshift(newRecord);
    localStorage.setItem(STORAGE_KEYS.LONG_HOUR_RECORDS, JSON.stringify(list));
    return newRecord;
  }

  closeLongHourDuty(id, reliefStation, reliefTime, reliefDate) {
    const list = this.getLongHourRecords();
    const idx = list.findIndex(r => r.id === id);
    if (idx !== -1) {
      list[idx].isClosed = true;
      list[idx].reliefStationCode = reliefStation;
      list[idx].reliefTime = reliefTime;
      list[idx].reliefDate = reliefDate || new Date().toLocaleDateString('en-GB');
      list[idx].closedAt = new Date().toLocaleString('en-IN');
      localStorage.setItem(STORAGE_KEYS.LONG_HOUR_RECORDS, JSON.stringify(list));
      return list[idx];
    }
    return null;
  }

  // --- JEEP MOVEMENTS & FIFO AVAILABILITY ---
  static CORE_JEEPS = ["89", "89(ll)", "91", "22", "31", "79", "Breakdown"];
  static STATIONS = [
    { code: "KHS", name: "Kharsia" },
    { code: "JDI", name: "Jharadih" },
    { code: "SKT", name: "Sakti" },
    { code: "BUA", name: "Baradwar" },
    { code: "ROB", name: "Robertson" },
    { code: "BEF", name: "Bhupdevpur" },
    { code: "VWLR", name: "VWLR" },
    { code: "MONET", name: "Monet" },
    { code: "VIMLA", name: "Vimla" },
    { code: "BEMR", name: "BEMR" },
    { code: "GURA", name: "Gurda" },
    { code: "CHHL", name: "Chaal" },
    { code: "SLCC", name: "Chaal Silo" },
    { code: "GGDA", name: "Gharghoda" },
    { code: "KCHP", name: "Karichapar" },
    { code: "BOMK", name: "Bomka" },
    { code: "BAROD", name: "Barod" },
    { code: "DMJG", name: "Dharmjaygarh" },
    { code: "BUMA", name: "Bhalumuda" },
    { code: "RIG", name: "Raigarh" },
    { code: "BSP", name: "Bilaspur" },
    { code: "KDTR", name: "Kirodimal" },
    { code: "SGRD", name: "Saragaon" },
    { code: "OTHER", name: "Other" }
  ];

  getJeepMovements() {
    const raw = localStorage.getItem(STORAGE_KEYS.JEEP_MOVEMENTS);
    if (!raw) return [];
    try {
      return JSON.parse(raw);
    } catch {
      return [];
    }
  }

  addJeepMovement(record) {
    const list = this.getJeepMovements();
    const newRecord = {
      id: Date.now(),
      jeepNo: record.jeepNo,
      driverName: record.driverName,
      // Outward
      fromStation: record.fromStation || 'KHS',
      toStation: record.toStation || '',
      departureDate: record.departureDate,
      departureTime: record.departureTime,
      arrivalDate: record.arrivalDate,
      arrivalTime: record.arrivalTime,
      reliefTime: record.reliefTime,
      outwardCrews: record.outwardCrews || [],
      // Returning
      returningFromStation: record.returningFromStation || '',
      returningToStation: record.returningToStation || 'KHS',
      returningDepartureDate: record.returningDepartureDate || '',
      returningDepartureTime: record.returningDepartureTime || '',
      returningArrivalDate: record.returningArrivalDate || '',
      returningArrivalTime: record.returningArrivalTime || '',
      returningCrews: record.returningCrews || [],
      timestamp: Date.now()
    };
    list.unshift(newRecord);
    localStorage.setItem(STORAGE_KEYS.JEEP_MOVEMENTS, JSON.stringify(list));
    return newRecord;
  }

  getJeepAvailability() {
    const movements = this.getJeepMovements();
    const jeepStatusMap = {};

    // Initialize all core jeeps
    KharsiaStore.CORE_JEEPS.forEach(jeep => {
      jeepStatusMap[jeep] = {
        jeepNo: jeep,
        isAvailable: true,
        lastArrivalDate: 'Today',
        lastArrivalTime: '06:00',
        arrivalTimestamp: 1000,
        driverName: 'Regular Staff',
        currentLocation: 'KHS Lobby',
        statusDescription: 'Ready in Lobby'
      };
    });

    // Compute status from most recent movements
    movements.forEach(m => {
      if (jeepStatusMap[m.jeepNo]) {
        const item = jeepStatusMap[m.jeepNo];
        item.driverName = m.driverName || item.driverName;
        if (m.returningArrivalTime && m.returningToStation === 'KHS') {
          item.isAvailable = true;
          item.currentLocation = 'KHS Lobby';
          item.lastArrivalDate = m.returningArrivalDate || m.arrivalDate || 'Today';
          item.lastArrivalTime = m.returningArrivalTime;
          item.arrivalTimestamp = m.timestamp;
          item.statusDescription = `Arrived from ${m.returningFromStation || m.toStation}`;
        } else if (m.departureTime) {
          item.isAvailable = false;
          item.currentLocation = `Enroute to ${m.toStation}`;
          item.lastArrivalTime = m.arrivalTime || m.departureTime;
          item.arrivalTimestamp = m.timestamp;
          item.statusDescription = `Departed for ${m.toStation} at ${m.departureTime}`;
        }
      }
    });

    // Calculate FIFO turns for available jeeps
    const available = Object.values(jeepStatusMap).filter(j => j.isAvailable && j.jeepNo !== 'Breakdown');
    available.sort((a, b) => (a.arrivalTimestamp || 0) - (b.arrivalTimestamp || 0));
    available.forEach((j, idx) => {
      j.turnNumber = idx + 1;
    });

    return Object.values(jeepStatusMap);
  }

  // --- ROASTER & TLC UPDATE ---
  getRosterTlcRecords(date = null, shift = null) {
    const raw = localStorage.getItem(STORAGE_KEYS.ROSTER_TLC);
    let list = [];
    try {
      list = raw ? JSON.parse(raw) : [];
    } catch {
      list = [];
    }

    if (date) {
      list = list.filter(r => r.rosterDate === date);
    }
    if (shift) {
      list = list.filter(r => r.shiftTiming === shift);
    }
    return list;
  }

  saveRosterTlcRecord(record) {
    const list = this.getRosterTlcRecords();
    const existingIdx = list.findIndex(r => r.rosterDate === record.rosterDate && r.shiftTiming === record.shiftTiming);
    const newRecord = {
      ...record,
      id: record.id || Date.now(),
      timestamp: Date.now()
    };
    if (existingIdx >= 0) {
      list[existingIdx] = newRecord;
    } else {
      list.unshift(newRecord);
    }
    localStorage.setItem(STORAGE_KEYS.ROSTER_TLC, JSON.stringify(list));
    return newRecord;
  }

  // --- SEED INITIAL ACTIVE DATA MATCHING ANDROID ---
  _initSeedData() {
    if (!localStorage.getItem(STORAGE_KEYS.PR_REQUESTS)) {
      const initialPr = [
        {
          id: 1,
          crewId: "KHS1001",
          crewName: "ROHIT KU KURRE",
          designation: "Loco Pilot (Goods)",
          signOffDate: new Date().toLocaleDateString('en-GB'),
          signOffTime: "04:30",
          requestDate: new Date().toLocaleDateString('en-GB'),
          status: "Confirmed",
          remarks: "PR granted (30 hrs)",
          reviewedBy: "CLI Kharsia",
          reviewedAt: "Today 05:00 AM",
          timestamp: Date.now() - 10000000
        },
        {
          id: 2,
          crewId: "KHS1006",
          crewName: "RAKESH KR.RAJAK",
          designation: "Sr. Assistant Loco Pilot",
          signOffDate: new Date().toLocaleDateString('en-GB'),
          signOffTime: "07:15",
          requestDate: new Date().toLocaleDateString('en-GB'),
          status: "Pending",
          remarks: "",
          reviewedBy: null,
          reviewedAt: null,
          timestamp: Date.now() - 5000000
        }
      ];
      localStorage.setItem(STORAGE_KEYS.PR_REQUESTS, JSON.stringify(initialPr));
    }

    if (!localStorage.getItem(STORAGE_KEYS.STORE_RECORDS)) {
      const initialStore = [
        {
          id: 1,
          equipmentName: "Walkie-Talkie (VHF Set)",
          equipmentSerialNo: "WT-KHS-204",
          issuedToCrewId: "KHS1001",
          issuedToCrewName: "ROHIT KU KURRE",
          designation: "LPG",
          category: "LP",
          issueDate: new Date().toLocaleDateString('en-GB'),
          issueTime: "08:15",
          returnDate: null,
          returnTime: null,
          status: "ISSUED",
          trainNo: "N/BOXN",
          fromStation: "KHS",
          toStation: "RIG",
          remarks: "Battery 100%",
          timestamp: Date.now() - 12000000
        },
        {
          id: 2,
          equipmentName: "Tri-Color LED Torch",
          equipmentSerialNo: "TC-882",
          issuedToCrewId: "KHS1006",
          issuedToCrewName: "RAKESH KR.RAJAK",
          designation: "SALP",
          category: "ALP",
          issueDate: new Date().toLocaleDateString('en-GB'),
          issueTime: "08:20",
          returnDate: null,
          returnTime: null,
          status: "ISSUED",
          trainNo: "N/BOXN",
          fromStation: "KHS",
          toStation: "RIG",
          remarks: "Checked and verified",
          timestamp: Date.now() - 11000000
        }
      ];
      localStorage.setItem(STORAGE_KEYS.STORE_RECORDS, JSON.stringify(initialStore));
    }

    if (!localStorage.getItem(STORAGE_KEYS.LONG_HOUR_RECORDS)) {
      const initialLongHour = [
        {
          id: 1,
          lpgId: "KHS1003",
          lpgName: "SHANKAR LAL SIDAR",
          alpId: "KHS1008",
          alpName: "NITISH KUMAR",
          trainNo: "BCN/E",
          locoNo: "31456",
          signOnDate: new Date().toLocaleDateString('en-GB'),
          signOnTime: "03:30",
          direction: "UP",
          currentStationCode: "ROB",
          arrivalTimeCurrentStation: "08:10",
          currentTrainPosition: "3",
          positionTiming: "08:45",
          isClosed: false,
          reliefDate: null,
          reliefTime: null,
          reliefStationCode: null,
          createdAt: new Date().toISOString()
        }
      ];
      localStorage.setItem(STORAGE_KEYS.LONG_HOUR_RECORDS, JSON.stringify(initialLongHour));
    }

    if (!localStorage.getItem(STORAGE_KEYS.ROSTER_TLC)) {
      const today = new Date().toLocaleDateString('en-GB');
      const initialRoster = [
        {
          id: 1,
          rosterDate: today,
          shiftTiming: "06-14",
          tfrCrewName: "A K PRIYADARSHI",
          tfrMobile: "9752425843",
          lhCrewName: "A.M.KHAN",
          lhMobile: "9752442189",
          diCrewName: "ABHAY KUMAR",
          diMobile: "9752441631",
          wdCrewName: "ABHISHEK KUMAR",
          wdMobile: "7024219230",
          cmsName: "S. K. Verma",
          lobbyCliShift: "08-16",
          lobbyCliName: "CLI S. P. Patel",
          lobbyCliMobile: "9752411022",
          sanderBoyShift: "08-16",
          sanderBoyName: "Raju Yadav",
          tlcShift: "09-17",
          tlcMlName: "TLC Bilaspur Main",
          tlcMlMobile: "9752440101",
          tlcLhName: "TLC Bilaspur LH",
          tlcLhMobile: "9752440102",
          remarks: "Normal operations. 4 BOXN trains line up.",
          updatedByAdmin: "Lobby In-Charge",
          timestamp: Date.now()
        }
      ];
      localStorage.setItem(STORAGE_KEYS.ROSTER_TLC, JSON.stringify(initialRoster));
    }
  }
}

export const store = new KharsiaStore();
