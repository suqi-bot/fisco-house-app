package com.suqi.service;

import com.suqi.pojo.RentHouse;

import java.util.List;

public interface RentHouseService {
    List<RentHouse> getAll();

    List<RentHouse> getRentHouseByAddress(String ownerAddress);

    Integer set(RentHouse house);

    Integer update(RentHouse house);

    Integer delete(Integer id, Integer houseId);
}
