package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record VectorLengthNumberProvider(Vec3Provider vector) implements NumberProvider {

    public static final MapCodec<VectorLengthNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Vec3Provider.CODEC.fieldOf("vector").forGetter(VectorLengthNumberProvider::vector)
    ).apply(instance, VectorLengthNumberProvider::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VectorLengthNumberProvider> STREAM_CODEC = StreamCodec.composite(
        Vec3Provider.STREAM_CODEC, VectorLengthNumberProvider::vector,
        VectorLengthNumberProvider::new
    );

    @Override
    public @NotNull NumberProviderType<?> getType() {
        return NumberProviderTypes.VECTOR_LENGTH;
    }

    @Override
    public @NotNull Number nextNumber(Context context) {

        Context vectorContext = context.forChild(".vector");
        Vec3 vector = vector().nextVec3(vectorContext);

        if (vectorContext.hasErrors()) {
            return 0.0d;
        }

        else {
            return vector.length();
        }

    }

    @Override
    public void validate(Context.Validator validator) {
        NumberProvider.super.validate(validator);
        vector().validate(validator.forChild(".vector"));
    }

}
