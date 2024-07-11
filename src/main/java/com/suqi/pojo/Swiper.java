package com.suqi.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Swiper {
    @NotNull
    private Integer id;
    @NotEmpty
    private String name;
    private String context;
    @NotNull
    private String address;

}
