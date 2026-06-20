package org.website.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.website.dao.mapper.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}