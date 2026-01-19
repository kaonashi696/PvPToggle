package me.jack.pvptoggle.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import me.jack.pvptoggle.PvPTogglePlugin;

import java.time.Instant;

public class PvPToggleComponent implements Component {
    private boolean pvpEnabled;
    private Instant lastToggleTime = Instant.EPOCH;
    private Instant lastCombatTime = Instant.EPOCH;
    private Instant pendingDisableAt = Instant.EPOCH;
    private long lastDisableAnnouncementSeconds = -1;

    public PvPToggleComponent() {
        this.pvpEnabled = PvPTogglePlugin.CONFIG.get().isDefaultPvPEnabled();
    }

    public PvPToggleComponent(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public static ComponentType getComponentType() {
        return PvPTogglePlugin.getInstance().getPvPToggleComponentType();
    }

    public static final BuilderCodec<PvPToggleComponent> CODEC = BuilderCodec
        .builder(PvPToggleComponent.class, PvPToggleComponent::new)
        .append(new KeyedCodec<>("PvPEnabled", Codec.BOOLEAN),
                (component, enabled) -> component.pvpEnabled = enabled,
                (component) -> component.pvpEnabled).add()
        .build();

    public boolean isPvPEnabled() {
        return pvpEnabled;
    }

    public void setPvPEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public Instant getLastToggleTime() {
        return lastToggleTime;
    }

    public void setLastToggleTime(Instant lastToggleTime) {
        this.lastToggleTime = lastToggleTime;
    }

    public Instant getLastCombatTime() {
        return lastCombatTime;
    }

    public void setLastCombatTime(Instant lastCombatTime) {
        this.lastCombatTime = lastCombatTime;
    }

    public boolean hasPendingDisable() {
        return !Instant.EPOCH.equals(this.pendingDisableAt);
    }

    public Instant getPendingDisableAt() {
        return pendingDisableAt;
    }

    public void setPendingDisableAt(Instant pendingDisableAt) {
        this.pendingDisableAt = pendingDisableAt;
    }

    public void clearPendingDisable() {
        this.pendingDisableAt = Instant.EPOCH;
        this.lastDisableAnnouncementSeconds = -1;
    }

    public long getRemainingPendingDisableSeconds() {
        if (!hasPendingDisable()) return 0;
        return Math.max(0, pendingDisableAt.getEpochSecond() - Instant.now().getEpochSecond());
    }

    public long getRemainingOffTimeoutSeconds() {
        if (!pvpEnabled) return 0;
        long timeout = PvPTogglePlugin.CONFIG.get().getOffTimeoutSeconds();
        if (timeout <= 0) return 0;
        Instant disableAt = this.lastToggleTime.plusSeconds(timeout);
        return Math.max(0, disableAt.getEpochSecond() - Instant.now().getEpochSecond());
    }

    public Instant getOffTimeoutEndTime() {
        if (!pvpEnabled) return Instant.EPOCH;
        long timeout = PvPTogglePlugin.CONFIG.get().getOffTimeoutSeconds();
        if (timeout <= 0) return Instant.EPOCH;
        return this.lastToggleTime.plusSeconds(timeout);
    }

    public long getLastDisableAnnouncementSeconds() {
        return lastDisableAnnouncementSeconds;
    }

    public void setLastDisableAnnouncementSeconds(long lastDisableAnnouncementSeconds) {
        this.lastDisableAnnouncementSeconds = lastDisableAnnouncementSeconds;
    }

    public boolean isInCombat() {
        long duration = PvPTogglePlugin.CONFIG.get().getCombatTimerSeconds();
        if (duration <= 0) return false;
        return Instant.now().isBefore(this.lastCombatTime.plusSeconds(duration));
    }

    public boolean isOnCooldown() {
        long duration = PvPTogglePlugin.CONFIG.get().getToggleCooldownSeconds();
        if (duration <= 0) return false;
        return Instant.now().isBefore(this.lastToggleTime.plusSeconds(duration));
    }

    public long getRemainingCombatTime() {
        if (!isInCombat()) return 0;
        long duration = PvPTogglePlugin.CONFIG.get().getCombatTimerSeconds();
        Instant combatEnds = this.lastCombatTime.plusSeconds(duration);
        return Math.max(0, combatEnds.getEpochSecond() - Instant.now().getEpochSecond());
    }

    public long getRemainingCooldown() {
        if (!isOnCooldown()) return 0;
        long cooldown = PvPTogglePlugin.CONFIG.get().getToggleCooldownSeconds();
        Instant cooldownEnds = this.lastToggleTime.plusSeconds(cooldown);
        return Math.max(0, cooldownEnds.getEpochSecond() - Instant.now().getEpochSecond());
    }

    @Override
    public Component clone() {
        PvPToggleComponent clone = new PvPToggleComponent(this.pvpEnabled);

        clone.lastCombatTime = this.lastCombatTime;
        clone.lastToggleTime = this.lastToggleTime;
        clone.pendingDisableAt = this.pendingDisableAt;
        clone.lastDisableAnnouncementSeconds = this.lastDisableAnnouncementSeconds;

        return clone;
    }
}
