import React, { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Typography,
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
  Alert,
  CircularProgress,
  Tabs,
  Tab,
} from '@mui/material';
import { playerService } from '../services/api';
import DashboardLayout from '../components/DashboardLayout';

const PlayerDashboard = () => {
  const [tournaments, setTournaments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedTournament, setSelectedTournament] = useState(null);
  const [tabValue, setTabValue] = useState(0);
  const [scheduleDialogOpen, setScheduleDialogOpen] = useState(false);
  const [tournamentMatches, setTournamentMatches] = useState([]);
  
  const [tournamentScores, setTournamentScores] = useState([]);
  const [scoreDetails, setScoreDetails] = useState(null);
  const [scoreListDialogOpen, setScoreListDialogOpen] = useState(false);
  const [scoreDetailsDialogOpen, setScoreDetailsDialogOpen] = useState(false);
  
  useEffect(() => {
    fetchTournaments();
  }, []);

  const fetchTournaments = async () => {
    try {
      setLoading(true);
      const response = await playerService.getAvailableTournaments();
      console.log('Fetched tournaments:', response.data);
      setTournaments(response.data.sort((a, b) => new Date(a.startDate) - new Date(b.startDate)));

    } catch (err) {
      setError('Failed to fetch tournaments');
    } finally {
      setLoading(false);
    }
  };


  const handleRegister = (tournament) => {
    setSelectedTournament(tournament);
    setOpenDialog(true);
  };

  const handleConfirmRegistration = async () => {
    try {
      setLoading(true);
      setError('');
      await playerService.registerForTournament(selectedTournament.id);
      setSuccess('Successfully registered for tournament');
      fetchTournaments();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to register for tournament');
    } finally {
      setLoading(false);
      setOpenDialog(false);
    }
  };
  const handleViewScores = async (tournamentId) => {
    try {
      setLoading(true);
      const response = await playerService.getTournamentMatches(tournamentId);
      const completedMatches = response.data.filter(match => match.status === 'COMPLETED');
      setTournamentScores(completedMatches);
      setScoreListDialogOpen(true); // ✅ open list dialog
    } catch (err) {
      setError('Failed to fetch match scores');
    } finally {
      setLoading(false);
    }
  };
  
  const handleViewSchedule = async (tournamentId) => {
    try {
      setLoading(true);
      const response = await playerService.getTournamentMatches(tournamentId);
      setTournamentMatches(response.data);
      setScheduleDialogOpen(true);
    } catch (err) {
      setError('Failed to fetch schedule');
    } finally {
      setLoading(false);
    }
  };
  const handleViewScore = async (matchId) => {
    try {
      setLoading(true);
      const response = await playerService.getMatchScore(matchId);
      setScoreDetails(response.data);
      setScoreDetailsDialogOpen(true); // ✅ open details dialog
    } catch (err) {
      setError('Failed to load score details');
    } finally {
      setLoading(false);
    }
  };
  
  
  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  return (
    <DashboardLayout title="Player Dashboard">
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
            <Tab label="Available Tournaments" />
            <Tab label="Tournament Schedule" />
          </Tabs>
        </Box>

        {tabValue === 0 && (
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                <TableCell>Name</TableCell>
                <TableCell>Location</TableCell>
                <TableCell>Start</TableCell>
                <TableCell>End</TableCell>
                <TableCell>Registration Deadline</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
<TableBody>
  {loading ? (
    <TableRow>
      <TableCell colSpan={7} align="center">
        <CircularProgress />
      </TableCell>
    </TableRow>
  ) : tournaments.length === 0 ? (
    <TableRow>
      <TableCell colSpan={7} align="center">
        No tournaments available
      </TableCell>
    </TableRow>
  ) : (
    tournaments.map((t) => {
      const startDate = new Date(t.startDate);
      const endDate = new Date(t.endDate);
      const regDeadline = new Date(t.registrationDeadline);
      const now = new Date();
    
      const canRegister = now < startDate && now < regDeadline;

      return (
        <TableRow key={t.id}>
          <TableCell>{t.name}</TableCell>
          <TableCell>{t.location}</TableCell>
          <TableCell>{startDate.toLocaleDateString()}</TableCell>
          <TableCell>{endDate.toLocaleDateString()}</TableCell>
          <TableCell>{regDeadline.toLocaleDateString()}</TableCell>
          <TableCell>{canRegister ? 'Open' : 'Closed'}</TableCell>
          <TableCell>
  {t.registered ? (
    <Typography color="success.main">Registration Approved</Typography>
  ) : (
    <Button
      variant="contained"
      color="primary"
      onClick={() => handleRegister(t)}
      disabled={!canRegister}
    >
      Register
    </Button>
  )}
</TableCell>



        </TableRow>
      );
    })
  )}
</TableBody>



            </Table>
          </TableContainer>
        )}

        {tabValue === 1 && (
          <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Name</TableCell>
                <TableCell>Location</TableCell>
                <TableCell>Start</TableCell>
                <TableCell>End</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {tournaments.map((t) => (
                <TableRow key={t.id}>
                  <TableCell>{t.name}</TableCell>
                  <TableCell>{t.location}</TableCell>
                  <TableCell>{new Date(t.startDate).toLocaleDateString()}</TableCell>
                  <TableCell>{new Date(t.endDate).toLocaleDateString()}</TableCell>
                  <TableCell>
                    <Button variant="outlined" onClick={() => handleViewSchedule(t.id)}>
                      See Tour Schedule
                    </Button>
                    <Button
                      variant="outlined"
                      onClick={() => handleViewScores(t.id)}
                      sx={{ ml: 1 }}
>
                      See Match Scores
                    </Button>

                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        
        )}
        <Dialog open={scheduleDialogOpen} onClose={() => setScheduleDialogOpen(false)} maxWidth="md" fullWidth>
  <DialogTitle>Match Schedule</DialogTitle>
  <DialogContent>
    {tournamentMatches.length === 0 ? (
      <Typography>No matches found for this tournament.</Typography>
    ) : (
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Player 1</TableCell>
            <TableCell>Player 2</TableCell>
            <TableCell>Referee</TableCell>
            <TableCell>Date</TableCell>
            <TableCell>Court</TableCell>
            <TableCell>Status</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {tournamentMatches.map((match) => (
            <TableRow key={match.id}>
              <TableCell>{match.player1Name}</TableCell>
              <TableCell>{match.player2Name}</TableCell>
              <TableCell>{match.refereeName}</TableCell>
              <TableCell>{new Date(match.scheduledDate).toLocaleString()}</TableCell>
              <TableCell>{match.courtName}</TableCell>
              <TableCell>{match.status}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    )}
  </DialogContent>
  <DialogActions>
    <Button onClick={() => setScheduleDialogOpen(false)}>Close</Button>
  </DialogActions>
</Dialog>




        <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
          <DialogTitle>Confirm Tournament Registration</DialogTitle>
          <DialogContent>
            <Typography>
              Are you sure you want to register for {selectedTournament?.name}?
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
            <Button onClick={handleConfirmRegistration} variant="contained" color="primary">
              Confirm
            </Button>
          </DialogActions>
        </Dialog>
        
<Dialog open={scoreListDialogOpen} onClose={() => setScoreListDialogOpen(false)} maxWidth="md" fullWidth>
  <DialogTitle>Match Scores</DialogTitle>
  <DialogContent>
    {tournamentScores.length === 0 ? (
      <Typography>No scores found for this tournament.</Typography>
    ) : (
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Player 1</TableCell>
            <TableCell>Player 2</TableCell>
            <TableCell>Referee</TableCell>
            <TableCell>Score</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {tournamentScores.map((match) => (
            <TableRow key={match.id}>
              <TableCell>{match.player1Name}</TableCell>
              <TableCell>{match.player2Name}</TableCell>
              <TableCell>{match.refereeName}</TableCell>
              <TableCell>
                <Button
                  variant="outlined"
                  size="small"
                  onClick={() => handleViewScore(match.id)}
                >
                  View Score
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    )}
  </DialogContent>
  <DialogActions>
    <Button onClick={() => setScoreListDialogOpen(false)}>Close</Button>
  </DialogActions>
</Dialog>
<Dialog open={scoreDetailsDialogOpen} onClose={() => setScoreDetailsDialogOpen(false)}>
  <DialogTitle>Match Score Details</DialogTitle>
  <DialogContent>
    {scoreDetails ? (
      <Box>
        <Typography><strong>Player 1 Points:</strong> {scoreDetails.player1Points}</Typography>
        <Typography><strong>Player 2 Points:</strong> {scoreDetails.player2Points}</Typography>
        <Typography><strong>Sets Won - Player 1:</strong> {scoreDetails.setsWonPlayer1}</Typography>
        <Typography><strong>Sets Won - Player 2:</strong> {scoreDetails.setsWonPlayer2}</Typography>
        <Typography><strong>Set 1:</strong> {scoreDetails.set1}</Typography>
        <Typography><strong>Set 2:</strong> {scoreDetails.set2}</Typography>
        <Typography><strong>Set 3:</strong> {scoreDetails.set3 || 'Not Played'}</Typography>
        <Typography><strong>Tiebreak Played:</strong> {scoreDetails.tiebreakPlayed ? 'Yes' : 'No'}</Typography>
        <Typography><strong>Retirement:</strong> {scoreDetails.retirement ? 'Yes' : 'No'}</Typography>
        <Typography><strong>Walkover:</strong> {scoreDetails.walkover ? 'Yes' : 'No'}</Typography>
      </Box>
    ) : (
      <Typography>Loading...</Typography>
    )}
  </DialogContent>
  <DialogActions>
    <Button onClick={() => setScoreDetailsDialogOpen(false)}>Close</Button>
  </DialogActions>
</Dialog>


      </Container>
    </DashboardLayout>
  );
};

export default PlayerDashboard; 