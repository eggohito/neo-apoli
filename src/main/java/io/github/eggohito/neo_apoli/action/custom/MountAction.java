package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundMountEntityPacket;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record MountAction(BooleanProvider force, EntityProvider vehicle, EntityProvider passenger) implements Action {

	public static final MapCodec<MountAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BooleanProvider.CODEC.optionalFieldOf("force", new ConstantBooleanProvider(false)).forGetter(MountAction::force),
		EntityProvider.CODEC.fieldOf("vehicle").forGetter(MountAction::vehicle),
		EntityProvider.CODEC.fieldOf("passenger").forGetter(MountAction::passenger)
	).apply(instance, MountAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, MountAction> STREAM_CODEC = StreamCodec.composite(
		BooleanProvider.STREAM_CODEC, MountAction::force,
		EntityProvider.STREAM_CODEC, MountAction::vehicle,
		EntityProvider.STREAM_CODEC, MountAction::passenger,
		MountAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.MOUNT;
	}

	@Override
	public void execute(Context context) {

		if (context.level().isClientSide()) {
			return;
		}

		Entity vehicle = vehicle().getEntity(context.forChild(".vehicle")).orElse(null);
		Entity passenger = passenger().getEntity(context.forChild(".passenger")).orElse(null);

		if (vehicle == null || passenger == null) {
			return;
		}

		boolean force = force().getBoolean(context.forChild(".force"));
		boolean startedRiding = passenger.startRiding(vehicle, force);

		if (startedRiding) {
			MiscUtil.sendToTracking(vehicle, new ClientboundMountEntityPacket(passenger, vehicle, force));
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		vehicle().validate(validator.forChild(".vehicle"));
		passenger().validate(validator.forChild(".passenger"));
	}

}
