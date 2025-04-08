import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

// ✅ AUTH
export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
};

// ✅ ADMIN
export const adminService = {
  getAllUsers: () => api.get('/admin/users'),
  getUserById: (id) => api.get(`/admin/users/${id}`),
  updateUser: (id, data) => api.put(`/admin/users/${id}`, data),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),

  getAllTournaments: () => api.get('/admin/tournaments'),
  createTournament: (data) => api.post('/admin/tournaments', data),
  updateTournament: (id, data) => api.put(`/admin/tournaments/${id}`, data),
  deleteTournament: (id) => api.delete(`/admin/tournaments/${id}`),
  generateMatches: (id) => api.post(`/admin/tournaments/${id}/generate-matches`),
  assignRefereesToTournament: (tournamentId, refereeIds) =>
    api.post(`/admin/tournaments/${tournamentId}/assign-referees`, refereeIds),
  

  deleteMatch: (id) => api.delete(`/admin/matches/${id}`),
  exportAllMatchesCsv: () => api.get('/admin/matches/export/csv', { responseType: 'blob' }),
  exportAllMatchesTxt: () => api.get('/admin/matches/export/txt', { responseType: 'blob' }),
};

// ✅ PLAYER
export const playerService = {
  registerForTournament: (tournamentId) =>
    api.post('/player/register-tournament', { tournamentId }),
  getSchedule: () => api.get('/player/schedule'),
  getScores: () => api.get('/player/scores'),
  getPlayerMatches: () => api.get('/player/matches'),
  getAvailableTournaments: () => api.get('/player/available-tournaments'),
  getTournamentMatches: (tournamentId) => api.get(`auth/tournament/${tournamentId}/matches`),
  getTournamentScores: (tournamentId) => api.get(`auth/tournament/${tournamentId}/scores`),
  getMatchScore: (matchId) =>
    api.get(`/player/matches/${matchId}/score`),
  
};

// ✅ REFEREE
export const refereeService = {
  getAssignedMatches: () => api.get('/referee/matches'),
  updateMatchScore: (matchId, scoreData) =>
    api.post(`/referee/matches/${matchId}/score`, scoreData),
  submitMatchScore: (matchId, scoreData) => 
    api.post(`/referee/matches/${matchId}/score`, scoreData),
  getMatchScore: (matchId) =>
    api.get(`/referee/matches/${matchId}/score`),
  
};

// ✅ USER
export const userService = {
  getMe: () => api.get('/users/me'),
  updateMe: (data) => api.put('/users/me/update', data),
};

export default api;
