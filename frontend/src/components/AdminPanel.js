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
  Tabs,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Chip,
  IconButton
} from '@mui/material';
import {
  People,
  AccountBalance,
  Receipt,
  TrendingUp,
  Edit,
  Delete,
  Block,
  CheckCircle
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import userService from '../services/userService';
import adminService from '../services/adminService';

const AdminPanel = () => {
  const { isAdmin } = useAuth();
  const [currentTab, setCurrentTab] = useState(0);
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isAdmin()) {
      loadAdminData();
    }
  }, [isAdmin]);

  const loadAdminData = async () => {
    try {
      const [statsRes, usersRes, accountsRes, transactionsRes] = await Promise.all([
        adminService.getDashboardStats(),
        adminService.getAllUsers(),
        adminService.getAdminAccounts(),
        adminService.getAdminTransactions()
      ]);
      
      setStats(statsRes);
      setUsers(usersRes);
      setAccounts(accountsRes);
      setTransactions(transactionsRes);
    } catch (err) {
      setError('Failed to load admin data');
      console.error('Admin data loading error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleUserAction = async (userId, action) => {
    try {
      switch (action) {
        case 'activate':
          await adminService.activateUser(userId);
          break;
        case 'deactivate':
          await adminService.deactivateUser(userId);
          break;
        case 'delete':
          await adminService.deleteUser(userId);
          break;
        default:
          return;
      }
      loadAdminData(); // Reload data
    } catch (err) {
      setError(`Failed to ${action} user`);
      console.error(`Error ${action} user:`, err);
    }
  };

  const StatCard = ({ title, value, icon, color = 'primary' }) => (
    <Card>
      <CardContent>
        <Box display="flex" alignItems="center" justifyContent="space-between">
          <Box>
            <Typography color="textSecondary" gutterBottom>
              {title}
            </Typography>
            <Typography variant="h4" component="div">
              {value}
            </Typography>
          </Box>
          <Box color={`${color}.main`}>
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );

  const UsersTab = () => (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Email</TableCell>
            <TableCell>Role</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.id}>
              <TableCell>{`${user.firstName} ${user.lastName}`}</TableCell>
              <TableCell>{user.email}</TableCell>
              <TableCell>
                <Chip 
                  label={user.role} 
                  color={user.role === 'ADMIN' ? 'error' : user.role === 'EMPLOYEE' ? 'warning' : 'default'}
                  size="small"
                />
              </TableCell>
              <TableCell>
                <Chip 
                  label={user.active ? 'Active' : 'Inactive'} 
                  color={user.active ? 'success' : 'default'}
                  size="small"
                />
              </TableCell>
              <TableCell>
                {user.active ? (
                  <IconButton 
                    onClick={() => handleUserAction(user.id, 'deactivate')}
                    color="warning"
                    size="small"
                  >
                    <Block />
                  </IconButton>
                ) : (
                  <IconButton 
                    onClick={() => handleUserAction(user.id, 'activate')}
                    color="success"
                    size="small"
                  >
                    <CheckCircle />
                  </IconButton>
                )}
                <IconButton 
                  onClick={() => handleUserAction(user.id, 'delete')}
                  color="error"
                  size="small"
                >
                  <Delete />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );

  const AccountsTab = () => (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Account Number</TableCell>
            <TableCell>Owner</TableCell>
            <TableCell>Type</TableCell>
            <TableCell>Balance</TableCell>
            <TableCell>Status</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {accounts.map((account) => (
            <TableRow key={account.id}>
              <TableCell>{account.accountNumber}</TableCell>
              <TableCell>{account.user ? `${account.user.firstName} ${account.user.lastName}` : 'N/A'}</TableCell>
              <TableCell>{account.accountType}</TableCell>
              <TableCell>₹{account.balance}</TableCell>
              <TableCell>
                <Chip 
                  label={account.accountStatus} 
                  color={account.accountStatus === 'ACTIVE' ? 'success' : 'default'}
                  size="small"
                />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );

  const TransactionsTab = () => (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Reference</TableCell>
            <TableCell>Type</TableCell>
            <TableCell>Amount</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Date</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {transactions.slice(0, 100).map((transaction) => (
            <TableRow key={transaction.id}>
              <TableCell>{transaction.transactionReference}</TableCell>
              <TableCell>{transaction.transactionType}</TableCell>
              <TableCell>₹{transaction.amount}</TableCell>
              <TableCell>
                <Chip 
                  label={transaction.transactionStatus} 
                  color={transaction.transactionStatus === 'COMPLETED' ? 'success' : 'default'}
                  size="small"
                />
              </TableCell>
              <TableCell>
                {new Date(transaction.createdAt).toLocaleDateString()}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );

  if (!isAdmin()) {
    return (
      <Container maxWidth="lg">
        <Alert severity="error">
          Access denied. Admin privileges required.
        </Alert>
      </Container>
    );
  }

  if (loading) {
    return (
      <Container maxWidth="lg">
        <Box display="flex" justifyContent="center" mt={4}>
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Admin Panel
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {stats && (
        <Grid container spacing={3} sx={{ mb: 3 }}>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard 
              title="Total Users" 
              value={stats.totalUsers} 
              icon={<People />}
              color="primary"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard 
              title="Active Accounts" 
              value={stats.activeAccounts} 
              icon={<AccountBalance />}
              color="success"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard 
              title="Total Transactions" 
              value={stats.totalTransactions} 
              icon={<Receipt />}
              color="info"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard 
              title="Pending Transactions" 
              value={stats.pendingTransactions} 
              icon={<TrendingUp />}
              color="warning"
            />
          </Grid>
        </Grid>
      )}

      <Card>
        <CardContent>
          <Tabs value={currentTab} onChange={(e, newValue) => setCurrentTab(newValue)}>
            <Tab label="Users" />
            <Tab label="Accounts" />
            <Tab label="Transactions" />
          </Tabs>
          
          <Box mt={3}>
            {currentTab === 0 && <UsersTab />}
            {currentTab === 1 && <AccountsTab />}
            {currentTab === 2 && <TransactionsTab />}
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
};

export default AdminPanel;
