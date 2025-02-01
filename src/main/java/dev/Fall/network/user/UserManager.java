package dev.Fall.network.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author IDeal_Studio
 * @since 8/5/2024
 */
@Getter
@Setter
public class UserManager {
    private User user = new User("Unknown", null, null, Date.from(LocalDate.of(1980, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()), "");
    private Map<String, User> inGameUsers = new HashMap<>();
    private Map<String, User> users = new HashMap<>();

    private String token;

    public void addUser(String username, User user) {
        users.put(username, user);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void setUserAvatar(String username, String avatarData) {
        User user = getUser(username);
        if (user != null) {
            user.setAvatarData(avatarData);
        } else {
            user = new User(username, null, avatarData);
            addUser(username, user);
        }
    }

    public void updateUserRank(String username, String rank) {
        User user = getUser(username);
        if (user != null) {
            user.setRank(rank);
        }
    }

    public void addInGameUser(String inGameName, User user) {
        inGameUsers.put(inGameName, user);
    }

    public User getInGameUser(String inGameName) {
        return inGameUsers.get(inGameName);
    }

    public void update(String rank) {
        user = new User(user.getUsername(), null, rank, null, "");
    }
}
