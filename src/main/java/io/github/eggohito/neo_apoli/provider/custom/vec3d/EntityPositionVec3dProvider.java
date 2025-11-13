package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record EntityPositionVec3dProvider(EntityTarget entity) implements Vec3dProvider {

	public static final MapCodec<EntityPositionVec3dProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.fieldOf("entity").forGetter(EntityPositionVec3dProvider::entity)
	).apply(instance, EntityPositionVec3dProvider::new));

	public static final PacketCodec<RegistryByteBuf, EntityPositionVec3dProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityTarget.PACKET_CODEC, EntityPositionVec3dProvider::entity,
		EntityPositionVec3dProvider::new
	);

	@Override
	public Vec3dProviderType<?> getType() {
		return Vec3dProviderTypes.ENTITY_POSITION;
	}

	@Override
	public @NotNull Vec3d next(Context context) {
		return context.optional(entity().getParameter())
			.map(Entity::getPos)
			.orElse(Vec3d.ZERO);
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

}
