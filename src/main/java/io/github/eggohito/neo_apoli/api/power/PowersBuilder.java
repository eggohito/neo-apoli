package io.github.eggohito.neo_apoli.api.power;

import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import net.minecraft.resources.ResourceLocation;

public interface PowersBuilder extends Powers {

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


	void build();

}
