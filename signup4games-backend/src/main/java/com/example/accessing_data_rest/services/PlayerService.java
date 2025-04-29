package com.example.accessing_data_rest.services;

import com.example.accessing_data_rest.controllers.CouldNotCreatePlayerException;
import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.GameState;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.GameRepository;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import com.example.accessing_data_rest.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Player createPlayer(Player player) {
        Game game = player.getGame();
        User user = player.getUser();
        if (game != null && user != null) {
            game = gameRepository.findByUid(game.getUid());
            user = userRepository.findByUid(user.getUid());
            if (game != null && user != null) {
                if (!GameState.SIGNUP.equals(game.getState())) {
                    throw new CouldNotCreatePlayerException("Game is not in SIGNUP state.");
                }
                int noPlayers = game.getPlayers().size();
                if (game.getMaxPlayers() <= noPlayers) {
                    throw new CouldNotCreatePlayerException("Game does not allow for more players.");
                }
                for (Player p : game.getPlayers()) {
                    User playerUser = p.getUser();
                    if (p == null || playerUser.getUid() == user.getUid()) {
                        throw new CouldNotCreatePlayerException("User is already part of this game.");
                    }
                }
                if (player.getName() == null) {
                    player.setName(user.getName());
                }
                Player result = playerRepository.save(player);
                return result;
            }
        }
        throw new CouldNotCreatePlayerException("Player does not refer to a game and a user.");
    }
}