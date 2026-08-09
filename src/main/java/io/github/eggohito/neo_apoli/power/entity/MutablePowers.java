package io.github.eggohito.neo_apoli.power.entity;

import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.entity.impl.MutablePowersImpl;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public interface MutablePowers extends Powers, AutoCloseable {

	boolean grant(PowerIdentifier id, ResourceLocation source);

	boolean revoke(PowerIdentifier id, ResourceLocation source);


	@Override
	void close();


	static MutablePowers create(@NotNull Entity holder) {
		return MutablePowersImpl.of(holder);
	}

	@Nullable
	static MutablePowers getNullable(Entity holder) {
		return holder != null && holder.hasAttached(NeoApoliEntityAttachments.POWERS)
			? create(holder)
			: null;
	}

	static Optional<MutablePowers> getOptional(Entity holder) {
		return Optional.ofNullable(getNullable(holder));
	}

}
