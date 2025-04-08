package com.example.tennistournament.controller;

import com.example.tennistournament.dto.AdminTournamentResponse;
import com.example.tennistournament.dto.AdminUpdateUserRequest;
import com.example.tennistournament.dto.UserResponse;
import com.example.tennistournament.model.Tournament;
import com.example.tennistournament.repository.UserRepository;
import com.example.tennistournament.service.AdminService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController
{
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,
                                             @RequestBody @Valid AdminUpdateUserRequest request) {
        adminService.updateUser(id, request);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/tournaments")
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        return ResponseEntity.ok(adminService.createTournament(tournament));
    }
    @PostMapping("/tournaments/{id}/generate-matches")
    public ResponseEntity<String> generateMatches(@PathVariable Long id) {
        adminService.generateMatches(id);
        return ResponseEntity.ok("Matches created.");
    }
    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable Long id) {
        adminService.deleteTournament(id);
        return ResponseEntity.ok("Tournament deleted.");
    }

    @DeleteMapping("/matches/{id}")
    public ResponseEntity<String> deleteMatch(@PathVariable Long id) {
        adminService.deleteMatch(id);
        return ResponseEntity.ok("Match deleted.");
    }
    @PutMapping("/tournaments/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Long id, @RequestBody Tournament updated) {
        Tournament result = adminService.updateTournament(id, updated);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/tournaments")
    public ResponseEntity<List<AdminTournamentResponse>> getAllTournaments() {
        return ResponseEntity.ok(adminService.getAllTournaments());
    }
    @PostMapping("/tournaments/{id}/assign-referees")
    public ResponseEntity<String> assignRefereesToTournament(
            @PathVariable Long id,
            @RequestBody List<Long> refereeIds) {
        adminService.assignRefereesToTournament(id, refereeIds);
        return ResponseEntity.ok("Referees assigned successfully.");
    }
    @GetMapping("/matches/export/csv")
    public void exportMatchesCsv(HttpServletResponse response) throws IOException {
        byte[] data = adminService.exportMatchListAsCsv();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=matches.csv");
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }


    @GetMapping("/matches/export/txt")
    public void exportMatchesTxt(HttpServletResponse response) throws IOException {
        byte[] data = adminService.exportMatchListAsTxt();
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=matches.txt");
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }









}
