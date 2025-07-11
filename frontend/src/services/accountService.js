import api from './api';

export const getAccounts = () => {
  return api.get('/customer/accounts');
};

export const getAccountById = (id) => {
  return api.get(`/customer/accounts/${id}`);
};

export const createAccount = (accountData) => {
  return api.post('/customer/accounts', accountData);
};

export const updateAccount = (id, accountData) => {
  return api.put(`/customer/accounts/${id}`, accountData);
};

export const deleteAccount = (id) => {
  return api.delete(`/customer/accounts/${id}`);
};

export const getAccountBalance = (id) => {
  return api.get(`/customer/accounts/${id}/balance`);
};
