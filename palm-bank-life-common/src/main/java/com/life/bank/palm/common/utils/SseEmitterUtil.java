package com.life.bank.palm.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SseEmitterUtil {

    private static final Map<String, SseEmitter> SSE_CACHE = new ConcurrentHashMap<>();

    /**
     * 创建SSE连接
     */
    public static SseEmitter create(String sessionId, Long timeout) {
        if (SSE_CACHE.containsKey(sessionId)) {
            SseEmitter oldEmitter = SSE_CACHE.get(sessionId);
            oldEmitter.complete();
        }

        SseEmitter emitter = new SseEmitter(timeout);
        SSE_CACHE.put(sessionId, emitter);

        emitter.onCompletion(() -> {
            log.info("SSE连接完成：{}", sessionId);
            SSE_CACHE.remove(sessionId);
        });

        emitter.onTimeout(() -> {
            log.info("SSE连接超时：{}", sessionId);
            SSE_CACHE.remove(sessionId);
        });

        emitter.onError(throwable -> {
            log.error("SSE连接异常：{}", sessionId, throwable);
            SSE_CACHE.remove(sessionId);
        });

        return emitter;
    }

    /**
     * 发送消息
     */
//    public static void send(String sessionId, String data) {
//        SseEmitter emitter = SSE_CACHE.get(sessionId);
//        if (emitter != null) {
//            try {
//                emitter.send(SseEmitter.event().data(data));
//            } catch (IOException e) {
//                log.error("发送SSE消息失败：{}", sessionId, e);
//                SSE_CACHE.remove(sessionId);
//            }
//        }
//    }

    public static boolean send(String sessionId, String data) {
        SseEmitter emitter = SSE_CACHE.get(sessionId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(data));
                return true;
            } catch (IOException e) {
                log.error("发送SSE消息失败：{}", sessionId, e);
                SSE_CACHE.remove(sessionId);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    // 忽略
                }
                return false;
            }
        }
        return false;
    }

    /**
     * 关闭连接
     */
    public static void close(String sessionId) {
        SseEmitter emitter = SSE_CACHE.get(sessionId);
        if (emitter != null) {
            emitter.complete();
            SSE_CACHE.remove(sessionId);
        }
    }
}