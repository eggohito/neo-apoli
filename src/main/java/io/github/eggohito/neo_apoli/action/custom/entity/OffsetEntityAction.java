package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public record OffsetEntityAction(EntityAction action, Vec3Provider offset) implements EntityAction {

	public static final MapCodec<OffsetEntityAction> CODEC = MapCodecUtil.lazy(OffsetEntityAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityAction.CODEC.fieldOf("action").forGetter(OffsetEntityAction::action),
		Vec3Provider.CODEC.fieldOf("offset").forGetter(OffsetEntityAction::offset)
	).apply(instance, OffsetEntityAction::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, OffsetEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(OffsetEntityAction.class.getSimpleName(), () -> StreamCodec.composite(
		EntityAction.STREAM_CODEC, OffsetEntityAction::action,
		Vec3Provider.STREAM_CODEC, OffsetEntityAction::offset,
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

		Context offsetContext = context.forChild(".offset");
		Vec3 offset = offset().next(offsetContext);

		if (offsetContext.hasErrors()) {
			return;
		}

		Vec3 offsetPos = context.required(NeoApoliContextKeys.THIS_POS).add(offset);
		Context actionContext = new Context.Builder(context)
			.add(NeoApoliContextKeys.THIS_POS, offsetPos)
			.build(context.getLevel());

		action().execute(actionContext.forChild(".action"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.THIS_POS);
	}

	@Override
	public void validate(Context.Validator validator) {

		EntityAction.super.validate(validator);

		action().validate(validator.forChild(".action"));
		offset().validate(validator.forChild(".offset"));

	}

}
