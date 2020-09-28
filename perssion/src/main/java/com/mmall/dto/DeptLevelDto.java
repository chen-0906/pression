package com.mmall.dto;

import com.google.common.collect.Lists;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author BaiYe
 * @create 2020-09-24 17:39
 */
@Data
public class DeptLevelDto extends SysDept {
    private List<DeptLevelDto> deptList = Lists.newArrayList();

    public static DeptLevelDto adapt(SysDept dept){
        DeptLevelDto deptLevelDto = new DeptLevelDto();
        BeanUtils.copyProperties(dept, deptLevelDto);
        return deptLevelDto;
    }
}
