package org.example.store.payment;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProxyController {

    @GetMapping(value = "/external/kakaopay-css", produces = "text/css")
    public ResponseEntity<ByteArrayResource> getKakaoPayCss() {
        String externalCssUrl = "https://t1.kakaocdn.net/online_payment/online-payment-pc-bridge-client/3c296bcc363f6110d94150d63869bd3dcb442001/static/index-78371517.css";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(externalCssUrl, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            ByteArrayResource resource = new ByteArrayResource(response.getBody());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("text/css"));
            // CORS 헤더 추가
            headers.add("Access-Control-Allow-Origin", "*");
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }
}