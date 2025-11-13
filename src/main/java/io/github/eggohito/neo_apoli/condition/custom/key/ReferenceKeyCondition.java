package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceKeyCondition(Identifier value) implements KeyCondition, ReferenceMetaCondition<KeyCondition> {

	public static final MapCodec<ReferenceKeyCondition> CODEC = ReferenceMetaCondition.codec(ReferenceKeyCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceKeyCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.REFERENCE;
	}

	@Override
	public Pair<Class<KeyCondition>, String> classAndName() {
		return Pair.of(KeyCondition.class, "Key condition");
	}

	@Override
	public String asDisplayString() {
		return KeyCondition.super.asDisplayString();
	}

}
