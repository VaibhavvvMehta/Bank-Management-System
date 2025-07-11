import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const Transactions = () => {
  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Transactions
      </Typography>
      <Box>
        <Typography variant="body1">
          Transaction management functionality will be implemented here.
        </Typography>
      </Box>
    </Container>
  );
};

export default Transactions;
