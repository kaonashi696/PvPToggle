package me.jack.pvptoggle.util;

import com.hypixel.hytale.server.core.Message;

public final class PvPToggleMessageUtil {
    private PvPToggleMessageUtil() {
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
