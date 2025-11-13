package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public record OffsetEntityAction(EntityAction action, Vec3dProvider offset) implements EntityAction {

	public static final MapCodec<OffsetEntityAction> CODEC = MapCodecUtil.lazy(OffsetEntityAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityAction.CODEC.fieldOf("action").forGetter(OffsetEntityAction::action),
		Vec3dProvider.CODEC.fieldOf("offset").forGetter(OffsetEntityAction::offset)
	).apply(instance, OffsetEntityAction::new)));

	public static final PacketCodec<RegistryByteBuf, OffsetEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(OffsetEntityAction.class.getSimpleName(), () -> PacketCodec.tuple(
		EntityAction.PACKET_CODEC, OffsetEntityAction::action,
		Vec3dProvider.PACKET_CODEC, OffsetEntityAction::offset,
		OffsetEntityAction::new
	));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.OFFSET;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Context offsetContext = context.makeChild(".offset");
		Vec3d offset = offset().next(offsetContext);

		if (offsetContext.hasErrors()) {
			return;
		}

		Vec3d offsetPos = context.required(ContextParameters.ENTITY_POS).add(offset);
		Context actionContext = ContextImpl.of(context, builder -> builder.add(ContextParameters.ENTITY_POS, offsetPos));

		action().execute(actionContext.makeChild(".action"));

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.ENTITY_POS);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		EntityAction.super.validate(reporter);

		action().validate(reporter.makeChild(".action"));
		offset().validate(reporter.makeChild(".offset"));

	}

}
