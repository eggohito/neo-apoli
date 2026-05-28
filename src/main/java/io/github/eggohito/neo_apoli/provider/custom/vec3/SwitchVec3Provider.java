package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliVec3ProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SwitchVec3Provider(List<Case<Condition, Vec3Provider>> cases, Vec3Provider defaultValue) implements Vec3Provider, SwitchValueProvider<Vec3Provider> {

	public static final MapCodec<SwitchVec3Provider> CODEC = MapCodecUtil.lazy(SwitchVec3Provider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(Vec3Provider.CODEC, SwitchVec3Provider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchVec3Provider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchVec3Provider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(Vec3Provider.STREAM_CODEC, SwitchVec3Provider::new));

	@Override
	public @NotNull Vec3Provider.Type<?> getType() {
		return NeoApoliVec3ProviderTypes.SWITCH;
	}

	@Override
	public @NotNull Vec3 getVec3(Context context) {
		return this.nextOrDefault(context, Vec3Provider::getVec3);
	}

}
