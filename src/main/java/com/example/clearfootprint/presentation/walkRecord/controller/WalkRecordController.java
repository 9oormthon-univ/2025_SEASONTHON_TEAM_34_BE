package com.example.clearfootprint.presentation.walkRecord.controller;

import com.example.clearfootprint.application.walkRecord.service.WalkRecordService;
import com.example.clearfootprint.presentation.walkRecord.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalkRecordController {

    public WalkRecordController(WalkRecordService walkRecordService) {
        this.walkRecordService = walkRecordService;
    }

    private final WalkRecordService walkRecordService;

    @PostMapping("/api/walk")
    public GetTargetDisAndWalkDisResponse getTargetDisAndWalkDis(@RequestBody GetTargetDisAndWalkDisRequest request){
        System.out.println("controller 동작함");

        return walkRecordService.getTargetDisAndWalkDis(request.getId(), request.getNum());
    }

    @PostMapping("/api/walk/update")
    public ResponseEntity<Void> updateWalk(@RequestBody UpdateWalkRequest request){
        System.out.println("controller 동작함");

        walkRecordService.updateWalk(request.getUserId(), request.getTargetDistanceM(), request.getWalkedDistanceM());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/rank")
    public GetRankResponse weeklyLeaderboard(@RequestBody GetRankRequest request) {
        System.out.println("api3 동작함");
        return walkRecordService.getWeeklyLeaderboard(request.getUserId());
    }
}

