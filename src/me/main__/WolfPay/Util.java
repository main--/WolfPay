package me.main__.WolfPay;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class Util {
    public static final Logger logger = Logger.getLogger("Minecraft");

    public static List<Wolf> getWolves(Player player) {
        List<Wolf> wolves = new ArrayList<Wolf>();

        for (LivingEntity entity : player.getWorld().getLivingEntities()) {
            if ((entity instanceof Wolf) && (((Wolf) entity).getOwner() instanceof Player)) {
                Wolf wolf = (Wolf) entity;
                if (wolf.isTamed()) {
                    if (((Player) wolf.getOwner()).getName() == player.getName())
                        wolves.add(wolf);
                }
            }
        }

        return wolves;
    }

    public static void log(String message) {
        log(message, Level.INFO);
    }

    public static void log(String message, Level loglevel) {
        logger.log(loglevel, "[WolfPay] message".replaceAll("message", message));
    }
}
