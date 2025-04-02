package com.devStudy.oauth2.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomOidcUserInfoService {

    private final UserService userService;

    @Autowired
    public CustomOidcUserInfoService(UserService userService) {
        this.userService = userService;
    }

    public Map<String,Object> loadUserInfoForIDToken(String email){
        return userService.loadUserInfoByEmail(email)
                .entrySet()
                .stream()
                .filter(entry -> "firstName".equals(entry.getKey()) || "lastName".equals(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
    }

    public OidcUserInfo loadUserInfo(String email){
        Map<String, Object> userInfo = userService.loadUserInfoByEmail(email);
//        return new OidcUserInfo(userInfo);
        return OidcUserInfo.builder()
                .subject(email)
                .name(userInfo.get("lastName") + " " + userInfo.get("firstName"))
                .givenName((String) userInfo.get("firstName"))
                .familyName((String) userInfo.get("lastName"))
                .email(email)
                .emailVerified(true)
                .gender((String)userInfo.get("gender"))
                .birthdate((String)userInfo.get("dateOfBirth"))
                .phoneNumber( (String)userInfo.get("phone"))
                .address((String)userInfo.get("address"))
                .zoneinfo("Europe/Paris")
                .build();
    }
}
