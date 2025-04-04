package org.example.store.payment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String API_SECRET_KEY = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R";

    // 결제버튼 클릭 시 임시저장 ajax 처리
    @PostMapping("/payment/temporary-save")
    @ResponseBody
    public Map<String, Boolean> temporarySave(@RequestBody SaveDto saveDto, HttpSession session) {
        log.info("saveDto == {}", saveDto); //폼데이터를 통해서 상품아이디, 멤버아이디,이메일도 들어온다
        session.removeAttribute("saveDto"); //기존 값 제거
        session.setAttribute("saveDto", saveDto); //새 값 저장
        return Map.of("isSaved", saveDto.getOrderId() != null);
    }

    // 위젯에서 결제를 거치면 여기로 넘어옴
    @RequestMapping("/confirm/widget")
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request,
                                                     @RequestBody String jsonBody) throws Exception {
        //사실 우리는 widget 시크릿 키만 씀
        String secretKey = request.getRequestURI().contains("/confirm/payment") ? API_SECRET_KEY : WIDGET_SECRET_KEY;
        JSONObject response
                = sendRequest(
                parseRequestData(jsonBody), secretKey, "https://api.tosspayments.com/v1/payments/confirm"
        );
        int statusCode = response.containsKey("error") ? 400 : 200;
        return ResponseEntity.status(statusCode).body(response);
    }

    // 제이슨 데이터 파싱
    private JSONObject parseRequestData(String jsonBody) {
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            log.error("JSON Parsing Error", e);
            return new JSONObject();
        }
    }

    // 제이슨 데이터 보내기
    private JSONObject sendRequest(JSONObject requestData, String secretKey,
                                   String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }
        try (InputStream responseStream
                     = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader
                     = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            log.error("Error reading response", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
        }
    }

    //커넥션 생성 >> api 에서 결제 검증이 필요하기 때문
    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(
                "Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":")
                        .getBytes(StandardCharsets.UTF_8))
        );
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

    @GetMapping("/payment/success")
    public String showSuccessPage(HttpSession session, Model model) {
        SaveDto saveDto = (SaveDto) session.getAttribute("saveDto");
        if (saveDto != null) {
            model.addAttribute("productId", saveDto.getProductId());
        }
        return "payment/success";
    }

    // 결제 실패 시 진입화면
    @GetMapping("/payment/fail")
    public String failPage(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("code", code);
        model.addAttribute("message", message);
        return "payment/fail";
    }


    // 결제 성공, 실패 내역 저장
    @PostMapping("/payment/resultSave")
    @ResponseBody
    public Map<String, Object> paymentSuccess(@RequestBody PaymentDto paymentDto,
                                              HttpSession session) {
        SaveDto saveDto = (SaveDto) session.getAttribute("saveDto"); //임시저장 값 가져오기
        log.info("success saveDto === {}", saveDto);
        if (saveDto == null) return Map.of("save", "saveDto 세션 값 없음");
        log.info("saveDto userId : {}", saveDto.getCustomerId());
        session.removeAttribute("saveDto");
        session.removeAttribute("saveDtoCreatedTime");

        boolean isSaved = paymentService.resultSave(paymentDto, saveDto);
        return Map.of("save", isSaved);
    }

    // 내 결제 내역
    @GetMapping("/payment/buyHistory")
    public String getBuyPayments(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        List<PaymentDto> paymentDtoList = paymentService.getBuyPayments(user);
        model.addAttribute("paymentList", paymentDtoList);
        return "payment/buyHistory";
    }
}
