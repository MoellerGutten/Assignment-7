package com.example.accessing_data_rest.services;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.GameState;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.GameRepository;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public List<Game> getOpenGames() {
        List<Game> result = new ArrayList<>();
        gameRepository.findByStateIs(GameState.SIGNUP).forEach(result::add);
        return result;
    }

    public List<Game> getGames() {
        List<Game> result = new ArrayList<>();
        gameRepository.findAll().forEach(result::add);
        return result;
    }

    @Transactional
    public Game createGame(Game game) {
        gameRepository.save(game);
        User owner = game.getOwner();
        System.out.println(owner);
        if (owner != null) {
            Player player = new Player();
            player.setGame(game);
            player.setUser(owner);
            player.setName(owner.getName());
            playerRepository.save(player);
        }
        Game result = gameRepository.findByUid(game.getUid());
        return result;

    }

}
