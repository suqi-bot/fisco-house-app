package com.suqi.controller;

import com.suqi.pojo.Result;
import com.suqi.pojo.Swiper;
import com.suqi.service.SwiperService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/swiper")
public class SwiperController {
    @Autowired
    private SwiperService swiperService;

    @GetMapping
    public Result<List<Swiper>> getSwiper(){

        return Result.success(swiperService.getSwiper());
    }
}
