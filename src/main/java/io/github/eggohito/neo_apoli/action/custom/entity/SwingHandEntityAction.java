package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

import java.util.Optional;

public record SwingHandEntityAction(Optional<Hand> hand) implements EntityAction {

	public static final MapCodec<SwingHandEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliCodecs.HAND.optionalFieldOf("hand").forGetter(SwingHandEntityAction::hand))
		.apply(instance, SwingHandEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, SwingHandEntityAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(NeoApoliPacketCodecs.HAND), SwingHandEntityAction::hand,
		SwingHandEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SWING_HAND;
	}

	@Override
	public void execute(Context context) {
		context.optional(ContextParameters.THIS_ENTITY)
			.filter(LivingEntity.class::isInstance)
			.map(LivingEntity.class::cast)
			.ifPresent(livingEntity -> livingEntity.swingHand(hand().orElseGet(livingEntity::getActiveHand), livingEntity instanceof ServerPlayerEntity));
	}

}
