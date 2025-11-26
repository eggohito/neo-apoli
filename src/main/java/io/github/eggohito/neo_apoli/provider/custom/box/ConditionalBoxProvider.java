package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ConditionalBoxProvider(Condition condition, BoxProvider ifValue, BoxProvider elseValue) implements BoxProvider, ConditionalValueProvider<BoxProvider, AABB> {

	public static final MapCodec<ConditionalBoxProvider> CODEC = MapCodecUtil.lazy(ConditionalBoxProvider.class.getSimpleName(), () -> ConditionalValueProvider.createCodec(BoxProvider.CODEC, ConditionalBoxProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalBoxProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalBoxProvider.class.getSimpleName(), () -> ConditionalValueProvider.createStreamCodec(BoxProvider.STREAM_CODEC, ConditionalBoxProvider::new));

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull AABB next(Context context) {
		return internalNextOrElse(context, () -> new AABB(Vec3.ZERO, Vec3.ZERO));
	}

}
