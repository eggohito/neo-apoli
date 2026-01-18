package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.duck.MovingEntity;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record VelocityVec3Provider(TypedContextKey<Entity> entity) implements Vec3Provider {

    public static final MapCodec<VelocityVec3Provider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(VelocityVec3Provider::entity))
        .apply(instance, VelocityVec3Provider::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VelocityVec3Provider> STREAM_CODEC = StreamCodec.composite(
        NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, VelocityVec3Provider::entity,
        VelocityVec3Provider::new
    );

    @Override
    public Vec3ProviderType<?> getType() {
        return Vec3ProviderTypes.VELOCITY;
    }

    @Override
    public @NotNull Vec3 next(Context context) {

        switch (context.nullable(entity())) {
            case MovingEntity movingEntity -> {
                return movingEntity.neo_apoli$getVelocity();
            }
            case null ->
                context.report("Couldn't get velocity of entity from parameter \"" + entity().name() + "\", as it doesn't exist!");
            default ->
                context.report("Couldn't get velocity of entity from parameter \"" + entity().name() + "\", as it's not considered a moving entity!");
        }

        return Vec3.ZERO;

    }

    @Override
    public Set<ContextKey<?>> getRequiredParameters() {
        return Set.of(entity());
    }

}
