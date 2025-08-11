import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Button,
  Chip,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Tooltip
} from '@mui/material';
import {
  Add,
  AccountBalance,
  Visibility,
  Edit,
  Delete,
  AttachMoney,
  Receipt,
  SwapHoriz,
  Block,
  CheckCircle
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import userService from '../services/userService';
import TransactionForm from '../components/TransactionForm';

const Accounts = () => {
  const { user, isAdmin, isEmployee, isCustomer } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [transactionDialogOpen, setTransactionDialogOpen] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [transactionType, setTransactionType] = useState('DEPOSIT');
  const [newAccount, setNewAccount] = useState({
    accountType: 'SAVINGS'
  });

  useEffect(() => {
    loadAccounts();
  }, []);

  const loadAccounts = async () => {
    try {
      setLoading(true);
      let response;
      
      if (isCustomer()) {
        response = await userService.getMyAccounts();
      } else {
        response = await userService.getAllAccounts();
      }
      
      setAccounts(response.data);
    } catch (err) {
      setError('Failed to load accounts');
      console.error('Load accounts error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAccount = async () => {
    try {
      setLoading(true);
      await userService.createAccount(newAccount);
      setCreateDialogOpen(false);
      setNewAccount({ accountType: 'SAVINGS' });
      loadAccounts();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create account');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateAccountStatus = async (accountId, status) => {
    try {
      await userService.updateAccountStatus(accountId, status);
      loadAccounts();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update account status');
    }
  };

  const handleDeleteAccount = async (accountId) => {
    if (window.confirm('Are you sure you want to delete this account?')) {
      try {
        await userService.deleteAccount(accountId);
        loadAccounts();
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to delete account');
      }
    }
  };

  const openTransactionDialog = (account, type) => {
    setSelectedAccount(account);
    setTransactionType(type);
    setTransactionDialogOpen(true);
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount || 0);
  };

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'ACTIVE': return 'success';
      case 'SUSPENDED': return 'warning';
      case 'CLOSED': return 'error';
      default: return 'default';
    }
  };

  const getAccountTypeIcon = (type) => {
    switch (type?.toUpperCase()) {
      case 'SAVINGS': return <AccountBalance color="primary" />;
      case 'BUSINESS': return <AttachMoney color="success" />;
      default: return <AccountBalance />;
    }
  };

  if (loading && accounts.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <CircularProgress size={60} />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">
          {isCustomer() ? 'My Accounts' : 'Account Management'}
        </Typography>
        
        {isCustomer() && (
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setCreateDialogOpen(true)}
          >
            Create Account
          </Button>
        )}
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {/* Account Cards for Customer View */}
      {isCustomer() && (
        <Grid container spacing={3} sx={{ mb: 4 }}>
          {accounts.map((account) => (
            <Grid item xs={12} md={6} lg={4} key={account.id}>
              <Card>
                <CardContent>
                  <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
                    {getAccountTypeIcon(account.accountType)}
                    <Chip
                      label={account.accountStatus}
                      color={getStatusColor(account.accountStatus)}
                      size="small"
                    />
                  </Box>
                  
                  <Typography variant="h6" gutterBottom>
                    {account.accountType} Account
                  </Typography>
                  
                  <Typography variant="body2" color="textSecondary" gutterBottom>
                    Account Number: {account.accountNumber}
                  </Typography>
                  
                  <Typography variant="h4" color="primary" gutterBottom>
                    {formatCurrency(account.balance)}
                  </Typography>
                  
                  <Box display="flex" gap={1} mt={2}>
                    <Button
                      size="small"
                      variant="outlined"
                      startIcon={<AttachMoney />}
                      onClick={() => openTransactionDialog(account, 'DEPOSIT')}
                      disabled={account.accountStatus !== 'ACTIVE'}
                    >
                      Deposit
                    </Button>
                    <Button
                      size="small"
                      variant="outlined"
                      startIcon={<Receipt />}
                      onClick={() => openTransactionDialog(account, 'WITHDRAWAL')}
                      disabled={account.accountStatus !== 'ACTIVE'}
                    >
                      Withdraw
                    </Button>
                    <Button
                      size="small"
                      variant="outlined"
                      startIcon={<SwapHoriz />}
                      onClick={() => openTransactionDialog(account, 'TRANSFER')}
                      disabled={account.accountStatus !== 'ACTIVE'}
                    >
                      Transfer
                    </Button>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Table View for Admin/Employee */}
      {(isAdmin() || isEmployee()) && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Account Number</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Owner</TableCell>
                <TableCell>Balance</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Created</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {accounts.map((account) => (
                <TableRow key={account.id}>
                  <TableCell>
                    <Box display="flex" alignItems="center" gap={1}>
                      {getAccountTypeIcon(account.accountType)}
                      {account.accountNumber}
                    </Box>
                  </TableCell>
                  <TableCell>{account.accountType}</TableCell>
                  <TableCell>
                    {account.user ? 
                      `${account.user.firstName} ${account.user.lastName}` : 
                      'N/A'
                    }
                  </TableCell>
                  <TableCell>{formatCurrency(account.balance)}</TableCell>
                  <TableCell>
                    <Chip
                      label={account.accountStatus}
                      color={getStatusColor(account.accountStatus)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    {new Date(account.createdAt).toLocaleDateString()}
                  </TableCell>
                  <TableCell>
                    <Box display="flex" gap={1}>
                      {account.accountStatus === 'ACTIVE' ? (
                        <Tooltip title="Suspend Account">
                          <IconButton
                            size="small"
                            color="warning"
                            onClick={() => handleUpdateAccountStatus(account.id, 'SUSPENDED')}
                          >
                            <Block />
                          </IconButton>
                        </Tooltip>
                      ) : (
                        <Tooltip title="Activate Account">
                          <IconButton
                            size="small"
                            color="success"
                            onClick={() => handleUpdateAccountStatus(account.id, 'ACTIVE')}
                          >
                            <CheckCircle />
                          </IconButton>
                        </Tooltip>
                      )}
                      
                      {isAdmin() && (
                        <Tooltip title="Delete Account">
                          <IconButton
                            size="small"
                            color="error"
                            onClick={() => handleDeleteAccount(account.id)}
                          >
                            <Delete />
                          </IconButton>
                        </Tooltip>
                      )}
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Create Account Dialog */}
      <Dialog open={createDialogOpen} onClose={() => setCreateDialogOpen(false)}>
        <DialogTitle>Create New Account</DialogTitle>
        <DialogContent>
          <FormControl fullWidth sx={{ mt: 2 }}>
            <InputLabel>Account Type</InputLabel>
            <Select
              value={newAccount.accountType}
              onChange={(e) => setNewAccount({ ...newAccount, accountType: e.target.value })}
              label="Account Type"
            >
              <MenuItem value="SAVINGS">Savings Account</MenuItem>
              <MenuItem value="BUSINESS">Business Account</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleCreateAccount}>
            Create Account
          </Button>
        </DialogActions>
      </Dialog>

      {/* Transaction Dialog */}
      <TransactionForm
        open={transactionDialogOpen}
        onClose={() => setTransactionDialogOpen(false)}
        onSuccess={() => {
          loadAccounts();
          setTransactionDialogOpen(false);
        }}
        transactionType={transactionType}
      />
    </Container>
  );
};

export default Accounts;
