package com.example.complaintbox.controller;

import com.example.complaintbox.entity.Hosteller;
import com.example.complaintbox.service.HostellersService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hostellers")
public class HostellersController {

    private final HostellersService hostellersService;

    public HostellersController(
            HostellersService hostellersService) {

        this.hostellersService = hostellersService;
    }

    @PostMapping
    public ResponseEntity<Hosteller> createHosteller(
            @Valid @RequestBody Hosteller hosteller) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        hostellersService
                                .createHosteller(hosteller)
                );
    }

    @GetMapping
    public ResponseEntity<List<Hosteller>>
    getAllHostellers() {

        return ResponseEntity.ok(
                hostellersService.getAllHostellers()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hosteller>
    getHostellerById(@PathVariable Long id) {

        return ResponseEntity.ok(
                hostellersService.getHostellerById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hosteller>
    updateHosteller(
            @PathVariable Long id,
            @Valid @RequestBody Hosteller hosteller) {

        return ResponseEntity.ok(
                hostellersService
                        .updateHosteller(id, hosteller)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteHosteller(@PathVariable Long id) {

        return ResponseEntity.ok(
                hostellersService.deleteHosteller(id)
        );
    }
}