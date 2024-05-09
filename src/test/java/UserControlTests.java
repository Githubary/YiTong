/**
 * description:
 *
 * @author liuhuayang
 * date: 2024/5/9 16:24
 */
import com.alibaba.fastjson.JSON;
import org.example.model.AccessRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ApplicationTests.class)
class UserControlTests {
    private final String port = "8092";
    @Test
    void testAdminAddUser() {
        // 添加用户权限
        HttpHeaders headers = new HttpHeaders();
        String userInfo = "{\n" +
                "\"userId\":123456,\n" +
                "\"accountName\": \"XXXXXXX\",\n" +
                "\"role\": \"admin\"\n" +
                "}";
        String encodeUserInfo = Base64.getEncoder().encodeToString(userInfo.getBytes());
        System.out.println(encodeUserInfo);
        headers.add("Authorization",encodeUserInfo);
        headers.setContentType(MediaType.APPLICATION_JSON);
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setUserId(123456);
        accessRequest.setEndpoints(new String[]{"resourceD", "resourceC"});
        System.out.println(JSON.toJSONString(accessRequest));
        HttpEntity<AccessRequest> requestEntity = new HttpEntity<>(accessRequest, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> addResponse = restTemplate.exchange(
                "http://localhost:" + port + "/admin/addUser",
                HttpMethod.POST,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
    }

    @Test
    void testResource() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String userInfo = "{\n" +
                "\"userId\":123456,\n" +
                "\"accountName\": \"XXXXXXX\",\n" +
                "\"role\": \"admin\"\n" +
                "}";
        String encodeUserInfo = Base64.getEncoder().encodeToString(userInfo.getBytes());
        System.out.println(encodeUserInfo);
        headers.add("userinfo",encodeUserInfo);
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 检查用户权限
        ResponseEntity<String> checkResponse = restTemplate.exchange(
                "http://localhost:" + port + "/user/resourceA",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.OK, checkResponse.getStatusCode());
        assertEquals("User has access to resource resourceA", checkResponse.getBody());
    }


//    @Test
//    void testUserAddUser() {
//        HttpHeaders headers = new HttpHeaders();
//        String userInfo = "{\n" +
//                "\"userId\":234567,\n" +
//                "\"accountName\": \"XXXXXXX\",\n" +
//                "\"role\": \"user\"\n" +
//                "}";
//        String encodeUserInfo = Base64.getEncoder().encodeToString(userInfo.getBytes());
//        System.out.println(encodeUserInfo);
//        headers.add("Authorization",encodeUserInfo);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        AccessRequest accessRequest = new AccessRequest();
//        accessRequest.setUserId(234567);
//        accessRequest.setEndpoints(new String[]{"resourceA", "resourceB", "resourceC"});
//        System.out.println(JSON.toJSONString(accessRequest));
//        HttpEntity<AccessRequest> requestEntity = new HttpEntity<>(accessRequest, headers);
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> addResponse = restTemplate.exchange(
//                "http://localhost:" + port + "/admin/addUser",
//                HttpMethod.POST,
//                requestEntity,
//                String.class);
//        assertEquals(HttpStatus.UNAUTHORIZED, addResponse.getStatusCode());
//    }

}
