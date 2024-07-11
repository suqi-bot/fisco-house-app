package com.suqi.service.impl;

import com.suqi.mapper.SwiperMapper;
import com.suqi.pojo.Swiper;
import com.suqi.service.SwiperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SwiperServiceImpl implements SwiperService {
    @Autowired
    SwiperMapper swiperMapper;
    @Override
    public List<Swiper> getSwiper() {
        return swiperMapper.list();
    }
}
