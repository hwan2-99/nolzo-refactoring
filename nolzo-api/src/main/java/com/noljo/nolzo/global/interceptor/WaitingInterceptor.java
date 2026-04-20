//package com.noljo.nolzo.global.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.time.Duration;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//@RequiredArgsConstructor
//public class WaitingInterceptor implements HandlerInterceptor {
//    private final StringRedisTemplate redisTemplate;
//
//    private static final int ALLOWED_COUNT = 100;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request,
//                             HttpServletResponse response,
//                             Object handler) throws Exception {
//
//        String eventId = extractEventId(request);
//        String userId = getUserId(request);
//
//        String entryKey = "entry:token:" + userId + ":" + eventId;
//
//        // 1️⃣ 이미 입장 허용된 사용자면 통과
//        if (Boolean.TRUE.equals(redisTemplate.hasKey(entryKey))) {
//            return true;
//        }
//
//        String waitingKey = "waiting:event:" + eventId;
//
//        // 2️⃣ 대기열에 없으면 등록
//        redisTemplate.opsForZSet()
//                .add(waitingKey, userId, System.currentTimeMillis());
//
//        Long rank = redisTemplate.opsForZSet()
//                .rank(waitingKey, userId);
//
//        if (rank != null && rank < ALLOWED_COUNT) {
//
//            // 3️⃣ 입장 허용
//            redisTemplate.opsForZSet().remove(waitingKey, userId);
//
//            redisTemplate.opsForValue()
//                    .set(entryKey, "true", Duration.ofMinutes(5));
//
//            return true;
//        }
//
//        // 4️⃣ 아직 대기 중
//        response.setStatus(429); // Too Many Requests
//        response.getWriter().write("Waiting... your rank: " + rank);
//        return false;
//    }
//}
