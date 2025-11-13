package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfKeyCondition(List<KeyCondition> conditions) implements KeyCondition, AllOfMetaCondition<KeyCondition> {

	public static final MapCodec<AllOfKeyCondition> CODEC = MapCodecUtil.lazy(AllOfKeyCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(KeyCondition.CODEC, AllOfKeyCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfKeyCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfKeyCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(KeyCondition.PACKET_CODEC, AllOfKeyCondition::new));

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.ALL_OF;
	}

	@Override
	public String asDisplayString() {
		return KeyCondition.super.asDisplayString();
	}

}
