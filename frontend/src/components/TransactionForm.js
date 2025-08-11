import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Typography,
  Box,
  Alert,
  Grid,
  Card,
  CardContent,
  Chip
} from '@mui/material';
import {
  AttachMoney,
  SwapHoriz,
  AccountBalance,
  Receipt
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import accountService from '../services/accountService';
import transactionService from '../services/transactionService';

const TransactionForm = ({ open, onClose, onSuccess, transactionType = 'DEPOSIT' }) => {
  const { user } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [limits, setLimits] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState({
    amount: '',
    description: '',
    accountId: '',
    fromAccountId: '',
    toAccountId: '',
    toAccountNumber: ''
  });

  useEffect(() => {
    if (open) {
      loadUserAccounts();
      loadTransactionLimits();
      resetForm();
    }
  }, [open]);

  const loadUserAccounts = async () => {
    try {
      const response = await accountService.getMyActiveAccounts();
      setAccounts(response.data);
      if (response.data.length > 0) {
        setFormData(prev => ({
          ...prev,
          accountId: response.data[0].id,
          fromAccountId: response.data[0].id
        }));
      }
    } catch (err) {
      setError('Failed to load accounts');
    }
  };

  const loadTransactionLimits = async () => {
    try {
      const response = await transactionService.getTransactionLimits();
      setLimits(response);
    } catch (err) {
      console.error('Failed to load transaction limits:', err);
    }
  };

  const resetForm = () => {
    setFormData({
      amount: '',
      description: '',
      accountId: '',
      fromAccountId: '',
      toAccountId: '',
      toAccountNumber: ''
    });
    setError('');
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const validateForm = () => {
    if (!formData.amount || parseFloat(formData.amount) <= 0) {
      setError('Please enter a valid amount');
      return false;
    }

    if (transactionType === 'DEPOSIT' || transactionType === 'WITHDRAWAL') {
      if (!formData.accountId) {
        setError('Please select an account');
        return false;
      }
    }

    if (transactionType === 'TRANSFER') {
      if (!formData.fromAccountId) {
        setError('Please select a source account');
        return false;
      }
      if (!formData.toAccountNumber || formData.toAccountNumber.trim() === '') {
        setError('Please enter destination account number');
        return false;
      }
      // Check if user is trying to transfer to their own account
      const sourceAccount = accounts.find(acc => acc.id.toString() === formData.fromAccountId);
      if (sourceAccount && sourceAccount.accountNumber === formData.toAccountNumber.trim()) {
        setError('Cannot transfer to the same account');
        return false;
      }
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setLoading(true);
    setError('');

    try {
      let response;
      const transactionData = {
        amount: parseFloat(formData.amount),
        description: formData.description || getDefaultDescription()
      };

      switch (transactionType) {
        case 'DEPOSIT':
          response = await transactionService.createDeposit({
            ...transactionData,
            accountId: formData.accountId
          });
          break;
        
        case 'WITHDRAWAL':
          response = await transactionService.createWithdrawal({
            ...transactionData,
            accountId: formData.accountId
          });
          break;
        
        case 'TRANSFER':
          // For transfers, we send the account number and let backend resolve the ID
          response = await transactionService.createTransfer({
            fromAccountId: parseInt(formData.fromAccountId),
            toAccountNumber: formData.toAccountNumber,
            amount: parseFloat(formData.amount),
            description: formData.description || getDefaultDescription()
          });
          break;
        
        default:
          throw new Error('Invalid transaction type');
      }

      onSuccess(response.data);
      onClose();
    } catch (err) {
      // Enhanced error handling for better user experience
      let errorMessage = `${transactionType.toLowerCase()} failed`;
      
      if (err.response?.data) {
        const errorData = err.response.data;
        errorMessage = errorData.message || errorMessage;
        
        // Add helpful tips based on error type
        if (errorData.errorCode === 'INSUFFICIENT_BALANCE') {
          errorMessage += '\n💡 Tip: Check your account balance before making transactions.';
        } else if (errorData.errorCode === 'INVALID_AMOUNT') {
          errorMessage += '\n💡 Tip: Amount must be positive and not exceed limits.';
        } else if (errorData.errorCode === 'DAILY_LIMIT_EXCEEDED') {
          errorMessage += '\n💡 Tip: Try again tomorrow or contact your bank for higher limits.';
        } else if (errorData.errorCode === 'ACCOUNT_NOT_ACTIVE') {
          errorMessage += '\n💡 Tip: Please contact customer service to activate your account.';
        }
      }
      
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const getDefaultDescription = () => {
    switch (transactionType) {
      case 'DEPOSIT': return 'Account deposit';
      case 'WITHDRAWAL': return 'Account withdrawal';
      case 'TRANSFER': return 'Account transfer';
      default: return 'Transaction';
    }
  };

  const getFormTitle = () => {
    switch (transactionType) {
      case 'DEPOSIT': return 'Make Deposit';
      case 'WITHDRAWAL': return 'Make Withdrawal';
      case 'TRANSFER': return 'Transfer Funds';
      default: return 'Transaction';
    }
  };

  const getFormIcon = () => {
    switch (transactionType) {
      case 'DEPOSIT': return <AttachMoney color="success" />;
      case 'WITHDRAWAL': return <Receipt color="warning" />;
      case 'TRANSFER': return <SwapHoriz color="primary" />;
      default: return <AccountBalance />;
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>
        <Box display="flex" alignItems="center" gap={1}>
          {getFormIcon()}
          <Typography variant="h6">{getFormTitle()}</Typography>
        </Box>
      </DialogTitle>
      
      <form onSubmit={handleSubmit}>
        <DialogContent>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Grid container spacing={3}>
            {/* Amount Field */}
            <Grid item xs={12} md={6}>
              <TextField
                name="amount"
                label="Amount"
                type="number"
                value={formData.amount}
                onChange={handleInputChange}
                fullWidth
                required
                inputProps={{ min: "0.01", step: "0.01" }}
                InputProps={{
                  startAdornment: <Typography sx={{ mr: 1 }}>₹</Typography>
                }}
              />
            </Grid>

            {/* Transaction Limits Display */}
            {limits && (
              <Grid item xs={12} md={6}>
                <Card sx={{ bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
                  <CardContent sx={{ p: 2 }}>
                    <Typography variant="subtitle2" gutterBottom color="primary">
                      Transaction Limits
                    </Typography>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                      <Typography variant="body2">
                        Min Amount: <Chip label={`₹${limits.minTransactionAmount}`} size="small" color="default" />
                      </Typography>
                      <Typography variant="body2">
                        Max Single: <Chip label={`₹${limits.maxSingleTransaction}`} size="small" color="warning" />
                      </Typography>
                      {(transactionType === 'WITHDRAWAL') && (
                        <Typography variant="body2">
                          Daily Limit: <Chip label={`₹${limits.dailyWithdrawalLimit}`} size="small" color="error" />
                        </Typography>
                      )}
                      {(transactionType === 'TRANSFER') && (
                        <Typography variant="body2">
                          Daily Limit: <Chip label={`₹${limits.dailyTransferLimit}`} size="small" color="error" />
                        </Typography>
                      )}
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            )}

            {/* Account Selection for Deposit/Withdrawal */}
            {(transactionType === 'DEPOSIT' || transactionType === 'WITHDRAWAL') && (
              <Grid item xs={12} md={6}>
                <FormControl fullWidth required>
                  <InputLabel>Account</InputLabel>
                  <Select
                    name="accountId"
                    value={formData.accountId}
                    onChange={handleInputChange}
                    label="Account"
                  >
                    {accounts.map((account) => (
                      <MenuItem key={account.id} value={account.id}>
                        <Box>
                          <Typography variant="body1">
                            {account.accountNumber} - {account.accountType}
                          </Typography>
                          <Typography variant="body2" color="textSecondary">
                            Balance: ₹{parseFloat(account.balance).toFixed(2)}
                          </Typography>
                        </Box>
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}

            {/* Transfer Fields */}
            {transactionType === 'TRANSFER' && (
              <>
                <Grid item xs={12} md={6}>
                  <FormControl fullWidth required>
                    <InputLabel>From Account</InputLabel>
                    <Select
                      name="fromAccountId"
                      value={formData.fromAccountId}
                      onChange={handleInputChange}
                      label="From Account"
                    >
                      {accounts.map((account) => (
                        <MenuItem key={account.id} value={account.id}>
                          <Box>
                            <Typography variant="body1">
                              {account.accountNumber} - {account.accountType}
                            </Typography>
                            <Typography variant="body2" color="textSecondary">
                              Balance: ₹{parseFloat(account.balance).toFixed(2)}
                            </Typography>
                          </Box>
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <TextField
                    name="toAccountNumber"
                    label="To Account Number"
                    value={formData.toAccountNumber}
                    onChange={handleInputChange}
                    fullWidth
                    required
                    placeholder="Enter destination account number"
                  />
                </Grid>
              </>
            )}

            {/* Description Field */}
            <Grid item xs={12}>
              <TextField
                name="description"
                label="Description (Optional)"
                value={formData.description}
                onChange={handleInputChange}
                fullWidth
                multiline
                rows={2}
                placeholder={`Enter ${transactionType.toLowerCase()} description...`}
              />
            </Grid>

            {/* Summary Card */}
            <Grid item xs={12}>
              <Card variant="outlined">
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Transaction Summary
                  </Typography>
                  <Box display="flex" flexDirection="column" gap={1}>
                    <Box display="flex" justifyContent="space-between">
                      <Typography>Type:</Typography>
                      <Chip 
                        label={transactionType} 
                        color="primary" 
                        size="small" 
                      />
                    </Box>
                    <Box display="flex" justifyContent="space-between">
                      <Typography>Amount:</Typography>
                      <Typography fontWeight="bold">
                        ₹{formData.amount || '0.00'}
                      </Typography>
                    </Box>
                    {formData.description && (
                      <Box display="flex" justifyContent="space-between">
                        <Typography>Description:</Typography>
                        <Typography>{formData.description}</Typography>
                      </Box>
                    )}
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </DialogContent>

        <DialogActions>
          <Button onClick={onClose} disabled={loading}>
            Cancel
          </Button>
          <Button 
            type="submit" 
            variant="contained" 
            disabled={loading}
            color="primary"
          >
            {loading ? 'Processing...' : `Confirm ${transactionType}`}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default TransactionForm;
