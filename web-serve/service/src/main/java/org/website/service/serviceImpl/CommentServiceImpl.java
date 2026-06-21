package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.website.dao.mapper.CommentMapper;
import org.website.dao.mapper.entity.Comment;
import org.website.service.CommentService;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}