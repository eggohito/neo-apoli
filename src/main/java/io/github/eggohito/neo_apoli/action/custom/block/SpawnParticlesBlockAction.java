package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SpawnParticlesMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.BlockPositionVec3Provider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SpawnParticlesBlockAction(ParticleOptions particle, BiEntityCondition biEntityCondition, Vec3Provider position, Vec3Provider spread, NumberProvider speed, NumberProvider count, BooleanProvider force) implements BlockAction, SpawnParticlesMetaAction {

	public static final MapCodec<SpawnParticlesBlockAction> MAP_CODEC = SpawnParticlesMetaAction.createDefaultedCodec(() -> BlockPositionVec3Provider.INSTANCE, SpawnParticlesBlockAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnParticlesBlockAction> STREAM_CODEC = SpawnParticlesMetaAction.streamCodec(SpawnParticlesBlockAction::new);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.SPAWN_PARTICLES;
	}

}
