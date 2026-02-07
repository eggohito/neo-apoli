package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfKeyCondition(List<KeyCondition> conditions) implements KeyCondition, IAllOfMetaCondition<KeyCondition> {

	public static final MapCodec<AllOfKeyCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfKeyCondition.class.getSimpleName(), () -> IAllOfMetaCondition.mapCodec(KeyCondition.CODEC, AllOfKeyCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfKeyCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfKeyCondition.class.getSimpleName(), () -> IAllOfMetaCondition.streamCodec(KeyCondition.STREAM_CODEC, AllOfKeyCondition::new));

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.ALL_OF;
	}

}
