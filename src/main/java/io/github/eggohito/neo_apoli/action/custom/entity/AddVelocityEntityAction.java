package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.util.Space;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public record AddVelocityEntityAction(Vec3Provider velocity, Space space) implements EntityAction {

	public static final MapCodec<AddVelocityEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3Provider.CODEC.fieldOf("velocity").forGetter(AddVelocityEntityAction::velocity),
		Space.CODEC.optionalFieldOf("space", Space.WORLD).forGetter(AddVelocityEntityAction::space)
	).apply(instance, AddVelocityEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AddVelocityEntityAction> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, AddVelocityEntityAction::velocity,
		Space.STREAM_CODEC, AddVelocityEntityAction::space,
		AddVelocityEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.ADD_VELOCITY;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasParameter(NeoApoliContextKeys.THIS_ENTITY)) {
			return;
		}

		Context velocityContext = context.forChild(".velocity");
		Vector3f velocity = velocity().next(velocityContext).toVector3f();

		if (velocityContext.hasErrors()) {
			return;
		}

		Entity entity = context.required(NeoApoliContextKeys.THIS_ENTITY);
		space().globalize(velocity, entity);

		entity.push(velocity.x(), velocity.y(), velocity.z());
		entity.hurtMarked = true;

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityAction.super.validate(validator);
		velocity().validate(validator.forChild(".velocity"));
	}

}
