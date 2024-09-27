package com.acoldbottle.todolist.controller;

import com.acoldbottle.todolist.service.RefreshService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * access 토큰이 만료되었을 때, 헤더에 access 토큰 대신 refresh 토큰을 담아서 /reissue에 요청.
 * access 토큰 재발급
 *
 * access 토큰 만료시간 -> 10분
 * refresh 토큰 만료시간 -> 14일
 */
@RestController
@RequiredArgsConstructor
public class RefreshController {

    private final RefreshService refreshService;


    @PostMapping("/reissue")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        refreshService.reissue(request, response);
    }
}
