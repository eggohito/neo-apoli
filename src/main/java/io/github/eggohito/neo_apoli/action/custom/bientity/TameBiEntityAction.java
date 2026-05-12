package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBiEntityActionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public enum TameBiEntityAction implements BiEntityAction {

	INSTANCE;

	public static final MapCodec<TameBiEntityAction> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, TameBiEntityAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public BiEntityAction.Type<?> getType() {
		return NeoApoliBiEntityActionTypes.TAME;
	}

	@Override
	public void execute(Context context) {

		if (context.getNullable(NeoApoliContextParams.ACTOR_ENTITY) instanceof ServerPlayer serverPlayer) {

			switch (context.getNullable(NeoApoliContextParams.TARGET_ENTITY)) {
				case TamableAnimal tamableAnimal ->
					tamableAnimal.tame(serverPlayer);
				case AbstractHorse abstractHorse ->
					abstractHorse.tameWithName(serverPlayer);
				case null, default -> {
					//  No-op; either null or unsupported
				}
			}

		}

	}

}
