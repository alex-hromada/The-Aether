package com.gildedgames.aether.item.combat.abilities.armor;

import com.gildedgames.aether.capability.player.AetherPlayer;
import com.gildedgames.aether.util.EquipmentUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface ValkyrieArmor {
    static void handleFlight(LivingEntity entity) {
        if (EquipmentUtil.hasFullValkyrieSet(entity)) {
            if (entity instanceof Player player && !player.getAbilities().flying) {
                AetherPlayer.get(player).ifPresent(aetherPlayer -> {
                    Vec3 deltaMovement = player.getDeltaMovement();
                    if (!player.level.isClientSide()) {
                        if (aetherPlayer.isJumping() && !player.isOnGround()) {
                            if (aetherPlayer.getFlightModifier() >= aetherPlayer.getFlightModifierMax()) {
                                aetherPlayer.setFlightModifier(aetherPlayer.getFlightModifierMax());
                            }
                            if (aetherPlayer.getFlightTimer() > 2) {
                                if (aetherPlayer.getFlightTimer() < aetherPlayer.getFlightTimerMax()) {
                                    aetherPlayer.setFlightModifier(aetherPlayer.getFlightModifier() + 0.25F);
                                    aetherPlayer.setFlightTimer(aetherPlayer.getFlightTimer() + 1);
                                }
                            } else {
                                aetherPlayer.setFlightTimer(aetherPlayer.getFlightTimer() + 1);
                            }
                        } else {
                            aetherPlayer.setFlightModifier(1.0F);
                        }
                        if (player.isOnGround()) {
                            aetherPlayer.setFlightTimer(0);
                            aetherPlayer.setFlightModifier(1.0F);
                        }
                    }
                    if (aetherPlayer.isJumping() && !player.isOnGround() && aetherPlayer.getFlightTimer() > 2 && aetherPlayer.getFlightTimer() < aetherPlayer.getFlightTimerMax() && aetherPlayer.getFlightModifier() > 1.0F) {
                        player.setDeltaMovement(deltaMovement.x(), 0.025F * aetherPlayer.getFlightModifier(), deltaMovement.z());
                    }
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.connection.aboveGroundTickCount = 0;
                    }
                });
            }
        }
    }
}
