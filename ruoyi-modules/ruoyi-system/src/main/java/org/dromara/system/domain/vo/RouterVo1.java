package org.dromara.system.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class RouterVo1 {

    List<RouterVo> routes;

    String home;
}
