package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.impl.misc.MovingEntity;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record VelocityVec3Provider(Context.Parameter<Entity> entity) implements Vec3Provider {

    public static final MapCodec<VelocityVec3Provider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(VelocityVec3Provider::entity))
        .apply(instance, VelocityVec3Provider::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VelocityVec3Provider> STREAM_CODEC = StreamCodec.composite(
        NeoApoliContextParams.StreamCodecs.ENTITY, VelocityVec3Provider::entity,
        VelocityVec3Provider::new
    );

    @Override
    public @NotNull Vec3ProviderType<?> getType() {
        return Vec3ProviderTypes.VELOCITY;
    }

    @Override
    public @NotNull Vec3 nextVec3(Context context) {

        switch (context.getNullable(entity())) {
            case MovingEntity movingEntity -> {
                return movingEntity.neo_apoli$getVelocity();
            }
            case null ->
                context.forChild(".entity").reportProblem("Entity doesn't exist!");
            default ->
                context.forChild(".entity").reportProblem("Entity is not considered a moving entity!");
        }

        return Vec3.ZERO;

    }

    @Override
    public Set<ContextKey<?>> getRequiredParameters() {
        return Set.of(entity());
    }

}
