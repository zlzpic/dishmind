package com.bdu.dishmind.controller;

import com.bdu.dishmind.dto.ApiResult;
import com.bdu.dishmind.dto.request.BehaviorReportRequest;
import com.bdu.dishmind.service.BehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/behavior")
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    @PostMapping("/report")
    public ApiResult<Void> report(@RequestBody BehaviorReportRequest request) {
        behaviorService.report(request);
        return ApiResult.success(null);
    }
}
