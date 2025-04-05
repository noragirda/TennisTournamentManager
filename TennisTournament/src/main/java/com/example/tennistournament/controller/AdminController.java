package com.example.tennistournament.controller;

import com.example.tennistournament.dto.AdminUpdateUserRequest;
import com.example.tennistournament.dto.UserResponse;
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

    @GetMapping("/matches/export/csv")
    public void exportMatchesCsv(HttpServletResponse response) throws IOException {
        byte[] data = adminService.exportMatchListAsCsv();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=matches.csv");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(data);
        outputStream.flush();
    }

    @GetMapping("/matches/export/txt")
    public void exportMatchesTxt(HttpServletResponse response) throws IOException {
        byte[] data = adminService.exportMatchListAsTxt();
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=matches.txt");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(data);
        outputStream.flush();
    }

}
