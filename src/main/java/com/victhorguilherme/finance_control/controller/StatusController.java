package com.victhorguilherme.finance_control.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/status")
    public String verificarStatus(){
        return "API de controle financeiro funcionando!";
    }


}
