package com.example.guessgame.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.guessgame.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/daily-report")
    public AdminResponse getDailyReport(
            @RequestParam LocalDate date) {

        return adminService.getDailyReport(date);
    }

    @GetMapping("/user-report/{playerId}")
    public AdminUserResponse getUserReport(
            @PathVariable String playerId,
            @RequestParam LocalDate date) {

        return adminService.getUserReport(playerId, date);
    }
}