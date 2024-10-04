package vc.voyageconnect.match.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class MatchController {

    /*@PostMapping("/match/addProfile")
    public void addProfile(MatchProfile matchProfile) {

        *//**
         * POST로 받아서 매칭 프로필 저장하는 로직
         *//*
    }*/

    @GetMapping("/match/profile/{userId}")
    public void viewProfile() {


    }
}
