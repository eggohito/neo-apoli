package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliEntityActionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public record SwingHandEntityAction(Optional<InteractionHand> hand) implements EntityAction {

	public static final MapCodec<SwingHandEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliCodecs.HAND.optionalFieldOf("hand").forGetter(SwingHandEntityAction::hand))
		.apply(instance, SwingHandEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SwingHandEntityAction> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(NeoApoliStreamCodecs.HAND), SwingHandEntityAction::hand,
		SwingHandEntityAction::new
	);

	@Override
	public EntityAction.Type<?> getType() {
		return NeoApoliEntityActionTypes.SWING_HAND;
	}

	@Override
	public void execute(Context context) {
		context.getOptional(NeoApoliContextParams.THIS_ENTITY)
			.filter(LivingEntity.class::isInstance)
			.map(LivingEntity.class::cast)
			.ifPresent(this::swingHand);
	}

	private InteractionHand getSpecifiedOrUsedHand(LivingEntity user) {
		return hand().orElseGet(user::getUsedItemHand);
	}

	private void swingHand(LivingEntity entity) {
		entity.swing(this.getSpecifiedOrUsedHand(entity), entity instanceof ServerPlayer);
	}

}
