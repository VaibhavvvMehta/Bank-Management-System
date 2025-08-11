import api from './api';

const accountService = {
  // Customer endpoints
  createAccount: (accountData) => api.post('/accounts', accountData),
  getMyAccounts: () => api.get('/accounts/my-accounts'),
  getMyActiveAccounts: () => api.get('/accounts/my-accounts/active'),
  getAccountById: (id) => api.get(`/accounts/${id}`),
  getAccountBalance: (id) => api.get(`/accounts/${id}/balance`),

  // Admin/Employee endpoints
  getAllAccounts: () => api.get('/accounts/all'),
  getAccountsByUserId: (userId) => api.get(`/accounts/user/${userId}`),
  getAccountsByStatus: (status) => api.get(`/accounts/status/${status}`),
  updateAccount: (id, accountData) => api.put(`/accounts/${id}`, accountData),
  updateAccountStatus: (id, status) => api.put(`/accounts/${id}/status`, { status }),
  deleteAccount: (id) => api.delete(`/accounts/${id}`),
  
  // Account types and status
  getAccountTypes: () => Promise.resolve(['SAVINGS', 'BUSINESS']),
  getAccountStatuses: () => Promise.resolve(['ACTIVE', 'SUSPENDED', 'CLOSED']),
};

export default accountService;

// Legacy exports for backward compatibility
export const getAccounts = accountService.getMyAccounts;
export const getActiveAccounts = accountService.getMyActiveAccounts;
export const getAccountById = accountService.getAccountById;
export const createAccount = accountService.createAccount;
export const getAccountBalance = accountService.getAccountBalance;
export const getAllAccounts = accountService.getAllAccounts;
export const getAccountsByUserId = accountService.getAccountsByUserId;
export const getAccountsByStatus = accountService.getAccountsByStatus;
export const updateAccount = accountService.updateAccount;
export const updateAccountStatus = accountService.updateAccountStatus;
export const deleteAccount = accountService.deleteAccount;
