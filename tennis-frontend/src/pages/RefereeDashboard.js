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
  TextField,
  Alert,
  CircularProgress,
  Tabs,
  Tab,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormGroup,
  FormControlLabel,
  Checkbox,
} from '@mui/material';
import { refereeService } from '../services/api';
import DashboardLayout from '../components/DashboardLayout';

const RefereeDashboard = () => {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedMatch, setSelectedMatch] = useState(null);
  const [scoreForm, setScoreForm] = useState({
    setsWonPlayer1: '',
    setsWonPlayer2: '',
    player1Points: '',
    player2Points: '',
    winnerId: '',
    tiebreakPlayed: false,
    retirement: false,
    walkover: false,
    set1Score: '',
    set2Score: '',
    set3Score: '',
  });
  
  const [scoreDetails, setScoreDetails] = useState(null);
  const [scoreDialogOpen, setScoreDialogOpen] = useState(false);

  const [tabValue, setTabValue] = useState(0);

  useEffect(() => {
    fetchMatches();
  }, []);
  const validateScoreForm = () => {
    const scorePattern = /^\d{1,2}-\d{1,2}$/;
  
    if (!scorePattern.test(scoreForm.set1Score) || !scorePattern.test(scoreForm.set2Score)) {
      setError('Set 1 and Set 2 scores must be in format: X-Y');
      return false;
    }
  
    if (scoreForm.set3Score && !scorePattern.test(scoreForm.set3Score)) {
      setError('Set 3 score must be in format: X-Y');
      return false;
    }
  
    if (!scoreForm.winnerId) {
      setError('Please select a winner');
      return false;
    }
  
    if (Number(scoreForm.setsWonPlayer1) === Number(scoreForm.setsWonPlayer2)) {
      setError('Sets must not be equal. There must be a winner.');
      return false;
    }
  
    return true;
  };
  const handleViewScore = async (matchId) => {
    try {
      setLoading(true);
      const response = await refereeService.getMatchScore(matchId); // Make sure this service exists!
      setScoreDetails(response.data);
      setScoreDialogOpen(true);
    } catch (err) {
      setError('Failed to load score details');
    } finally {
      setLoading(false);
    }
  };
  
  const fetchMatches = async () => {
    try {
      setLoading(true);
      const response = await refereeService.getAssignedMatches();
      setMatches(response.data);
    } catch (err) {
      setError('Failed to fetch matches');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenScoreDialog = (match) => {
    setSelectedMatch({
      ...match,
      player1: match.player1Name,
      player2: match.player2Name,
      player1Id: match.player1Id,
      player2Id: match.player2Id,
    });
  
    setScoreForm({
      setsWonPlayer1: '',
      setsWonPlayer2: '',
      player1Points: '',
      player2Points: '',
      winnerId: '',
      tiebreakPlayed: false,
      retirement: false,
      walkover: false,
      set1Score: '',
      set2Score: '',
      set3Score: '',
    });
  
    setOpenDialog(true);
  };
  

  const handleScoreChange = (e) => {
    const { name, value } = e.target;
    setScoreForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmitScore = async () => {
    if (!validateScoreForm()) return;
  
    try {
      setLoading(true);
      setError('');
  
      // Determine status
      let status = 'SCHEDULED';
  
      const hasAnyScore = [
        scoreForm.setsWonPlayer1,
        scoreForm.setsWonPlayer2,
        scoreForm.player1Points,
        scoreForm.player2Points,
        scoreForm.set1Score,
        scoreForm.set2Score,
        scoreForm.set3Score,
      ].some(val => val && val !== '');
  
      if (hasAnyScore) status = 'IN_PROGRESS';
      if (scoreForm.winnerId) status = 'COMPLETED';
  
      await refereeService.submitMatchScore(selectedMatch.matchId, {
        setsWonPlayer1: parseInt(scoreForm.setsWonPlayer1),
        setsWonPlayer2: parseInt(scoreForm.setsWonPlayer2),
        player1Points: parseInt(scoreForm.player1Points),
        player2Points: parseInt(scoreForm.player2Points),
        winnerId: parseInt(scoreForm.winnerId),
        tiebreakPlayed: scoreForm.tiebreakPlayed,
        retirement: scoreForm.retirement,
        walkover: scoreForm.walkover,
        set1: scoreForm.set1Score,
        set2: scoreForm.set2Score,
        set3: scoreForm.set3Score,
        status: status, // <-- Add this
      });
  
      setSuccess('Score submitted successfully');
      fetchMatches();
      setOpenDialog(false);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit score');
    } finally {
      setLoading(false);
    }
  };
  
  

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const filteredMatches = matches.filter((match) => {
    if (tabValue === 0) return match.status === 'SCHEDULED'|| match.status === 'IN_PROGRESS'|| match.status === 'COMPLETED';
    //if (tabValue === 1) return match.status === 'IN_PROGRESS';
    return match.status === 'COMPLETED';
  });

  return (
    <DashboardLayout title="Referee Dashboard">
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
            <Tab label="Scheduled Matches" />

          </Tabs>
        </Box>

        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Tournament</TableCell>
                <TableCell>Player 1</TableCell>
                <TableCell>Player 2</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Score</TableCell>
                <TableCell>Action</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={7} align="center">
                    <CircularProgress />
                  </TableCell>
                </TableRow>
              ) : filteredMatches.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} align="center">
                    No matches found
                  </TableCell>
                </TableRow>
              ) : (
                filteredMatches.map((match) => (
                  <TableRow key={match.matchId}>
                    <TableCell>{match.tournamentName}</TableCell>
                    <TableCell>{match.player1Name}</TableCell>
                    <TableCell>{match.player2Name}</TableCell>
                    <TableCell>
                      {new Date(match.matchDateTime).toLocaleDateString()}<br />
                     {new Date(match.matchDateTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </TableCell>

                    <TableCell>{match.status}</TableCell>
                    <TableCell>
  <Button
    variant="outlined"
    size="small"
    onClick={() => handleViewScore(match.matchId)}
    disabled={match.status !== 'COMPLETED'}
  >
    View Score
  </Button>
</TableCell>

                    <TableCell>
                      {(match.status === 'IN_PROGRESS' || match.status === 'SCHEDULED') && (
                        <Button
                          variant="contained"
                          color="primary"
                          onClick={() => handleOpenScoreDialog(match)}
                        >
                          Update Score
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>

        <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
          <DialogTitle>Submit Match Score</DialogTitle>
          <DialogContent>
            <Box sx={{ mt: 2 }}>
              <Typography variant="h6" gutterBottom>
                {selectedMatch?.player1Name} vs {selectedMatch?.player2Name}
              </Typography>
              
              <Typography variant="h6" gutterBottom>
  {selectedMatch?.player1Name} vs {selectedMatch?.player2Name}
</Typography>

<TextField
  fullWidth
  name="setsWonPlayer1"
  label={`${selectedMatch?.player1Name}'s Sets Won`}
  type="number"
  value={scoreForm.setsWonPlayer1}
  onChange={handleScoreChange}
/>
<TextField
  fullWidth
  name="setsWonPlayer2"
  label={`${selectedMatch?.player2Name}'s Sets Won`}
  type="number"
  value={scoreForm.setsWonPlayer2}
  onChange={handleScoreChange}
/>

<TextField
  fullWidth
  name="player1Points"
  label={`${selectedMatch?.player1Name}'s Points`}
  type="number"
  value={scoreForm.player1Points}
  onChange={handleScoreChange}
/>
<TextField
  fullWidth
  name="player2Points"
  label={`${selectedMatch?.player2Name}'s Points`}
  type="number"
  value={scoreForm.player2Points}
  onChange={handleScoreChange}
/>

<FormControl fullWidth margin="normal">
  <InputLabel>Winner</InputLabel>
  <Select
    name="winnerId"
    value={scoreForm.winnerId||''}
    onChange={handleScoreChange}
  >
    <MenuItem value={selectedMatch?.player1Id}>{selectedMatch?.player1}</MenuItem>
    <MenuItem value={selectedMatch?.player2Id}>{selectedMatch?.player2}</MenuItem>
  </Select>
</FormControl>

<FormGroup row>
  <FormControlLabel
    control={<Checkbox checked={scoreForm.tiebreakPlayed} onChange={(e) => setScoreForm(prev => ({ ...prev, tiebreakPlayed: e.target.checked }))} />}
    label="Tiebreak Played"
/>
  <FormControlLabel
    control={<Checkbox checked={scoreForm.retirement} onChange={(e) => setScoreForm(prev => ({ ...prev, retirement: e.target.checked }))} />}
    label="Retirement"
/>
  <FormControlLabel
    control={<Checkbox checked={scoreForm.walkover} onChange={(e) => setScoreForm(prev => ({ ...prev, walkover: e.target.checked }))} />}
    label="Walkover"
/>

</FormGroup>
<TextField
  fullWidth
  name="set1Score"
  label="Set 1 Score (e.g., 6-4)"
  value={scoreForm.set1Score}
  onChange={handleScoreChange}
  margin="normal"
/>
<TextField
  fullWidth
  name="set2Score"
  label="Set 2 Score (e.g., 4-6)"
  value={scoreForm.set2Score}
  onChange={handleScoreChange}
  margin="normal"
/>
<TextField
  fullWidth
  name="set3Score"
  label="Set 3 Score (optional)"
  value={scoreForm.set3Score}
  onChange={handleScoreChange}
  margin="normal"
/>


            </Box>
            <Typography variant="body2" sx={{ mt: 2 }}>
           Score Preview: {scoreForm.set1Score || '–'}, {scoreForm.set2Score || '–'}, {scoreForm.set3Score || '–'}
            </Typography>

          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
            <Button
              onClick={handleSubmitScore}
              variant="contained"
              color="primary"
              disabled={loading || !scoreForm.setsWonPlayer1 || !scoreForm.setsWonPlayer2}
            >
              {loading ? <CircularProgress size={24} /> : 'Update Score'}
            </Button>

          </DialogActions>
        </Dialog>
        <Dialog open={scoreDialogOpen} onClose={() => setScoreDialogOpen(false)}>
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
    <Button onClick={() => setScoreDialogOpen(false)}>Close</Button>
  </DialogActions>
</Dialog>

      </Container>
    </DashboardLayout>
  );
};

export default RefereeDashboard; 