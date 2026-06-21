package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import org.website.dao.mapper.TagMapper;
import org.website.dao.mapper.entity.Tag;
import org.website.service.TagService;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}