import api from './api';

export const login = (credentials) => {
  return api.post('/auth/login', credentials);
};

export const register = (userData) => {
  return api.post('/auth/register', userData);
};

export const getCurrentUser = () => {
  return api.get('/auth/user');
};

export const changePassword = (passwordData) => {
  return api.put('/auth/change-password', passwordData);
};
