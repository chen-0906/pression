package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysUser;
import com.mmall.param.UserParam;
import com.mmall.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author BaiYe
 * @create 2020-09-25 16:43
 */
@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysLogService sysLogService;

    public void save(UserParam param){

        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new ParamException("电话已被占用");
        }
        if(checkEmailExist(param.getMail(), param.getId())) {
            throw new ParamException("邮箱已被占用");
        }
        String password = PasswordUtil.randomPassword();
        password = "12345678";
        String encryptedPassword = MD5Util.encrypt(password);
        SysUser sysUser = SysUser.builder().username(param.getUsername()).telephone(param.getTelephone())
                .mail(param.getMail()).password(encryptedPassword).deptId(param.getDeptId()).status(param.getStatus())
                .remark(param.getRemark()).build();
        sysUser.setOperateIp(IpAddressUtil.getIpAddress(RequestHolder.getCurrentRequest()));
        sysUser.setOperateTime(new Date());
        sysUser.setOperator(RequestHolder.getCurrentUser().getUsername());
        //TODO sendEmail

        sysUserMapper.insertSelective(sysUser);
        sysLogService.saveUserLog(new SysUser(), sysUser);

    }

    public void update(UserParam param){
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new ParamException("电话已被占用");
        }
        if(checkEmailExist(param.getMail(), param.getId())) {
            throw new ParamException("邮箱已被占用");
        }
        SysUser before = sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");

        SysUser after = SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone())
                .mail(param.getMail()).password(before.getPassword()).deptId(param.getDeptId()).status(param.getStatus())
                .remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        //TODO
        after.setOperateIp(IpAddressUtil.getIpAddress(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveUserLog(before, after);

    }

    private boolean checkTelephoneExist(String telephone, Integer id) {

        return sysUserMapper.countByTelephone(telephone,id)>0;
    }

    public boolean checkEmailExist(String mail, Integer userId){
        return sysUserMapper.countByMail(mail, userId)>0;
    }

    public SysUser findByKeyWord(String keyword){
        SysUser byKeyWord = sysUserMapper.findByKeyWord(keyword);
        return byKeyWord;
    }

    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page){
        BeanValidator.check(page);
        int count = sysUserMapper.countByDeptId(deptId);
        if (count > 0){
            List<SysUser> list = sysUserMapper.getPageByDeptId(deptId, page);
            return PageResult.<SysUser>builder().data(list).total(count).build();
        }
        return PageResult.<SysUser>builder().build();
    }

    public List<SysUser> getAll() {
        return sysUserMapper.getAll();
    }
}
