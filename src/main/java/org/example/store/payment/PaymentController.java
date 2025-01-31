package org.example.store.payment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private final String prefix = "/payment";
    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String API_SECRET_KEY = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R";

    // 구매화면 진입
    @GetMapping("/payment/widget/checkout")
    public String checkout(Model model) {
        model.addAttribute("orderId", UUID.randomUUID().toString());
        return prefix + "/widget/checkout";
    }

    // 결제버튼 클릭 시 임시저장 ajax 처리
    @PostMapping("/payment/temporary-save")
    @ResponseBody
    public Map<String, String> temporarySave(@RequestBody SaveDto saveDto, HttpSession session) {
        Map<String, String> map = new HashMap<>();
        map.put("데이터보내졌을까", "오케이.");
        // 로그인 계정까지
        log.info(saveDto.toString());
        session.setAttribute("saveDto", saveDto); //세션은 성공 or 실패 시에
        return map;
    }

    // 위젯에서 결제를 거치면 여기로 넘어옴
    @RequestMapping("/confirm/widget")
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {
        log.info("여기는 컨펌 위젯, 컨펌 페이먼트");
        log.info("jsonBody: {}", jsonBody);
        //사실 우리는 widget 시크릿 키만 씀
        String secretKey = request.getRequestURI().contains("/confirm/payment") ? API_SECRET_KEY : WIDGET_SECRET_KEY;
        log.info("secretKey: {}", secretKey);
        JSONObject response = sendRequest(parseRequestData(jsonBody), secretKey, "https://api.tosspayments.com/v1/payments/confirm");
        int statusCode = response.containsKey("error") ? 400 : 200;
        return ResponseEntity.status(statusCode).body(response);
    }

    // 제이슨 데이터 파싱
    private JSONObject parseRequestData(String jsonBody) {
        log.info("파스 리퀘스트 데이터");
        log.info("jsonBody: {}", jsonBody);
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            log.error("JSON Parsing Error", e);
            return new JSONObject();
        }
    }

    // 제이슨 데이터 보내기
    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        log.info("센드 리퀘스트");
        log.info("requestData: {}", requestData);
        log.info("secretKey: {}", secretKey);
        log.info("urlString: {}", urlString);
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
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
        log.info("크리에이트 커넥션");
        log.info("secretKey: {}", secretKey);
        log.info("urlString: {}", urlString);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

    // 결제 실패 시 진입화면, 성공화면은 토스페이먼츠 api 에서 자동으로 띄워줌
    @GetMapping("/fail")
    public String failPayment(HttpServletRequest request, Model model) {
        log.info("여기는 실패");
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));
        return prefix + "/fail";
    }

    // 결제 성공, 실패 내역 저장
    @PostMapping("/payment/resultSave")
    @ResponseBody
    public Map<String, Object> paymentSuccess(@RequestBody PaymentDto paymentDto, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        //임시저장 값 가져오기
        SaveDto saveDto = (SaveDto) session.getAttribute("saveDto");
        log.info("saveDto userId : {}",saveDto.getCustomerId());
        if (paymentService.resultSave(paymentDto, saveDto)) result.put("success", true);
        else result.put("success", false);
        session.removeAttribute("saveDto");
        return result;
    }

    // 나중에 바꿀거임 어센틱 어쩌구로
    // 결제 내역 (뿌릴 때 성공 실패에 따라 값 다르게 주기) ex) 결제 실패한 내역만 "결제실패" 빨간색텍스트 처리 등
    @GetMapping("/payment/getPayments/{userId}")
    public List<PaymentDto> getPayments(@PathVariable String userId) {
        Member member = Member.builder().build();
        return paymentService.getPayments(member);
    }

    // 나중에 바꿀거임 어센틱 어쩌구로 // 내 판매 내역 받아오기
    @GetMapping("/payment/sellHistory/{userId}")
    public List<PaymentDto> getSellPayments(@PathVariable String userId) {
        Member member = Member.builder().build();
        return paymentService.getSellPayments(member);
    }

    // 나중에 바꿀거임 어센틱 어쩌구로 // 내 구매 내역 받아오기
    @GetMapping("/payment/buyHistory/{userId}")
    public List<PaymentDto> getBuyPayments(@PathVariable String userId) {
        Member member = Member.builder().build();
        return paymentService.getBuyPayments(member);
    }
}
