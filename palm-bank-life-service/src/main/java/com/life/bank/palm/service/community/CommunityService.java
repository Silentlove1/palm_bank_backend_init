package com.life.bank.palm.service.community;

import com.life.bank.palm.common.context.UserContext;
import com.life.bank.palm.common.exception.CommonBizException;
import com.life.bank.palm.common.utils.CheckUtil;
import com.life.bank.palm.dao.community.mapper.*;
import com.life.bank.palm.dao.community.pojo.*;
import com.life.bank.palm.dao.user.mapper.UserMapper;
import com.life.bank.palm.dao.user.pojo.UserPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class CommunityService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserLikeMapper userLikeMapper;

    @Autowired
    private UserCollectMapper userCollectMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发布帖子
     */
    public Integer publishPost(String title, String content) {
        CheckUtil.Biz.INSTANCE
                .strNotBlank(title, "标题不能为空")
                .isTrue(title.length() <= 100, "标题不能超过100字")
                .strNotBlank(content, "内容不能为空")
                .isTrue(content.length() <= 5000, "内容不能超过5000字");

        Integer userId = UserContext.getUserId();

        PostPO post = new PostPO();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setStatus(1); // 已发布

        postMapper.insertSelective(post);

        log.info("发布帖子成功：userId={}, postId={}", userId, post.getId());

        return post.getId();
    }

    /**
     * 点赞/取消点赞
     */
    @Transactional
    public boolean toggleLike(Integer targetId, Integer targetType) {
        Integer userId = UserContext.getUserId();

        // 查询是否已点赞
        UserLikePO existLike = userLikeMapper.selectByUserIdAndTarget(userId, targetId, targetType);

        if (existLike != null) {
            // 取消点赞
            userLikeMapper.deleteById(existLike.getId());

            // 更新点赞数
            if (targetType == 1) {
                PostPO post = postMapper.selectById(targetId);
                if (post != null) {
                    postMapper.updateCounts(targetId, post.getLikeCount() - 1, null, null);
                }
            } else if (targetType == 2) {
                CommentPO comment = commentMapper.selectById(targetId);
                if (comment != null) {
                    commentMapper.updateLikeCount(targetId, comment.getLikeCount() - 1);
                }
            }

            return false; // 取消点赞
        } else {
            // 添加点赞
            UserLikePO like = new UserLikePO();
            like.setUserId(userId);
            like.setTargetId(targetId);
            like.setTargetType(targetType);
            userLikeMapper.insertSelective(like);

            // 更新点赞数
            if (targetType == 1) {
                PostPO post = postMapper.selectById(targetId);
                if (post != null) {
                    postMapper.updateCounts(targetId, post.getLikeCount() + 1, null, null);
                }
            } else if (targetType == 2) {
                CommentPO comment = commentMapper.selectById(targetId);
                if (comment != null) {
                    commentMapper.updateLikeCount(targetId, comment.getLikeCount() + 1);
                }
            }

            return true; // 点赞成功
        }
    }

    /**
     * 收藏/取消收藏
     */
    @Transactional
    public boolean toggleCollect(Integer postId) {
        Integer userId = UserContext.getUserId();

        // 查询是否已收藏
        UserCollectPO existCollect = userCollectMapper.selectByUserIdAndPostId(userId, postId);

        if (existCollect != null) {
            // 取消收藏
            userCollectMapper.deleteById(existCollect.getId());

            // 更新收藏数
            PostPO post = postMapper.selectById(postId);
            if (post != null) {
                postMapper.updateCounts(postId, null, null, post.getCollectCount() - 1);
            }

            return false; // 取消收藏
        } else {
            // 添加收藏
            UserCollectPO collect = new UserCollectPO();
            collect.setUserId(userId);
            collect.setPostId(postId);
            userCollectMapper.insertSelective(collect);

            // 更新收藏数
            PostPO post = postMapper.selectById(postId);
            if (post != null) {
                postMapper.updateCounts(postId, null, null, post.getCollectCount() + 1);
            }

            return true; // 收藏成功
        }
    }

    /**
     * 发表评论
     */
    @Transactional
    public Integer addComment(Integer postId, Integer parentId, String content) {
        CheckUtil.Biz.INSTANCE
                .strNotBlank(content, "评论内容不能为空")
                .isTrue(content.length() <= 500, "评论内容不能超过500字");

        Integer userId = UserContext.getUserId();

        // 验证帖子存在
        PostPO post = postMapper.selectById(postId);
        CheckUtil.Biz.INSTANCE.noNull(post, "帖子不存在");

        // 如果是回复评论，验证父评论存在
        if (parentId != null && parentId > 0) {
            CommentPO parentComment = commentMapper.selectById(parentId);
            CheckUtil.Biz.INSTANCE.noNull(parentComment, "回复的评论不存在");
        }

        CommentPO comment = new CommentPO();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(parentId == null ? 0 : parentId);
        comment.setContent(content);

        commentMapper.insertSelective(comment);

        // 更新帖子评论数
        postMapper.updateCounts(postId, null, post.getCommentCount() + 1, null);

        return comment.getId();
    }
}