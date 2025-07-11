import React from 'react';
import { Container, Typography, Box } from '@mui/material';
import { useAuth } from '../context/AuthContext';

const Profile = () => {
  const { user } = useAuth();

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Profile
      </Typography>
      <Box>
        <Typography variant="body1">
          Welcome, {user?.firstName} {user?.lastName}!
        </Typography>
        <Typography variant="body1">
          Email: {user?.email}
        </Typography>
        <Typography variant="body1">
          Role: {user?.role}
        </Typography>
      </Box>
    </Container>
  );
};

export default Profile;
