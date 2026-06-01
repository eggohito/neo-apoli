package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliVec3ProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ConditionalVec3Provider(Condition condition, Vec3Provider ifValue, Vec3Provider elseValue) implements Vec3Provider, ConditionalValueProvider<Vec3Provider> {

	public static final MapCodec<ConditionalVec3Provider> CODEC = MapCodecUtil.lazy(ConditionalVec3Provider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(Vec3Provider.CODEC, ConditionalVec3Provider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalVec3Provider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalVec3Provider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(Vec3Provider.STREAM_CODEC, ConditionalVec3Provider::new));

	@Override
	public @NotNull Vec3Provider.Type<?> getType() {
		return NeoApoliVec3ProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull Vec3 getVec3(Context context) {
		return this.getOrElse(context, Vec3Provider::getVec3, () -> Vec3.ZERO);
	}

}
