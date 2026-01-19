package me.jack.pvptoggle.util;

import com.hypixel.hytale.server.core.Message;

import java.awt.Color;

public final class PvPToggleMessageUtil {
    private PvPToggleMessageUtil() {
    }

    public static Message buildPublicPvpOnMessage(String playerName) {
        return Message.translation("pvptoggle.public_on")
                .param("player", playerName)
                .color(Color.RED);
    }

    public static Message buildDisableCountdownMessage(long remainingSeconds) {
        if (remainingSeconds <= 0) {
            return Message.translation("pvptoggle.off");
        }
        if (remainingSeconds < 60) {
            return Message.translation("pvptoggle.off_countdown_seconds").param("seconds", remainingSeconds);
        }
        long minutes = remainingSeconds / 60;
        return Message.translation("pvptoggle.off_countdown_minutes").param("minutes", minutes);
    }
}
