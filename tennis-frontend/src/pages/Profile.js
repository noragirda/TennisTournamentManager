import React, { useState} from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  Alert,
  CircularProgress,
} from '@mui/material';
import { userService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const validationSchema = Yup.object().shape({
  name: Yup.string().required('Name is required'),
  email: Yup.string().email('Enter a valid email').required('Email is required'),

  currentPassword: Yup.string(),

  newPassword: Yup.string().when('currentPassword', {
    is: (val) => !!val && val.length > 0,
    then: () =>
      Yup.string()
        .required('New password is required')
        .min(6, 'Password must be at least 6 characters'),
    otherwise: () => Yup.string().notRequired(),
  }),

  confirmPassword: Yup.string().when('newPassword', {
    is: (val) => !!val && val.length > 0,
    then: () =>
      Yup.string()
        .required('Please confirm your new password')
        .oneOf([Yup.ref('newPassword'), null], 'Passwords must match'),
    otherwise: () => Yup.string().notRequired(),
  }),
});





const Profile = () => {
  const { user } = useAuth();
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const formik = useFormik({
    initialValues: {
      name: user?.name || '',
      email: user?.email || '',
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
    validationSchema: validationSchema,
    onSubmit: async (values) => {
      try {
        setLoading(true);
        setError('');
        setSuccess('');

        const updateData = {
          name: values.name.trim() || undefined,
          email: values.email.trim() || undefined,
          password: values.currentPassword && values.newPassword
            ? values.newPassword
            : undefined,
        };
        
        
        console.log('Sending update data:', updateData);

        await userService.updateMe(updateData);
        setSuccess('Profile updated successfully');
        formik.resetForm({
          values: {
            ...values,
            currentPassword: '',
            newPassword: '',
            confirmPassword: '',
          },
        });
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to update profile');
      } finally {
        setLoading(false);
      }
    },
  });

  return (
    <Container maxWidth="sm">
      <Box sx={{ my: 4 }}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            Profile Settings
          </Typography>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {success}
            </Alert>
          )}
          <form onSubmit={formik.handleSubmit}>
            <TextField
              fullWidth
              id="name"
              name="name"
              label="Name"
              value={formik.values.name}
              onChange={formik.handleChange}
              error={formik.touched.name && Boolean(formik.errors.name)}
              helperText={formik.touched.name && formik.errors.name}
              margin="normal"
              disabled={loading}
            />
            <TextField
              fullWidth
              id="email"
              name="email"
              label="Email"
              value={formik.values.email}
              onChange={formik.handleChange}
              error={formik.touched.email && Boolean(formik.errors.email)}
              helperText={formik.touched.email && formik.errors.email}
              margin="normal"
              disabled={loading}
            />
            <TextField
              fullWidth
              id="currentPassword"
              name="currentPassword"
              label="Current Password"
              type="password"
              value={formik.values.currentPassword}
              onChange={formik.handleChange}
              error={formik.touched.currentPassword && Boolean(formik.errors.currentPassword)}
              helperText={formik.touched.currentPassword && formik.errors.currentPassword}
              margin="normal"
              disabled={loading}
            />
            <TextField
              fullWidth
              id="newPassword"
              name="newPassword"
              label="New Password"
              type="password"
              value={formik.values.newPassword}
              onChange={formik.handleChange}
              error={formik.touched.newPassword && Boolean(formik.errors.newPassword)}
              helperText={formik.touched.newPassword && formik.errors.newPassword}
              margin="normal"
              disabled={loading}
            />
            <TextField
              fullWidth
              id="confirmPassword"
              name="confirmPassword"
              label="Confirm New Password"
              type="password"
              value={formik.values.confirmPassword}
              onChange={formik.handleChange}
              error={formik.touched.confirmPassword && Boolean(formik.errors.confirmPassword)}
              helperText={formik.touched.confirmPassword && formik.errors.confirmPassword}
              margin="normal"
              disabled={loading}
            />
            <Button
              type="submit"
              variant="contained"
              fullWidth
              sx={{ mt: 3 }}
              disabled={loading}
            >
              {loading ? <CircularProgress size={24} /> : 'Update Profile'}
            </Button>
          </form>
        </Paper>
      </Box>
    </Container>
  );
};

export default Profile; 