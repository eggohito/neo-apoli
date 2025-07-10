package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;

@EqualsAndHashCode
@Data
public final class TameBiEntityAction extends BiEntityAction {

	public static final MapCodec<TameBiEntityAction> CODEC = MapCodec.unit(TameBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, TameBiEntityAction> PACKET_CODEC = PacketCodec.unit(new TameBiEntityAction());

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.TAME;
	}

	@Override
	protected void impl(Context context) {

		if (context.required(ContextParameters.ACTOR) instanceof ServerPlayerEntity serverPlayer) {

			switch (context.required(ContextParameters.TARGET)) {
				case TameableEntity tameableEntity ->
					tameableEntity.setTamedBy(serverPlayer);
				case AbstractHorseEntity abstractHorseEntity ->
					abstractHorseEntity.bondWithPlayer(serverPlayer);
				default -> {

				}
			}

		}

	}

}
