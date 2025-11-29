package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public record TameBiEntityAction() implements BiEntityAction {

	public static final MapCodec<TameBiEntityAction> CODEC = MapCodec.unit(TameBiEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TameBiEntityAction> STREAM_CODEC = StreamCodecUtil.unit(TameBiEntityAction::new);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.TAME;
	}

	@Override
	public void execute(Context context) {

		if (context.nullable(NeoApoliContextKeys.ACTOR_ENTITY) instanceof ServerPlayer serverPlayer) {

			switch (context.nullable(NeoApoliContextKeys.TARGET_ENTITY)) {
				case TamableAnimal tameable ->
					tameable.tame(serverPlayer);
				case AbstractHorse abstractHorse ->
					abstractHorse.tameWithName(serverPlayer);
				case null, default -> {
					//	No-op
				}
			}

		}

	}

}
