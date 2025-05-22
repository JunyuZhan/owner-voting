package com.ownervoting.controller;

import com.ownervoting.model.entity.House;
import com.ownervoting.model.dto.HouseAddDTO;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.vo.HouseVO;
import com.ownervoting.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/house")
@Validated
public class HouseController {

    @Autowired
    private HouseService houseService;

    @PostMapping("/add")
    public ApiResponse<HouseVO> addHouse(@Valid @RequestBody HouseAddDTO dto) {
        House house = new House();
        house.setBuilding(dto.getBuilding());
        house.setUnit(dto.getUnit());
        house.setRoom(dto.getRoom());
        house.setAddress(dto.getAddress());
        house.setArea(dto.getArea());
        house.setCertificateNumber(dto.getCertificateNumber());
        house.setIsPrimary(dto.getIsPrimary());
        // owner和community应通过Service查找并set
        House saved = houseService.addHouse(house);
        HouseVO vo = toVO(saved);
        return ApiResponse.success(vo);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteHouse(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<HouseVO> getById(@PathVariable Long id) {
        House house = houseService.findById(id);
        if (house == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(house));
    }

    @GetMapping("/by-owner/{ownerId}")
    public ApiResponse<List<HouseVO>> getByOwnerId(@PathVariable Long ownerId) {
        List<House> list = houseService.findByOwnerId(ownerId);
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    private HouseVO toVO(House house) {
        HouseVO vo = new HouseVO();
        vo.setId(house.getId());
        vo.setOwnerId(house.getOwner() != null ? house.getOwner().getId() : null);
        vo.setCommunityId(house.getCommunity() != null ? house.getCommunity().getId() : null);
        vo.setBuilding(house.getBuilding());
        vo.setUnit(house.getUnit());
        vo.setRoom(house.getRoom());
        vo.setAddress(house.getAddress());
        vo.setArea(house.getArea());
        vo.setCertificateNumber(house.getCertificateNumber());
        vo.setIsPrimary(house.getIsPrimary());
        return vo;
    }
} 