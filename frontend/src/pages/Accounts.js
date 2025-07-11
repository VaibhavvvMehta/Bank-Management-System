import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const Accounts = () => {
  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Accounts
      </Typography>
      <Box>
        <Typography variant="body1">
          Account management functionality will be implemented here.
        </Typography>
      </Box>
    </Container>
  );
};

export default Accounts;
