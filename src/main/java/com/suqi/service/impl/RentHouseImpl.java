package com.suqi.service.impl;

import com.suqi.mapper.RentHouseMapper;
import com.suqi.pojo.RentHouse;
import com.suqi.service.RentHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentHouseImpl implements RentHouseService {

    @Autowired
    private RentHouseMapper rentHouseMapper;

    @Override
    public List<RentHouse> getAll() {
        return rentHouseMapper.getAll();
    }

    @Override
    public List<RentHouse> getRentHouseByAddress(String ownerAddress) {
        return rentHouseMapper.getRentHouseByAddress(ownerAddress);
    }

    @Override
    public Integer set(RentHouse house) {
        return rentHouseMapper.set(house);
    }

    @Override
    public Integer update(RentHouse house) {
        return rentHouseMapper.update(house);
    }

    @Override
    public Integer delete(Integer id, Integer houseId) {
        return rentHouseMapper.delete(id,houseId);
    }
}
