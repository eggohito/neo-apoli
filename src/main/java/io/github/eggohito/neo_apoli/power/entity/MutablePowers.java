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
public interface MutablePowers extends Powers {

	boolean grant(PowerIdentifier id, ResourceLocation source, boolean invokeCallbacks);

	default boolean grantWithCallback(PowerIdentifier id, ResourceLocation source) {
		return this.grant(id, source, true);
	}

	default boolean grantWithoutCallback(PowerIdentifier id, ResourceLocation source) {
		return this.grant(id, source, false);
	}


	boolean revoke(PowerIdentifier id, ResourceLocation source, boolean invokeCallbacks);

	default boolean revokeWithCallback(PowerIdentifier id, ResourceLocation source) {
		return this.revoke(id, source, true);
	}

	default boolean revokeWithoutCallback(PowerIdentifier id, ResourceLocation source) {
		return this.revoke(id, source, false);
	}


	void applyChanges();


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
