package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpAddressUtil;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.net.util.IPAddressUtil;
import sun.security.x509.IPAddressName;

import javax.annotation.Resource;
import javax.validation.constraints.Null;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author BaiYe
 * @create 2020-09-24 17:12
 */
@Service
public class SysDeptService {
    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysLogService sysLogService;

    public void save(DeptParam param){
        BeanValidator.check(param);
        if (checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept dept = SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        dept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        //TODO
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());
        //TODO
        dept.setOperateIp(IpAddressUtil.getIpAddress(RequestHolder.getCurrentRequest()));

        dept.setOperateTime(new Date());
        sysDeptMapper.insert(dept);
        sysLogService.saveDeptLog(new SysDept(), dept);
    }


    private boolean checkExist(Integer parentId, String deptName, Integer deptId){

        return sysDeptMapper.countByNameAndParentId(parentId,deptName,deptId) > 0;
    }

    private String getLevel(Integer deptId){
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if (dept == null){
            return null;
        }
        return dept.getLevel();
    }

    public void update(DeptParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"代更新的部门不存在");


        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();

        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));

        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpAddressUtil.getIpAddress(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());

        updateWithChild(before,after);

    }

    @Transactional()
    private void updateWithChild(SysDept before, SysDept after){

        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();

        if (!after.getLevel().equals(before.getLevel())){
            List<SysDept> deptList =sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if (CollectionUtils.isNotEmpty(deptList)) {
                for (SysDept sysDept : deptList) {
                    String level = sysDept.getLevel();
                    if ((level.indexOf(oldLevelPrefix)) == 0){
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        sysDept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);
        sysLogService.saveDeptLog(before, after);
    }

    public void delete(int deptId) {
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        Preconditions.checkNotNull(dept, "待删除的部门不存在，无法删除");
        if (sysDeptMapper.countByParentId(dept.getId()) > 0) {
            throw new ParamException("当前部门下面有子部门，无法删除");
        }
        if(sysUserMapper.countByDeptId(dept.getId()) > 0) {
            throw new ParamException("当前部门下面有用户，无法删除");
        }
        sysDeptMapper.deleteByPrimaryKey(deptId);
    }
}
