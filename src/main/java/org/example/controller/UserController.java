package org.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.example.enumerage.Role;
import org.example.model.AccessRequest;
import org.example.model.UserAccess;
import org.example.utils.IOUtil;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * description:
 *
 * @author liuhuayang
 * date: 2024/5/9 16:18
 */
@Slf4j
@RestController
@EnableScheduling
public class UserController {

    private final Map<Integer, UserAccess> accessMap;
    private final Map<Integer, UserAccess> hotAccessMap;

    private static final String DB_PATH = "/Users/liuhuayang/Documents/project/self/YiTong/Yitong/src/main/resources/file/UserRoleStore";

    public UserController() {
        accessMap = loadAccessMapFromFile();
        hotAccessMap = new HashMap<>();
        log.info("accessMap:{}",JSON.toJSONString(accessMap));
        log.info("hotAccessMap:{}",JSON.toJSONString(hotAccessMap));
    }

    @PostMapping("/admin/addUser")
    public ResponseEntity<String> addUserAccess(@RequestBody AccessRequest accessRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admin can access this endpoint.");
        }
        int userId = accessRequest.getUserId();
        if (queryUser(userId) != null) {
            return ResponseEntity.status(HttpStatus.OK).body("this user is existed");
        }
        try {
            saveAccessMapToFile(JSON.toJSONString(accessRequest));
            accessMap.put(userId, new UserAccess(userId, accessRequest.getEndpoints()));
            // 更新热点访问数据
            hotAccessMap.put(userId, accessMap.get(userId));
        } catch (Exception e) {
            log.error("添加用户发生异常：request:{}，error:", JSON.toJSONString(request), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("system error");
        }
        return ResponseEntity.ok("Access added for user " + userId);
    }

    @GetMapping("/user/{resource}")
    public ResponseEntity<String> checkUserAccess(@PathVariable String resource, HttpServletRequest request) {
        int userId = getUserId(request);
        if (userId == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization header.");
        }
        UserAccess userAccess = queryUser(userId);
        if (userAccess != null && userAccess.hasAccess(resource)) {
            userAccess.incrementAccessCount(); // 增加访问计数
            return ResponseEntity.ok("User has access to resource " + resource);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have access to resource " + resource);
        }
    }

    @Scheduled(fixedDelay = 60000) // 每分钟清理一次热点访问数据
    public void cleanupHotAccessData() {
        // 清理访问次数较低的数据
        hotAccessMap.entrySet().removeIf(entry -> entry.getValue().getAccessCount() < 3); // 举例：访问次数小于3的数据会被清理
        if(hotAccessMap.isEmpty()){
            return;
        }
        log.info("清理后，剩余的内容:{}",JSON.toJSONString(hotAccessMap));
        Set<Map.Entry<Integer, UserAccess>> entries = hotAccessMap.entrySet();
        for (Map.Entry<Integer, UserAccess> entry : entries) {
            UserAccess value = entry.getValue();
            value.decreaseAccessCount();
        }
    }

    public UserAccess queryUser(int userId){
        UserAccess userAccess = hotAccessMap.get(userId);
        if(userAccess==null){
            userAccess = accessMap.get(userId);
        }
        return userAccess;
    }

    private boolean isAdmin(HttpServletRequest request) {
        // 解码Header并检查角色是否为admin
        // 使用Base64解码authHeader，然后检查是否包含"role":"admin"
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(request.getHeader("Authorization"));
            String decodedString = new String(decodedBytes);
            Map<String, String> userInfo = JSON.parseObject(decodedString, new TypeReference<HashMap<String, String>>() {
            });
            return Role.ADMIN.getValue().equals(userInfo.get("role"));
        } catch (Exception e) {
            log.error("isAdmin：鉴权发生异常：error:", e);
            return false;
        }
    }

    private int getUserId(HttpServletRequest request) {
        if (request.getHeader("Authorization") == null) {
            return -1;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(request.getHeader("Authorization"));
            String decodedString = new String(decodedBytes);
            Map<String, Object> userInfo = JSON.parseObject(decodedString, new TypeReference<HashMap<String, Object>>() {
            });
            return (int) userInfo.get("userId");
        } catch (Exception e) {
            log.error("getUserId：鉴权发生异常：error:", e);
            return -1;
        }
    }

    private Map<Integer, UserAccess> loadAccessMapFromFile() {
        try {
            String json = IOUtil.readJsonFromFile(DB_PATH);
            if (StringUtils.isBlank(json)) {
                return new HashMap<>();
            }
            String[] split = json.split(";");
            Map<Integer, UserAccess>  map = new HashMap<>();
            for (String s : split) {
                if(StringUtils.isBlank(s)){
                    continue;
                }
                AccessRequest accessRequest = JSON.parseObject(s, AccessRequest.class);
                map.put(accessRequest.getUserId(),new UserAccess(accessRequest.getUserId(),accessRequest.getEndpoints()));
            }
            return map;
        } catch (Exception e) {
            log.error("数据库读取异常。error:", e);
            return new HashMap<>();
        }
    }

    private void saveAccessMapToFile(String content) {
        IOUtil.writeJson2File(DB_PATH, content);
    }

}
