package com.suqi.service.impl;

import com.suqi.mapper.HouseMapper;
import com.suqi.pojo.House;
import com.suqi.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Override
    public List<House> getAll() {
        return houseMapper.getAll();
    }

    @Override
    public House getHouseById(Integer id) {
        return houseMapper.getHouseById(id);
    }

    @Override
    public Integer set(House house) {
        return houseMapper.set(house);
    }

    @Override
    public Integer update(House house) {
        return houseMapper.update(house);
    }

    @Override
    public Integer delete(String ownerAddress, Integer houseId) {
        return houseMapper.delete(ownerAddress, houseId);
    }

    @Override
    public List<House> getHouseByUserAddress(String ownerAddress) {
        return houseMapper.getHouseByUserAddress(ownerAddress);
    }
}
