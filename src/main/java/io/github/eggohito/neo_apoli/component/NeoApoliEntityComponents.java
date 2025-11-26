package io.github.eggohito.neo_apoli.component;

import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import net.minecraft.world.entity.Entity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class NeoApoliEntityComponents implements EntityComponentInitializer {

	public static final ComponentKey<PowersComponent> POWERS = ComponentRegistry.getOrCreate(PowersComponent.getId(), PowersComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(Entity.class, POWERS)
			.impl(PowersComponent.class)
			.respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY)
			.end(PowersComponent::new);
	}

}
