package org.example.store.payment;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")  //정적 리소스 접근 URL 패턴
                .addResourceLocations("classpath:/templates/","classpath:/static/")  //실제 정적 파일이 위치한 경로
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES)); //이건 뭐하는거지
    }
}

// 결제내역 확인
//`https://dashboard.tosspayments.com/receipt/redirection?transactionId=${urlParams.get("paymentKey")}&ref=PX`
