package com.life.bank.palm.web.controller.community;

import com.life.bank.palm.common.context.UserContext;
import com.life.bank.palm.common.result.CommonResponse;
import com.life.bank.palm.dao.community.mapper.*;
import com.life.bank.palm.dao.community.pojo.*;
import com.life.bank.palm.dao.user.mapper.UserMapper;
import com.life.bank.palm.dao.user.pojo.UserPO;
import com.life.bank.palm.service.community.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "社区功能")
@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

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

    @Operation(summary = "发布帖子")
    @PostMapping("/post/publish")
    public CommonResponse<PublishResponse> publishPost(@RequestBody PublishRequest request) {
        Integer postId = communityService.publishPost(request.getTitle(), request.getContent());

        PublishResponse response = new PublishResponse();
        response.setPostId(postId);
        return CommonResponse.buildSuccess(response);
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping("/post/list")
    public CommonResponse<List<PostVO>> getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Integer offset = (page - 1) * pageSize;
        List<PostPO> posts = postMapper.selectList(offset, pageSize);

        // 获取当前用户的点赞和收藏状态
        Integer userId = UserContext.getUserId();
        List<Integer> likedPostIds = userLikeMapper.selectTargetIdsByUserIdAndType(userId, 1);
        List<Integer> collectedPostIds = userCollectMapper.selectPostIdsByUserId(userId);

        List<PostVO> voList = new ArrayList<>();
        for (PostPO post : posts) {
            PostVO vo = convertToPostVO(post);
            vo.setIsLiked(likedPostIds.contains(post.getId()));
            vo.setIsCollected(collectedPostIds.contains(post.getId()));
            voList.add(vo);
        }

        return CommonResponse.buildSuccess(voList);
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/post/{postId}")
    public CommonResponse<PostDetailVO> getPostDetail(@PathVariable Integer postId) {
        PostPO post = postMapper.selectById(postId);
        if (post == null) {
            return CommonResponse.buildError("帖子不存在");
        }

        // 增加浏览量
        postMapper.updateViewCount(postId);

        // 获取评论列表
        List<CommentPO> comments = commentMapper.selectByPostId(postId);

        // 获取用户信息
        List<Integer> userIds = new ArrayList<>();
        userIds.add(post.getUserId());
        userIds.addAll(comments.stream().map(CommentPO::getUserId).collect(Collectors.toList()));

        Map<Integer, UserPO> userMap = getUserMap(userIds);

        // 构建响应
        PostDetailVO vo = new PostDetailVO();
        vo.setPostId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setViewCount(post.getViewCount() + 1);
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setCollectCount(post.getCollectCount());
        vo.setCreateTime(post.getCreateTime());

        UserPO author = userMap.get(post.getUserId());
        if (author != null) {
            vo.setAuthorId(author.getId());
            vo.setAuthorName(author.getNickname());
            vo.setAuthorLogo(author.getLogo());
        }

        // 设置评论列表
        List<CommentVO> commentVOs = new ArrayList<>();
        for (CommentPO comment : comments) {
            CommentVO commentVO = convertToCommentVO(comment, userMap);
            commentVOs.add(commentVO);
        }
        vo.setComments(commentVOs);

        // 获取当前用户的点赞和收藏状态
        Integer userId = UserContext.getUserId();
        vo.setIsLiked(userLikeMapper.selectByUserIdAndTarget(userId, postId, 1) != null);
        vo.setIsCollected(userCollectMapper.selectByUserIdAndPostId(userId, postId) != null);

        return CommonResponse.buildSuccess(vo);
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/like")
    public CommonResponse<LikeResponse> toggleLike(@RequestBody LikeRequest request) {
        boolean isLiked = communityService.toggleLike(request.getTargetId(), request.getTargetType());

        LikeResponse response = new LikeResponse();
        response.setIsLiked(isLiked);
        return CommonResponse.buildSuccess(response);
    }

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/collect")
    public CommonResponse<CollectResponse> toggleCollect(@RequestBody CollectRequest request) {
        boolean isCollected = communityService.toggleCollect(request.getPostId());

        CollectResponse response = new CollectResponse();
        response.setIsCollected(isCollected);
        return CommonResponse.buildSuccess(response);
    }

    @Operation(summary = "发表评论")
    @PostMapping("/comment")
    public CommonResponse<CommentResponse> addComment(@RequestBody CommentRequest request) {
        Integer commentId = communityService.addComment(
                request.getPostId(),
                request.getParentId(),
                request.getContent()
        );

        CommentResponse response = new CommentResponse();
        response.setCommentId(commentId);
        return CommonResponse.buildSuccess(response);
    }

    // 辅助方法
    private PostVO convertToPostVO(PostPO post) {
        PostVO vo = new PostVO();
        vo.setPostId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent().length() > 100 ?
                post.getContent().substring(0, 100) + "..." : post.getContent());
        vo.setViewCount(post.getViewCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setCollectCount(post.getCollectCount());
        vo.setCreateTime(post.getCreateTime());

        // 获取作者信息
        UserPO author = userMapper.selectOneByIdAndIsDelete(post.getUserId(), 0);
        if (author != null) {
            vo.setAuthorId(author.getId());
            vo.setAuthorName(author.getNickname());
            vo.setAuthorLogo(author.getLogo());
        }

        return vo;
    }

    private CommentVO convertToCommentVO(CommentPO comment, Map<Integer, UserPO> userMap) {
        CommentVO vo = new CommentVO();
        vo.setCommentId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setLikeCount(comment.getLikeCount());
        vo.setCreateTime(comment.getCreateTime());
        vo.setParentId(comment.getParentId());

        UserPO user = userMap.get(comment.getUserId());
        if (user != null) {
            vo.setUserId(user.getId());
            vo.setUserName(user.getNickname());
            vo.setUserLogo(user.getLogo());
        }

        return vo;
    }

    private Map<Integer, UserPO> getUserMap(List<Integer> userIds) {
        // 实际应该批量查询，这里简化处理
        return userIds.stream()
                .distinct()
                .map(id -> userMapper.selectOneByIdAndIsDelete(id, 0))
                .filter(user -> user != null)
                .collect(Collectors.toMap(UserPO::getId, user -> user));
    }

    // 请求响应类
    @Data
    @Schema(description = "发布帖子请求")
    public static class PublishRequest {
        @Schema(description = "标题", example = "银行理财产品推荐")
        private String title;

        @Schema(description = "内容", example = "最近发现了一个不错的理财产品...")
        private String content;
    }

    @Data
    @Schema(description = "发布响应")
    public static class PublishResponse {
        @Schema(description = "帖子ID")
        private Integer postId;
    }

    @Data
    @Schema(description = "帖子VO")
    public static class PostVO {
        private Integer postId;
        private String title;
        private String content;
        private Integer viewCount;
        private Integer likeCount;
        private Integer commentCount;
        private Integer collectCount;
        private Integer authorId;
        private String authorName;
        private String authorLogo;
        private Boolean isLiked;
        private Boolean isCollected;
        private java.util.Date createTime;
    }

    @Data
    @Schema(description = "帖子详情VO")
    public static class PostDetailVO {
        private Integer postId;
        private String title;
        private String content;
        private Integer viewCount;
        private Integer likeCount;
        private Integer commentCount;
        private Integer collectCount;
        private Integer authorId;
        private String authorName;
        private String authorLogo;
        private Boolean isLiked;
        private Boolean isCollected;
        private java.util.Date createTime;
        private List<CommentVO> comments;
    }

    @Data
    @Schema(description = "评论VO")
    public static class CommentVO {
        private Integer commentId;
        private Integer userId;
        private String userName;
        private String userLogo;
        private String content;
        private Integer likeCount;
        private Integer parentId;
        private java.util.Date createTime;
    }

    @Data
    @Schema(description = "点赞请求")
    public static class LikeRequest {
        @Schema(description = "目标ID", example = "1")
        private Integer targetId;

        @Schema(description = "目标类型：1-帖子，2-评论", example = "1")
        private Integer targetType;
    }

    @Data
    @Schema(description = "点赞响应")
    public static class LikeResponse {
        @Schema(description = "是否点赞")
        private Boolean isLiked;
    }

    @Data
    @Schema(description = "收藏请求")
    public static class CollectRequest {
        @Schema(description = "帖子ID", example = "1")
        private Integer postId;
    }

    @Data
    @Schema(description = "收藏响应")
    public static class CollectResponse {
        @Schema(description = "是否收藏")
        private Boolean isCollected;
    }

    @Data
    @Schema(description = "评论请求")
    public static class CommentRequest {
        @Schema(description = "帖子ID", example = "1")
        private Integer postId;

        @Schema(description = "父评论ID，0表示一级评论", example = "0")
        private Integer parentId;

        @Schema(description = "评论内容", example = "说得很有道理！")
        private String content;
    }

    @Data
    @Schema(description = "评论响应")
    public static class CommentResponse {
        @Schema(description = "评论ID")
        private Integer commentId;
    }
}