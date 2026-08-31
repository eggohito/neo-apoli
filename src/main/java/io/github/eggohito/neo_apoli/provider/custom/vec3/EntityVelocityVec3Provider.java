package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.duck.internal.MovingEntity;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliVec3ProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record EntityVelocityVec3Provider(EntityProvider entity) implements Vec3Provider {

    public static final MapCodec<EntityVelocityVec3Provider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(EntityProvider.CODEC.fieldOf("entity").forGetter(EntityVelocityVec3Provider::entity))
        .apply(instance, EntityVelocityVec3Provider::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityVelocityVec3Provider> STREAM_CODEC = StreamCodec.composite(
        EntityProvider.STREAM_CODEC, EntityVelocityVec3Provider::entity,
        EntityVelocityVec3Provider::new
    );

    @Override
    public @NotNull Vec3Provider.Type<?> getType() {
        return NeoApoliVec3ProviderTypes.ENTITY_VELOCITY;
    }

    @Override
    public Optional<Vec3> getVec3(Context context) {

        Context entityContext = context.forChild(".entity");
        Entity entity = entity().getEntity(entityContext).orElse(null);

        switch (entity) {
            case MovingEntity movingEntity -> {
                return Optional.of(movingEntity.neo_apoli$getVelocity());
            }
            case null ->
                entityContext.reportProblem("Entity doesn't exist!");
            default ->
                entityContext.reportProblem("Entity is not considered a moving entity!");
        }

        return Optional.empty();

    }

    @Override
    public void validate(Context.Validator validator) {
        Vec3Provider.super.validate(validator);
        entity().validate(validator.forChild(".entity"));
    }

}
