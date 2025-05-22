package com.ownervoting.service.impl;

import com.ownervoting.model.entity.House;
import com.ownervoting.repository.HouseRepository;
import com.ownervoting.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private HouseRepository houseRepository;

    @Override
    @Transactional
    public House addHouse(House house) {
        return houseRepository.save(house);
    }

    @Override
    @Transactional
    public void deleteHouse(Long id) {
        houseRepository.deleteById(id);
    }

    @Override
    public House findById(Long id) {
        return houseRepository.findById(id).orElse(null);
    }

    @Override
    public List<House> findByOwnerId(Long ownerId) {
        return houseRepository.findAll().stream()
                .filter(h -> h.getOwner() != null && h.getOwner().getId().equals(ownerId))
                .toList();
    }
} 