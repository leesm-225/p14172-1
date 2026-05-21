package com.back.global.aspect;

import com.back.global.rsData.RsData;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResponseAspect {

    private final HttpServletResponse response;

    public ResponseAspect(HttpServletResponse response) {
        this.response = response;
    }

    @Around("""
                (
                    within(@org.springframework.web.bind.annotation.RestController *) &&
                    (
                        @annotation(org.springframework.web.bind.annotation.GetMapping) ||
                        @annotation(org.springframework.web.bind.annotation.PostMapping) ||
                        @annotation(org.springframework.web.bind.annotation.PutMapping) ||
                        @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
                        @annotation(org.springframework.web.bind.annotation.RequestMapping)
                    )
                ) ||
                @annotation(org.springframework.web.bind.annotation.ResponseBody)
            """)
    public Object handleResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        // 원래 메서드 실행
        Object proceed = joinPoint.proceed(); //프록시

        // RsData 타입이면 상태 코드 설정
        if (proceed instanceof RsData) {//프록시의 전처리코드
            RsData<?> rsData = (RsData<?>) proceed;//타입을 지정하지 않고 무시하겠다.
            response.setStatus(rsData.statusCode());
        }

        return proceed;
    }
}