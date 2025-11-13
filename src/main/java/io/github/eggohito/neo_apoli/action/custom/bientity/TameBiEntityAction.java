package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;

public record TameBiEntityAction() implements BiEntityAction {

	public static final MapCodec<TameBiEntityAction> CODEC = MapCodec.unit(TameBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, TameBiEntityAction> PACKET_CODEC = PacketCodecUtil.unit(TameBiEntityAction::new);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.TAME;
	}

	@Override
	public void execute(Context context) {

		if (context.nullable(ContextParameters.ACTOR) instanceof ServerPlayerEntity serverPlayer) {

			switch (context.nullable(ContextParameters.TARGET)) {
				case TameableEntity tameable ->
					tameable.setTamedBy(serverPlayer);
				case AbstractHorseEntity abstractHorse ->
					abstractHorse.bondWithPlayer(serverPlayer);
				case null, default -> {
					//	No-op
				}
			}

		}

	}

}
