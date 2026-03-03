package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.network.packet.s2c.MountEntityS2CPacket;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record MountBiEntityAction(BooleanProvider force) implements BiEntityAction {

	public static final MapCodec<MountBiEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BooleanProvider.CODEC.optionalFieldOf("force", new ConstantBooleanProvider(false)).forGetter(MountBiEntityAction::force)
	).apply(instance, MountBiEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, MountBiEntityAction> STREAM_CODEC = StreamCodec.composite(
		BooleanProvider.STREAM_CODEC, MountBiEntityAction::force,
		MountBiEntityAction::new
	);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.MOUNT;
	}

	@Override
	public void execute(Context context) {

		if (context.level().isClientSide() || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Entity actor = context.getRequired(NeoApoliContextParams.ACTOR_ENTITY);
		Entity target = context.getRequired(NeoApoliContextParams.TARGET_ENTITY);

		boolean force = force().nextBoolean(context.forChild(".force"));
		boolean successfulRide = actor.startRiding(target, force);

		if (successfulRide) {
			MiscUtil.sendToTracking(target, new MountEntityS2CPacket(actor, target, force));
		}

	}

}
