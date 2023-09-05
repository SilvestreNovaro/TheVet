package com.example.veterinaria.service;

import com.example.veterinaria.entity.AvailabilitySlot;
import com.example.veterinaria.repository.AvailabilitySlotRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AvailabilitySlotService {

    private final AvailabilitySlotRepository availableSlotRepository;


    public AvailabilitySlot add(AvailabilitySlot availabilitySlot){
        return availableSlotRepository.save(availabilitySlot);
    }
}
