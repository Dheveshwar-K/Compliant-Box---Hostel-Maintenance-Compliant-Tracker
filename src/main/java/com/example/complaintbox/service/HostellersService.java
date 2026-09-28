package com.example.complaintbox.service;

import com.example.complaintbox.entity.Hosteller;
import com.example.complaintbox.repository.HostellersRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HostellersService {

    private final HostellersRepository hostellersRepository;

    public HostellersService(HostellersRepository hostellersRepository) {
        this.hostellersRepository = hostellersRepository;
    }

    // Create a new hosteller
    public Hosteller createHosteller(Hosteller hosteller) {

        if (hosteller.getName() == null ||
                hosteller.getName().trim().isEmpty()) {

            throw new RuntimeException("Hosteller name cannot be empty");
        }

        if (hosteller.getRoomNumber() == null ||
                hosteller.getRoomNumber().trim().isEmpty()) {

            throw new RuntimeException("Room number cannot be empty");
        }

        return hostellersRepository.save(hosteller);
    }

    // Get all hostellers
    public List<Hosteller> getAllHostellers() {

        return hostellersRepository.findAll();
    }

    // Get hosteller by ID
    public Hosteller getHostellerById(Long id) {

        return hostellersRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Hosteller not found with id: " + id
                        ));
    }

    // Update hosteller
    public Hosteller updateHosteller(
            Long id,
            Hosteller updatedHosteller) {

        Hosteller existingHosteller =
                hostellersRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Hosteller not found with id: " + id
                                ));

        if (updatedHosteller.getName() == null ||
                updatedHosteller.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Hosteller name cannot be empty"
            );
        }

        if (updatedHosteller.getRoomNumber() == null ||
                updatedHosteller.getRoomNumber().trim().isEmpty()) {

            throw new RuntimeException(
                    "Room number cannot be empty"
            );
        }

        existingHosteller.setName(
                updatedHosteller.getName()
        );

        existingHosteller.setRoomNumber(
                updatedHosteller.getRoomNumber()
        );

        return hostellersRepository.save(existingHosteller);
    }

    // Delete hosteller
    public String deleteHosteller(Long id) {

        Hosteller hosteller =
                hostellersRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Hosteller not found with id: " + id
                                ));

        hostellersRepository.delete(hosteller);

        return "Hosteller deleted successfully";
    }
}