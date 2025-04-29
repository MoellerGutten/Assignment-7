package com.example.accessing_data_rest.controllers;

import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public Player postPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }
}
