package com.example.accessing_data_rest.controllers;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    @Autowired
    private GameService gameService;

    @GetMapping(value = "", produces = "application/json")
    public List<Game> getGames() {
        return gameService.getGames();
    }

    @GetMapping(value = "/opengames", produces = "application/json")
    public List<Game> getOpenGames() {
        return gameService.getOpenGames();
    }

    @PostMapping(value = "",
            consumes = "application/json",
            produces = "application/json")
    public Game postGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }

    @PutMapping(value = "/updategame", consumes = "application/json")
    public void updateGame(@RequestBody Game game) {
        gameService.updateGame(game);
    }

    @DeleteMapping(value = "/{uid}")
    public void deleteGame(@PathVariable("uid") long uid) {
        gameService.deleteGame(uid);
    }
}
