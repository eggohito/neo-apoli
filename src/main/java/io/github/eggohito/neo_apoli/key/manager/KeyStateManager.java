package io.github.eggohito.neo_apoli.key.manager;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.key.KeyState;
import io.github.eggohito.neo_apoli.util.services.Services;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.UUID;

@ApiStatus.NonExtendable
public interface KeyStateManager {

	ResourceLocation ID = NeoApoli.id("manager/key_state");

	KeyStateManager INSTANCE = Services.load(KeyStateManager.class);

	Optional<KeyState> getState(UUID uuid, String key);

}
