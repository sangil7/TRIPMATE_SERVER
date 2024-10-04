package vc.voyageconnect.login.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import vc.voyageconnect.login.dto.MemberDTO;

import java.util.Map;

@Slf4j
@Controller
public class LoginController {

    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.client_secret}")
    private String client_secret;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    @Value("${kakao.token_uri}")
    private String token_uri;

    @Value("${kakao.user_info_uri}")
    private String user_info_uri;

    // 사용자 정보를 저장할 객체

    @GetMapping("/")
    public String loginPage(Model model) {
        // 카카오 로그인 URL 생성 및 모델에 추가
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirect_uri;
        model.addAttribute("location", location);

        return "login"; // login.html 페이지
    }

    // 인가 코드를 받아오는 콜백 메서드
    @GetMapping("/callback")
    public String callback(@RequestParam String code) {
        // 인가 코드를 이용해 액세스 토큰 발급
        String accessToken = getAccessToken(code);

        if (accessToken != null) {
            MemberDTO memberDTO = getUserInfoFromKakao(accessToken);
            if (memberDTO != null) {
                log.info("KakaoUserId={}", memberDTO.getId());
                log.info("KakoaUserNickName={}", memberDTO.getNickName());

                /**
                 * DB에서 userId 찾아서 있으면 바로 로그인 처리
                 * 없으면 설문조사 하는 화면으로 간 뒤 설문조사 -> 회원 가입 처리
                 * 만약 설문조사 창을 닫았으면 TravelTendency == null 일테니
                 * 로그인 과정 시 TravelTendency == null 이면 설문조사 화면으로 가게?
                 */

                return "login";
            }
        }
        return "login";
    }

    // 인가 코드를 이용해 액세스 토큰을 요청하는 메서드
    private String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  // application/x-www-form-urlencoded 설정

        // 요청 파라미터를 설정할 때 MultiValueMap을 사용해야 함
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        params.add("redirect_uri", redirect_uri);
        params.add("code", code);
        params.add("client_secret", client_secret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    token_uri,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            // 토큰 정보에서 access_token 추출
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    return (String) responseBody.get("access_token");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 액세스 토큰을 사용하여 사용자 정보를 가져오는 메서드 (사용자 ID와 닉네임 추출)
    private MemberDTO getUserInfoFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    user_info_uri,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    // 카카오 사용자 ID
                    String userId = responseBody.get("id").toString();

                    // 카카오 사용자 닉네임
                    Map<String, Object> properties = (Map<String, Object>) responseBody.get("properties");
                    String nickname = (String) properties.get("nickname");

                    // MemberDTO 객체 생성 및 값 설정
                    return new MemberDTO(userId, nickname);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
