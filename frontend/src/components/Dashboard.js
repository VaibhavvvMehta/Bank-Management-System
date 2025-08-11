import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  CircularProgress,
  Alert,
  Chip,
  Divider,
  List,
  ListItem,
  ListItemText,
  Button
} from '@mui/material';
import {
  AccountBalance,
  TrendingUp,
  Receipt,
  Person,
  AdminPanelSettings,
  SupervisorAccount,
  AttachMoney,
  CreditCard,
  SwapHoriz,
  Groups,
  Assessment,
  Security
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import userService from '../services/userService';

const Dashboard = () => {
  const { user, isAdmin, isEmployee, isCustomer } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [adminStats, setAdminStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadDashboardData();
  }, [user]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      
      if (isCustomer()) {
        // Load customer data
        const [accountsRes, transactionsRes] = await Promise.all([
          userService.getMyAccounts(),
          userService.getMyTransactions()
        ]);
        
        setAccounts(accountsRes.data);
        setTransactions(transactionsRes.data.slice(0, 5)); // Latest 5 transactions
      } else if (isAdmin() || isEmployee()) {
        // Load admin/employee data
        const [accountsRes, transactionsRes, statsRes] = await Promise.all([
          userService.getAllAccounts(),
          userService.getAllTransactions(),
          isAdmin() ? userService.getSystemStats() : Promise.resolve({ data: null })
        ]);
        
        setAccounts(accountsRes.data);
        setTransactions(transactionsRes.data.slice(0, 10)); // Latest 10 for staff
        if (isAdmin()) {
          setAdminStats(statsRes.data);
        }
      }
    } catch (err) {
      setError('Failed to load dashboard data');
      console.error('Dashboard error:', err);
    } finally {
      setLoading(false);
    }
  };

  const calculateTotalBalance = () => {
    return accounts.reduce((total, account) => total + parseFloat(account.balance || 0), 0);
  };

  const getRecentTransactions = () => {
    return transactions.slice(0, 5);
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  };

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'ACTIVE': return 'success';
      case 'COMPLETED': return 'success';
      case 'PENDING': return 'warning';
      case 'SUSPENDED': return 'error';
      case 'CANCELLED': return 'error';
      case 'CLOSED': return 'error';
      default: return 'default';
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <CircularProgress size={60} />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Welcome, {user?.firstName} {user?.lastName}!
      </Typography>
      
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Summary Cards */}
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <AccountBalance color="primary" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Accounts
                  </Typography>
                  <Typography variant="h4">
                    {accounts.length}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <TrendingUp color="success" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Balance
                  </Typography>
                  <Typography variant="h4">
                    ₹{calculateTotalBalance().toFixed(2)}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Receipt color="secondary" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Transactions
                  </Typography>
                  <Typography variant="h4">
                    {transactions.length}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Person color="info" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Account Status
                  </Typography>
                  <Typography variant="h4">
                    Active
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Transactions */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Transactions
              </Typography>
              {getRecentTransactions().length === 0 ? (
                <Typography color="textSecondary">
                  No transactions yet
                </Typography>
              ) : (
                getRecentTransactions().map((transaction) => (
                  <Box
                    key={transaction.id}
                    display="flex"
                    justifyContent="space-between"
                    alignItems="center"
                    sx={{ py: 1, borderBottom: '1px solid #eee' }}
                  >
                    <Box>
                      <Typography variant="body1">
                        {transaction.transactionType}
                      </Typography>
                      <Typography variant="body2" color="textSecondary">
                        {transaction.description}
                      </Typography>
                    </Box>
                    <Typography variant="body1" color={
                      transaction.transactionType === 'DEPOSIT' ? 'success.main' : 'error.main'
                    }>
                      ₹{transaction.amount}
                    </Typography>
                  </Box>
                ))
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Dashboard;
