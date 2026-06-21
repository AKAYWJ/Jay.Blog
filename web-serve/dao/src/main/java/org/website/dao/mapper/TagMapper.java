package org.website.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.website.dao.mapper.entity.Tag;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}