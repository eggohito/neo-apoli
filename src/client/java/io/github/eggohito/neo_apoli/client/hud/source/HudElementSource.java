package io.github.eggohito.neo_apoli.client.hud.source;

import io.github.eggohito.neo_apoli.hud.element.HudElement;
import net.minecraft.world.entity.player.Player;

import java.util.List;

@FunctionalInterface
public interface HudElementSource {
	List<HudElement.WithContext> get(Player viewer, HudElement.RenderPhase renderPhase);
}
