package io.github.eggohito.neo_apoli.action.custom.entity;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SpawnParticlesMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.EntityPositionVec3Provider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public record SpawnParticlesEntityAction(ParticleOptions particle, BiEntityCondition biEntityCondition, Vec3Provider position, Vec3Provider spread, NumberProvider speed, NumberProvider count, BooleanProvider force) implements EntityAction, SpawnParticlesMetaAction {

	private static final Supplier<Vec3Provider> DEFAULT_POSITION = Suppliers.memoize(() -> new EntityPositionVec3Provider(NeoApoliContextKeys.THIS_ENTITY, EntityAnchorArgument.Anchor.FEET));

	public static final MapCodec<SpawnParticlesEntityAction> CODEC = SpawnParticlesMetaAction.createDefaultedCodec(DEFAULT_POSITION, SpawnParticlesEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnParticlesEntityAction> STREAM_CODEC = SpawnParticlesMetaAction.createStreamCodec(SpawnParticlesEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SPAWN_PARTICLES;
	}

	@Override
	public void sendParticlesTo(Context context, ServerLevel serverLevel, ServerPlayer viewer, boolean force, Vec3 pos, int count, Vec3 spread, float speed) {

		Vec3 actualSpread = context.optional(NeoApoliContextKeys.THIS_ENTITY)
			.map(entity -> spread.multiply(entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth()))
			.orElse(spread);

		SpawnParticlesMetaAction.super.sendParticlesTo(context, serverLevel, viewer, force, pos, count, actualSpread, speed);

	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}
