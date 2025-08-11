import api from './api';

const adminService = {
  // Dashboard statistics
  getDashboardStats: async () => {
    try {
      const response = await api.get('/admin/dashboard/stats');
      return response.data;
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
      throw error;
    }
  },

  // User management
  getAllUsers: async () => {
    try {
      const response = await api.get('/admin/users');
      return response.data;
    } catch (error) {
      console.error('Error fetching all users:', error);
      throw error;
    }
  },

  getActiveUsers: async () => {
    try {
      const response = await api.get('/admin/users/active');
      return response.data;
    } catch (error) {
      console.error('Error fetching active users:', error);
      throw error;
    }
  },

  getInactiveUsers: async () => {
    try {
      const response = await api.get('/admin/users/inactive');
      return response.data;
    } catch (error) {
      console.error('Error fetching inactive users:', error);
      throw error;
    }
  },

  searchUsers: async (name) => {
    try {
      const response = await api.get(`/admin/users/search?name=${name}`);
      return response.data;
    } catch (error) {
      console.error('Error searching users:', error);
      throw error;
    }
  },

  updateUser: async (id, userDetails) => {
    try {
      const response = await api.put(`/admin/users/${id}`, userDetails);
      return response.data;
    } catch (error) {
      console.error('Error updating user:', error);
      throw error;
    }
  },

  activateUser: async (id) => {
    try {
      const response = await api.put(`/admin/users/${id}/activate`);
      return response.data;
    } catch (error) {
      console.error('Error activating user:', error);
      throw error;
    }
  },

  deactivateUser: async (id) => {
    try {
      const response = await api.put(`/admin/users/${id}/deactivate`);
      return response.data;
    } catch (error) {
      console.error('Error deactivating user:', error);
      throw error;
    }
  },

  updateUserRole: async (id, role) => {
    try {
      const response = await api.put(`/admin/users/${id}/role`, { role });
      return response.data;
    } catch (error) {
      console.error('Error updating user role:', error);
      throw error;
    }
  },

  deleteUser: async (id) => {
    try {
      const response = await api.delete(`/admin/users/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error deleting user:', error);
      throw error;
    }
  },

  // Account management
  getAdminAccounts: async () => {
    try {
      const response = await api.get('/admin/accounts');
      return response.data;
    } catch (error) {
      console.error('Error fetching admin accounts:', error);
      throw error;
    }
  },

  // Transaction management
  getAdminTransactions: async () => {
    try {
      const response = await api.get('/admin/transactions');
      return response.data;
    } catch (error) {
      console.error('Error fetching admin transactions:', error);
      throw error;
    }
  },

  // System operations
  createEmployee: async (employeeData) => {
    try {
      const response = await api.post('/admin/create-employee', employeeData);
      return response.data;
    } catch (error) {
      console.error('Error creating employee:', error);
      throw error;
    }
  },

  createAdmin: async (adminData) => {
    try {
      const response = await api.post('/admin/create-admin', adminData);
      return response.data;
    } catch (error) {
      console.error('Error creating admin:', error);
      throw error;
    }
  }
};

export default adminService;
