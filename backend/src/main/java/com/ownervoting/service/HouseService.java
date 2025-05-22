package com.ownervoting.service;

import com.ownervoting.model.entity.House;
import java.util.List;

public interface HouseService {
    House addHouse(House house);
    void deleteHouse(Long id);
    House findById(Long id);
    List<House> findByOwnerId(Long ownerId);
} 