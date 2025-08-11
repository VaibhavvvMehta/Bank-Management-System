import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Typography, 
  Box, 
  Paper, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Chip, 
  CircularProgress, 
  Alert 
} from '@mui/material';
import { useAuth } from '../context/AuthContext';
import transactionService from '../services/transactionService';
import adminService from '../services/adminService';

const Transactions = () => {
  const { user, isAdmin, isEmployee, isCustomer } = useAuth();
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadTransactions();
  }, []);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      let data;
      
      if (isAdmin()) {
        // Admin sees ALL bank transactions
        data = await adminService.getAdminTransactions();
      } else if (isEmployee()) {
        // Employees see all transactions
        data = await transactionService.getAllTransactions();
      } else {
        // Customers see only their own transactions
        data = await transactionService.getMyTransactions();
      }
      
      setTransactions(data || []);
    } catch (err) {
      setError('Failed to load transactions');
      console.error('Error loading transactions:', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return 'success';
      case 'PENDING': return 'warning';
      case 'FAILED': return 'error';
      case 'CANCELLED': return 'default';
      default: return 'default';
    }
  };

  const getTypeColor = (type) => {
    switch (type) {
      case 'DEPOSIT': return 'success';
      case 'WITHDRAWAL': return 'error';
      case 'TRANSFER': return 'info';
      default: return 'default';
    }
  };

  const formatAmount = (amount, type) => {
    const prefix = type === 'DEPOSIT' ? '+' : type === 'WITHDRAWAL' ? '-' : '';
    return `${prefix}₹${parseFloat(amount).toFixed(2)}`;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        {isAdmin() ? 'All Bank Transactions' : isEmployee() ? 'All Transactions' : 'My Transactions'}
      </Typography>
      
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Paper sx={{ width: '100%', overflow: 'hidden' }}>
        {transactions.length === 0 ? (
          <Box sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="body1" color="text.secondary">
              No transactions found. Make some transactions to see them here.
            </Typography>
          </Box>
        ) : (
          <TableContainer sx={{ maxHeight: 440 }}>
            <Table stickyHeader>
              <TableHead>
                <TableRow>
                  <TableCell>Date</TableCell>
                  <TableCell>Reference</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Description</TableCell>
                  <TableCell align="right">Amount</TableCell>
                  <TableCell align="right">Balance After</TableCell>
                  <TableCell>Status</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {transactions.map((transaction) => (
                  <TableRow key={transaction.id} hover>
                    <TableCell>
                      {formatDate(transaction.createdAt)}
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" color="text.secondary">
                        {transaction.transactionReference}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={transaction.transactionType} 
                        color={getTypeColor(transaction.transactionType)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {transaction.description || 'No description'}
                    </TableCell>
                    <TableCell align="right">
                      <Typography 
                        variant="body2" 
                        color={transaction.transactionType === 'DEPOSIT' ? 'success.main' : 
                               transaction.transactionType === 'WITHDRAWAL' ? 'error.main' : 'inherit'}
                        fontWeight="bold"
                      >
                        {formatAmount(transaction.amount, transaction.transactionType)}
                      </Typography>
                    </TableCell>
                    <TableCell align="right">
                      <Typography variant="body2">
                        ₹{parseFloat(transaction.balanceAfterTransaction || 0).toFixed(2)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={transaction.transactionStatus} 
                        color={getStatusColor(transaction.transactionStatus)}
                        size="small"
                      />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Paper>
      
      <Box sx={{ mt: 2 }}>
        <Typography variant="body2" color="text.secondary">
          Total transactions: {transactions.length}
        </Typography>
      </Box>
    </Container>
  );
};

export default Transactions;
