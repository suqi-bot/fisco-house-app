package com.suqi.controller;

import com.suqi.pojo.RentHouse;
import com.suqi.pojo.Result;
import com.suqi.service.RentHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/renthouse")
public class RentHouseController {
    @Autowired
    private RentHouseService rentHouseService;

    @GetMapping("/getAll")
    public Result<List<RentHouse>> getAll(){
      return Result.success(rentHouseService.getAll());
    }

    @GetMapping("/getrentbyaddress")
    public Result<List<RentHouse>> getRentByAddress(String ownerAddress){
        return Result.success(rentHouseService.getRentHouseByAddress(ownerAddress));
    }

    @PostMapping
    public Result<Integer> SetRentHouse(@RequestBody RentHouse house){
        return Result.success(rentHouseService.set(house));
    }

    @PutMapping
    public Result<Integer> update(@RequestBody RentHouse house){
        return Result.success(rentHouseService.update(house));
    }

    @DeleteMapping
    public Result<Integer> delete(Integer id, Integer houseId){
        return Result.success(rentHouseService.delete(id,houseId));
    }
}
