import api from './api';

const transactionService = {
  // Get my transactions (customer transactions)
  getMyTransactions: async () => {
    try {
      const response = await api.get('/transactions/my-transactions');
      return response.data;
    } catch (error) {
      console.error('Error fetching my transactions:', error);
      throw error;
    }
  },

  // Get user transactions (alias for customer dashboard)
  getUserTransactions: async () => {
    try {
      const response = await api.get('/transactions/my-transactions');
      return response.data;
    } catch (error) {
      console.error('Error fetching user transactions:', error);
      throw error;
    }
  },

  // Get all transactions (admin/employee only)
  getAllTransactionsAdmin: async () => {
    try {
      const response = await api.get('/transactions/all');
      return response.data;
    } catch (error) {
      console.error('Error fetching all transactions:', error);
      throw error;
    }
  },

  // Get all transactions for admin (main method for admin)
  getAllTransactions: async () => {
    try {
      const response = await api.get('/transactions/all');
      return response.data;
    } catch (error) {
      console.error('Error fetching all transactions:', error);
      throw error;
    }
  },

  // Get transactions by account ID
  getTransactionsByAccount: async (accountId) => {
    try {
      const response = await api.get(`/transactions/account/${accountId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching transactions by account:', error);
      throw error;
    }
  },

  // Get transactions by date range
  getTransactionsByDateRange: async (accountId, startDate, endDate) => {
    try {
      const response = await api.get(`/transactions/account/${accountId}/date-range`, {
        params: { startDate, endDate }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching transactions by date range:', error);
      throw error;
    }
  },

  // Get transaction by ID
  getTransactionById: async (transactionId) => {
    try {
      const response = await api.get(`/transactions/${transactionId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching transaction:', error);
      throw error;
    }
  },

  // Transfer money between accounts
  transferMoney: async (transferData) => {
    try {
      const response = await api.post('/transactions/transfer', transferData);
      return response.data;
    } catch (error) {
      console.error('Error transferring money:', error);
      throw error;
    }
  },

  // Deposit money
  deposit: async (depositData) => {
    try {
      const response = await api.post('/transactions/deposit', depositData);
      return response.data;
    } catch (error) {
      console.error('Error depositing money:', error);
      throw error;
    }
  },

  // Create deposit (alias for backward compatibility)
  createDeposit: async (depositData) => {
    try {
      const response = await api.post('/transactions/deposit', depositData);
      return response.data;
    } catch (error) {
      console.error('Error depositing money:', error);
      throw error;
    }
  },

  // Withdraw money
  withdraw: async (withdrawData) => {
    try {
      const response = await api.post('/transactions/withdrawal', withdrawData);
      return response.data;
    } catch (error) {
      console.error('Error withdrawing money:', error);
      throw error;
    }
  },

  // Create withdrawal (alias for backward compatibility)
  createWithdrawal: async (withdrawData) => {
    try {
      const response = await api.post('/transactions/withdrawal', withdrawData);
      return response.data;
    } catch (error) {
      console.error('Error withdrawing money:', error);
      throw error;
    }
  },

  // Create transfer (alias for backward compatibility)
  createTransfer: async (transferData) => {
    try {
      const response = await api.post('/transactions/transfer', transferData);
      return response.data;
    } catch (error) {
      console.error('Error transferring money:', error);
      throw error;
    }
  },

  // Get transaction limits
  getTransactionLimits: async () => {
    try {
      const response = await api.get('/transactions/limits');
      return response.data;
    } catch (error) {
      console.error('Error fetching transaction limits:', error);
      throw error;
    }
  }
};

export default transactionService;
