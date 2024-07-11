package com.suqi.mapper;

import com.suqi.pojo.House;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HouseMapper {
    //查询所有
    @Select("select * from house")
    List<House> getAll();

    @Select("select * from house where id = #{id}")
    House getHouseById(Integer id);

    @Insert("insert into house(id,house_name,house_context,state,house_img,house_price,owner_address,owner_name)" +
            "values(#{id},#{houseName},#{houseContext},#{state},#{houseImg},#{housePrice},#{ownerAddress},#{ownerName})")
    Integer set(House house);
    @Update("update house set house_name=#{houseName} house_context=#{houseContext} state=#{state} house_img=#{houseImg}" +
            "house_price=#{housePrice} where id=#{id} and owner_address=#{ownerAddress}")
    Integer update(House house);
    @Delete("delete from house where id=#{id} and owner_address=#{ownerAddress}")
    Integer delete(String ownerAddress, Integer houseId);

}
