package com.suqi.service;

import com.suqi.pojo.House;

import java.util.List;

public interface HouseService {
    List<House> getAll();

    House getHouseById(Integer id);
    Integer set(House house);

    Integer update(House house);
    Integer delete(String ownerAddress, Integer houseId);

    List<House> getHouseByUserAddress(String ownerAddress);
}
