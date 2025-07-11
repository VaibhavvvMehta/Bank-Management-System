import api from './api';

export const getTransactions = () => {
  return api.get('/customer/transactions');
};

export const getTransactionById = (id) => {
  return api.get(`/customer/transactions/${id}`);
};

export const createDeposit = (transactionData) => {
  return api.post('/customer/transactions/deposit', transactionData);
};

export const createWithdrawal = (transactionData) => {
  return api.post('/customer/transactions/withdrawal', transactionData);
};

export const createTransfer = (transactionData) => {
  return api.post('/customer/transactions/transfer', transactionData);
};

export const getTransactionsByAccount = (accountId) => {
  return api.get(`/customer/transactions/account/${accountId}`);
};

export const getTransactionsByDateRange = (accountId, startDate, endDate) => {
  return api.get(`/customer/transactions/account/${accountId}/date-range`, {
    params: { startDate, endDate }
  });
};
