import React, { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  CircularProgress,
  Tabs,
  Tab,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@mui/material';
import { adminService } from '../services/api';
import DashboardLayout from '../components/DashboardLayout';

const AdminDashboard = () => {
  const [users, setUsers] = useState([]);
  const [tournaments, setTournaments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [openUserDialog, setOpenUserDialog] = useState(false);
  const [openTournamentDialog, setOpenTournamentDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [tabValue, setTabValue] = useState(0);

  const [userForm, setUserForm] = useState({
    name: '',
    email: '',
    role: '',
  });

  const [tournamentForm, setTournamentForm] = useState({
    name: '',
    location: '',
    startDate: '',
    endDate: '',
    registrationDeadline: '',
  });
  const [openAssignRefereeDialog, setOpenAssignRefereeDialog] = useState(false);
const [selectedTournamentId, setSelectedTournamentId] = useState(null);
const [selectedReferees, setSelectedReferees] = useState([]);
const [refereeOptions, setRefereeOptions] = useState([]);


  useEffect(() => {
    if (tabValue === 0) {
      fetchUsers();
    } else {
      fetchTournaments();
    }
  }, [tabValue]);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await adminService.getAllUsers();
      setUsers(response.data);
    } catch (err) {
      setError('Failed to fetch users');
    } finally {
      setLoading(false);
    }
  };

  const fetchTournaments = async () => {
    try {
      setLoading(true);
      const response = await adminService.getAllTournaments();
      setTournaments(response.data);
    } catch (err) {
      setError('Failed to fetch tournaments');
    } finally {
      setLoading(false);
    }
  };
  const fetchReferees = async () => {
    const allUsers = await adminService.getAllUsers();
    const referees = allUsers.data.filter(u => u.role === 'REFEREE');
    setRefereeOptions(referees);
  };
  const handleOpenAssignRefereeDialog = (tournamentId) => {
    setSelectedTournamentId(tournamentId);
    setSelectedReferees([]);
    fetchReferees();
    setOpenAssignRefereeDialog(true);
  };
  
  
  const handleOpenUserDialog = (user = null) => {
    setSelectedUser(user);
    setUserForm(
      user
        ? {
            name: user.name,
            email: user.email,
            role: user.role,
          }
        : {
            name: '',
            email: '',
            role: '',
          }
    );
    setOpenUserDialog(true);
  };

  const handleOpenTournamentDialog = () => {
    setTournamentForm({
      name: '',
      startDate: '',
      endDate: '',
    });
    setOpenTournamentDialog(true);
  };

  const handleUserFormChange = (e) => {
    const { name, value } = e.target;
    setUserForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleTournamentFormChange = (e) => {
    const { name, value } = e.target;
    setTournamentForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmitUser = async () => {
    try {
      setLoading(true);
      setError('');
      if (selectedUser) {
        await adminService.updateUser(selectedUser.id, userForm);
        setSuccess('User updated successfully');
      } else {
        await adminService.createUser(userForm);
        setSuccess('User created successfully');
      }
      fetchUsers();
      setOpenUserDialog(false);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save user');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitTournament = async () => {
    try {
      setLoading(true);
      setError('');
      await adminService.createTournament(tournamentForm);
      setSuccess('Tournament created successfully');
      fetchTournaments();
      setOpenTournamentDialog(false);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create tournament');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (userId) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        setLoading(true);
        await adminService.deleteUser(userId);
        setSuccess('User deleted successfully');
        fetchUsers();
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to delete user');
      } finally {
        setLoading(false);
      }
    }
  };

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };
  const handleGenerateMatches = async (id) => {
    try {
      setLoading(true);
      setError('');
      await adminService.generateMatches(id);
      setSuccess('Matches generated');
      fetchTournaments();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to generate matches');
    } finally {
      setLoading(false);
    }
  };
  const downloadFile = async (endpoint, filename) => {
    const fullUrl = `http://localhost:8080${endpoint}`;
    try {
      const response = await fetch(fullUrl, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
  
      if (!response.ok) {
        throw new Error("Download failed");
      }
  
      const blob = await response.blob();
      if (blob.type.startsWith('text/html')) {
        const text = await blob.text();
        console.error("Server returned HTML error page:\n", text);
        throw new Error("Server returned HTML instead of data. Check backend error.");
      }
  
      const urlBlob = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = urlBlob;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(urlBlob);
    } catch (err) {
      console.error("Download failed:", err);
      setError("Failed to download file.");
    }
  };
  
  
  
  const handleDeleteTournament = async (id) => {
    if (window.confirm('Are you sure you want to delete this tournament?')) {
      try {
        setLoading(true);
        setError('');
        await adminService.deleteTournament(id);
        setSuccess('Tournament deleted');
        fetchTournaments();
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to delete tournament');
      } finally {
        setLoading(false);
      }
    }
  };
  
  return (
    <DashboardLayout title="Admin Dashboard">
      <Container maxWidth="lg">
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

        <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
          <Tabs value={tabValue} onChange={handleTabChange}>
            <Tab label="Users" />
            <Tab label="Tournaments" />
          </Tabs>
        </Box>

        {tabValue === 0 && (
          <>
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Role</TableCell>
                    <TableCell>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {loading ? (
                    <TableRow>
                      <TableCell colSpan={4} align="center">
                        <CircularProgress />
                      </TableCell>
                    </TableRow>
                  ) : users.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={4} align="center">
                        No users found
                      </TableCell>
                    </TableRow>
                  ) : (
                    users.map((user) => (
                      <TableRow key={user.id}>
                        <TableCell>{user.name}</TableCell>
                        <TableCell>{user.email}</TableCell>
                        <TableCell>{user.role}</TableCell>
                        <TableCell>
                          <Button
                            variant="outlined"
                            color="primary"
                            onClick={() => handleOpenUserDialog(user)}
                            sx={{ mr: 1 }}
                          >
                            Edit
                          </Button>
                          <Button
                            variant="outlined"
                            color="error"
                            onClick={() => handleDeleteUser(user.id)}
                          >
                            Delete
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </>
        )}

        {tabValue === 1 && (
          <>
            <Box sx={{ mb: 2 }}>
              <Button
                variant="contained"
                color="primary"
                onClick={handleOpenTournamentDialog}
              >
                Create Tournament
              </Button>
            </Box>
            <Box sx={{ mb: 2, display: 'flex', gap: 2 }}>
  <Button
    variant="contained"
    color="primary"
    onClick={() => downloadFile('/api/admin/matches/export/csv', 'matches.csv')}
  >
    Export as CSV
  </Button>
  <Button
    variant="contained"
    color="secondary"
    onClick={() => downloadFile('/api/admin/matches/export/txt', 'matches.txt')}
  >
    Export as TXT
  </Button>
</Box>

            <TableContainer component={Paper}>
              <Table>
              <TableHead>
  <TableRow>
    <TableCell>Name</TableCell>
    <TableCell>Location</TableCell>
    <TableCell>Start Date</TableCell>
    <TableCell>End Date</TableCell>
    <TableCell>Registration Deadline</TableCell>
    <TableCell>Status</TableCell>
    <TableCell>Actions</TableCell>
  </TableRow>
</TableHead>

                <TableBody>
                  {loading ? (
                    <TableRow>
                      <TableCell colSpan={4} align="center">
                        <CircularProgress />
                      </TableCell>
                    </TableRow>
                  ) : tournaments.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={4} align="center">
                        No tournaments found
                      </TableCell>
                    </TableRow>
                  ) : (
                    tournaments.map((t) => (
                      <TableRow key={t.id}>
                        <TableCell>{t.name}</TableCell>
                        <TableCell>{t.location}</TableCell>
                        <TableCell>{new Date(t.startDate).toLocaleDateString()}</TableCell>
                        <TableCell>{new Date(t.endDate).toLocaleDateString()}</TableCell>
                        <TableCell>{new Date(t.registrationDeadline).toLocaleDateString()}</TableCell>
                        <TableCell>{t.status}</TableCell>
                        <TableCell>
                          <Button
                            variant="outlined"
                            size="small"
                            onClick={() => handleGenerateMatches(t.id)}
                            sx={{ mr: 1 }}
                          >
                            Generate Matches
                          </Button>
                          <Button
                           variant="outlined"
                            size="small"
                          onClick={() => handleOpenAssignRefereeDialog(t.id)}
                           sx={{ mr: 1 }}
>
                            Assign Referees
                            </Button>

                          <Button
                            variant="outlined"
                            color="error"
                            size="small"
                            onClick={() => handleDeleteTournament(t.id)}
                          >
                            Delete
                          </Button>
                          
                        </TableCell>
                      </TableRow>
                    ))
                    
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </>
        )}
        <Dialog open={openAssignRefereeDialog} onClose={() => setOpenAssignRefereeDialog(false)}>
  <DialogTitle>Assign Referees</DialogTitle>
  <DialogContent>
    <FormControl fullWidth margin="normal">
      <InputLabel>Referees</InputLabel>
      <Select
        multiple
        value={selectedReferees}
        onChange={(e) => setSelectedReferees(e.target.value)}
        renderValue={(selected) =>
          refereeOptions
            .filter((r) => selected.includes(r.id))
            .map((r) => r.name)
            .join(', ')
        }
      >
        {refereeOptions.map((ref) => (
          <MenuItem key={ref.id} value={ref.id}>
            {ref.name}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  </DialogContent>
  <DialogActions>
    <Button onClick={() => setOpenAssignRefereeDialog(false)}>Cancel</Button>
    <Button
      onClick={async () => {
        try {
          await adminService.assignRefereesToTournament(selectedTournamentId, selectedReferees);
          setSuccess('Referees assigned successfully');
          setOpenAssignRefereeDialog(false);
        } catch (err) {
          setError('Failed to assign referees');
        }
      }}
      variant="contained"
      color="primary"
    >
      Assign
    </Button>
  </DialogActions>
</Dialog>

        <Dialog open={openUserDialog} onClose={() => setOpenUserDialog(false)}>
          <DialogTitle>
            {selectedUser ? 'Edit User' : 'Create New User'}
          </DialogTitle>
          <DialogContent>
            <Box sx={{ mt: 2 }}>
              <TextField
                fullWidth
                name="name"
                label="Name"
                value={userForm.name}
                onChange={handleUserFormChange}
                margin="normal"
                disabled={loading}
              />
              <TextField
                fullWidth
                name="email"
                label="Email"
                type="email"
                value={userForm.email}
                onChange={handleUserFormChange}
                margin="normal"
                disabled={loading}
              />
              <FormControl fullWidth margin="normal">
                <InputLabel>Role</InputLabel>
                <Select
                  name="role"
                  value={userForm.role}
                  onChange={handleUserFormChange}
                  label="Role"
                  disabled={loading}
                >
                  <MenuItem value="PLAYER">Player</MenuItem>
                  <MenuItem value="REFEREE">Referee</MenuItem>
                  <MenuItem value="ADMIN">Admin</MenuItem>
                </Select>
              </FormControl>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenUserDialog(false)}>Cancel</Button>
            <Button
              onClick={handleSubmitUser}
              variant="contained"
              color="primary"
              disabled={loading || !userForm.name || !userForm.email || !userForm.role}
            >
              {loading ? <CircularProgress size={24} /> : 'Save'}
            </Button>
          </DialogActions>
        </Dialog>

        <Dialog
          open={openTournamentDialog}
          onClose={() => setOpenTournamentDialog(false)}
        >
          <DialogTitle>Create New Tournament</DialogTitle>
          <DialogContent>
            <Box sx={{ mt: 2 }}>
            <TextField
  fullWidth
  name="name"
  label="Tournament Name"
  value={tournamentForm.name}
  onChange={handleTournamentFormChange}
  margin="normal"
  disabled={loading}
/>
<TextField
  fullWidth
  name="location"
  label="Location"
  value={tournamentForm.location}
  onChange={handleTournamentFormChange}
  margin="normal"
  disabled={loading}
/>
<TextField
  fullWidth
  name="startDate"
  label="Start Date"
  type="date"
  InputLabelProps={{ shrink: true }}
  value={tournamentForm.startDate}
  onChange={handleTournamentFormChange}
  margin="normal"
  disabled={loading}
/>
<TextField
  fullWidth
  name="endDate"
  label="End Date"
  type="date"
  InputLabelProps={{ shrink: true }}
  value={tournamentForm.endDate}
  onChange={handleTournamentFormChange}
  margin="normal"
  disabled={loading}
/>
<TextField
  fullWidth
  name="registrationDeadline"
  label="Registration Deadline"
  type="date"
  InputLabelProps={{ shrink: true }}
  value={tournamentForm.registrationDeadline}
  onChange={handleTournamentFormChange}
  margin="normal"
  disabled={loading}
/>
            </Box>
          </DialogContent>
          <DialogActions>
          <Button
  onClick={handleSubmitTournament}
  variant="contained"
  color="primary"
  disabled={
    loading ||
    !tournamentForm.name ||
    !tournamentForm.location ||
    !tournamentForm.startDate ||
    !tournamentForm.endDate ||
    !tournamentForm.registrationDeadline
  }
>
  {loading ? <CircularProgress size={24} /> : 'Create'}
</Button>

          </DialogActions>
        </Dialog>
      </Container>
    </DashboardLayout>
  );
};

export default AdminDashboard; 