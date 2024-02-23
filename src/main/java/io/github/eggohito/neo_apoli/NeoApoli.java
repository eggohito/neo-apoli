package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NeoApoli implements ModInitializer {

	public static final String MOD_NAMESPACE = "neo-apoli";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	@Override
	public void onInitialize() {

		PowerTypes.register();

		PowerTypes.ALIASES.addNamespaceAlias("minecraft", MOD_NAMESPACE);
		PowerTypes.ALIASES.addPathAlias("shrimple", "simple");

		DynamicRegistries.registerSynced(NeoApoliRegistryKeys.POWERS, Power.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);

		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {

			Registry<Power> powerRegistry = registries.get(NeoApoliRegistryKeys.POWERS);
			if (powerRegistry == null) {
				return;
			}

			List<TagKey<Power>> powerTags = powerRegistry.streamTags().toList();
			LOGGER.info("Loaded " + powerTags.size() + " power tags");

		});

	}

}