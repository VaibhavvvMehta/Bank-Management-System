import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  Box,
  Typography
} from '@mui/material';
import * as accountService from '../services/accountService';
import * as transactionService from '../services/transactionService';

const TransactionDialog = ({ open, onClose, onTransactionCreated, type = 'deposit' }) => {
  const [accounts, setAccounts] = useState([]);
  const [formData, setFormData] = useState({
    accountId: '',
    fromAccountId: '',
    toAccountId: '',
    amount: '',
    description: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (open) {
      loadAccounts();
    }
  }, [open]);

  const loadAccounts = async () => {
    try {
      const response = await accountService.getMyActiveAccounts();
      setAccounts(response.data);
    } catch (err) {
      setError('Failed to load accounts');
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      let response;
      
      switch (type) {
        case 'deposit':
          response = await transactionService.createDeposit({
            accountId: formData.accountId,
            amount: parseFloat(formData.amount),
            description: formData.description || 'Deposit'
          });
          break;
        case 'withdrawal':
          response = await transactionService.createWithdrawal({
            accountId: formData.accountId,
            amount: parseFloat(formData.amount),
            description: formData.description || 'Withdrawal'
          });
          break;
        case 'transfer':
          response = await transactionService.createTransfer({
            fromAccountId: formData.fromAccountId,
            toAccountId: formData.toAccountId,
            amount: parseFloat(formData.amount),
            description: formData.description || 'Transfer'
          });
          break;
        default:
          throw new Error('Invalid transaction type');
      }

      onTransactionCreated(response.data.transaction);
      onClose();
      resetForm();
    } catch (err) {
      setError(err.response?.data?.message || 'Transaction failed');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      accountId: '',
      fromAccountId: '',
      toAccountId: '',
      amount: '',
      description: ''
    });
    setError('');
  };

  const handleClose = () => {
    resetForm();
    onClose();
  };

  const getTitle = () => {
    switch (type) {
      case 'deposit':
        return 'Make Deposit';
      case 'withdrawal':
        return 'Make Withdrawal';
      case 'transfer':
        return 'Transfer Funds';
      default:
        return 'Transaction';
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>{getTitle()}</DialogTitle>
      <DialogContent>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        
        <Box component="form" onSubmit={handleSubmit}>
          {type === 'transfer' ? (
            <>
              <FormControl fullWidth margin="normal">
                <InputLabel>From Account</InputLabel>
                <Select
                  name="fromAccountId"
                  value={formData.fromAccountId}
                  onChange={handleChange}
                  label="From Account"
                  required
                >
                  {accounts.map((account) => (
                    <MenuItem key={account.id} value={account.id}>
                      {account.accountNumber} - {account.accountType} (₹{account.balance})
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              <FormControl fullWidth margin="normal">
                <InputLabel>To Account</InputLabel>
                <Select
                  name="toAccountId"
                  value={formData.toAccountId}
                  onChange={handleChange}
                  label="To Account"
                  required
                >
                  {accounts
                    .filter(account => account.id !== parseInt(formData.fromAccountId))
                    .map((account) => (
                    <MenuItem key={account.id} value={account.id}>
                      {account.accountNumber} - {account.accountType} (₹{account.balance})
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </>
          ) : (
            <FormControl fullWidth margin="normal">
              <InputLabel>Account</InputLabel>
              <Select
                name="accountId"
                value={formData.accountId}
                onChange={handleChange}
                label="Account"
                required
              >
                {accounts.map((account) => (
                  <MenuItem key={account.id} value={account.id}>
                    {account.accountNumber} - {account.accountType} (₹{account.balance})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}

          <TextField
            fullWidth
            margin="normal"
            name="amount"
            label="Amount"
            type="number"
            value={formData.amount}
            onChange={handleChange}
            required
            inputProps={{ min: 0.01, step: 0.01 }}
          />

          <TextField
            fullWidth
            margin="normal"
            name="description"
            label="Description (Optional)"
            value={formData.description}
            onChange={handleChange}
            multiline
            rows={2}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Cancel</Button>
        <Button 
          onClick={handleSubmit} 
          variant="contained" 
          disabled={loading}
        >
          {loading ? 'Processing...' : 'Complete Transaction'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TransactionDialog;
