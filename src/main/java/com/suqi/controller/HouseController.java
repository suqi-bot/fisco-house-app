package com.suqi.controller;

import com.suqi.pojo.House;
import com.suqi.pojo.Result;
import com.suqi.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/house")
public class HouseController {
    @Autowired
    private HouseService houseService;

    @GetMapping("/getAll")
    public Result<List<House>> getAllHouse(){
       List<House> houses = houseService.getAll();
       return Result.success(houses);
    }

    @GetMapping("/getbyid")
    public Result<House> getHouseById(Integer id){
        House house = houseService.getHouseById(id);
        return Result.success(house);
    }

    @GetMapping("/getbyuseraddress")
    public Result<List<House>> getHouseByUserAddress(String ownerAddress){
        List<House> house = houseService.getHouseByUserAddress(ownerAddress);
        return Result.success(house);
    }
    @PostMapping
    public Result<Integer> setHouse(House house){
        Integer res = houseService.set(house);
        return Result.success(res);
    }

    @PutMapping
    public Result<Integer> updateHouse(@RequestBody House house){
        Integer res = houseService.update(house);
        return Result.success(res);
    }

    @DeleteMapping
    public Result<Integer> deleteHouse(String ownerAddress, Integer houseId){
        Integer res = houseService.delete(ownerAddress, houseId);
        return Result.success(res);
    }
}
