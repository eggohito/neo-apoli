package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.client.PowerIntegrationsClient;
import io.github.eggohito.neo_apoli.client.util.KeyBindingUtil;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingReference;
import io.github.eggohito.neo_apoli.networking.NeoApoliS2CNetworkHandler;
import io.github.eggohito.neo_apoli.networking.packet.c2s.TriggerPowerImplsC2SPacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.misc.KeyBound;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;

import java.util.List;
import java.util.Set;

public class NeoApoliClient implements ClientModInitializer {

	private static final Object2BooleanMap<String> LAST_KEYBINDING_STATES = new Object2BooleanOpenHashMap<>();

	@Override
	public void onInitializeClient() {

		NeoApoliS2CNetworkHandler.init();
		PowerIntegrationsClient.registerAll();

		ClientTickEvents.END_CLIENT_TICK.register(NeoApoliClient::triggerKeyBoundPowerImpls);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {

			((DataCommandStorageHolder) client).neo_apoli$clear();
			((PowerRecipeDisplayHolder) client).neo_apoli$setReferencesByDisplayEntry(new Object2ObjectOpenHashMap<>());

			NeoApoli.LOGS.clear();

		});

	}

	private static void triggerKeyBoundPowerImpls(MinecraftClient client) {

		if (client.player == null || client.world == null) {
			return;
		}

		ClientPlayerEntity player = client.player;
		Object2BooleanMap<String> currentKeyBindingStates = new Object2BooleanOpenHashMap<>();

		List<Power.Instance<?>> instances = PowersComponent.getAllInstances(player);
		Set<PowerReference> triggeredImpls = new ObjectOpenHashSet<>();

		for (var instance: instances) {

			if (!PowerManager.containsReference(instance.getPower()) || !(instance instanceof KeyBound.Instance keyBoundInstance)) {
				continue;
			}

			KeyBindingReference keyBindingReference = keyBoundInstance.getKey();
			TriState keyPressState = KeyBindingUtil.getKeyBinding(keyBindingReference.id())
				.map(KeyBinding::isPressed)
				.map(TriState::of)
				.orElse(TriState.DEFAULT);

			if (keyPressState != TriState.DEFAULT) {

				currentKeyBindingStates.putIfAbsent(keyBindingReference.id(), keyPressState.get());

				if (isPressed(keyBindingReference, keyPressState)) {

					PowerReference reference = PowerManager.getReference(instance.getPower());
					Context context = instance.createHolderContext();

					if (keyBoundInstance.shouldTrigger(context)) {
						keyBoundInstance.onPress(context);
					}

					triggeredImpls.add(reference);

				}

			}

		}

		LAST_KEYBINDING_STATES.clear();
		LAST_KEYBINDING_STATES.putAll(currentKeyBindingStates);

		if (!triggeredImpls.isEmpty()) {
			ClientPlayNetworking.send(new TriggerPowerImplsC2SPacket(triggeredImpls));
		}

	}

	private static boolean isPressed(KeyBindingReference keyBindingReference, TriState keyPressState) {
		return keyPressState.get()
			&& (keyBindingReference.continuous() || !LAST_KEYBINDING_STATES.getOrDefault(keyBindingReference.id(), false));
	}

}
