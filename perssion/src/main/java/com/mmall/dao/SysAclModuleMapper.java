package com.mmall.dao;

import com.mmall.model.SysAclModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    int countByNameAndParentId(@Param("parentId") Integer parentId, @Param("aclModuleName") String aclModuleName, @Param("deptId") Integer deptId);

    List<SysAclModule> getChildAclModuleListByLevel(@Param("level") String level);

    void batchUpdateLevel(@Param("aclModuleList") List<SysAclModule> aclModuleList);

    List<SysAclModule> getAllAclModule();

    int countByParentId(@Param("aclModuleId") Integer aclModuleId);
}