package me.jack.pvptoggle.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.jack.pvptoggle.components.PvPToggleComponent;
import me.jack.pvptoggle.util.PvPToggleMessageUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.time.Instant;

public class PvPOffTimeoutSystem extends EntityTickingSystem<EntityStore> {
    @Override
    public void tick(
            float dt,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        PvPToggleComponent pvp = (PvPToggleComponent) commandBuffer.getComponent(ref, PvPToggleComponent.getComponentType());

        if (pvp == null) {
            return;
        }

        if (!pvp.isPvPEnabled()) {
            if (pvp.hasPendingDisable()) {
                pvp.clearPendingDisable();
            }
            return;
        }

        if (!pvp.hasPendingDisable()) {
            return;
        }

        long remainingSeconds = pvp.getRemainingPendingDisableSeconds();

        if (remainingSeconds <= 0) {
            if (pvp.isInCombat()) {
                return;
            }

            pvp.setPvPEnabled(false);
            pvp.setLastToggleTime(Instant.now());
            pvp.clearPendingDisable();

            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            if (playerRef != null) {
                playerRef.sendMessage(Message.translation("pvptoggle.off"));
            }
            return;
        }

        if (remainingSeconds == pvp.getLastDisableAnnouncementSeconds()) {
            return;
        }

        if (remainingSeconds <= 10 || (remainingSeconds >= 60 && remainingSeconds % 60 == 0)) {
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            if (playerRef != null) {
                playerRef.sendMessage(PvPToggleMessageUtil.buildDisableCountdownMessage(remainingSeconds));
            }
            pvp.setLastDisableAnnouncementSeconds(remainingSeconds);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
