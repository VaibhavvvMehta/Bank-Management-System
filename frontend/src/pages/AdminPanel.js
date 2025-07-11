import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const AdminPanel = () => {
  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Admin Panel
      </Typography>
      <Box>
        <Typography variant="body1">
          Admin functionality will be implemented here.
        </Typography>
      </Box>
    </Container>
  );
};

export default AdminPanel;
