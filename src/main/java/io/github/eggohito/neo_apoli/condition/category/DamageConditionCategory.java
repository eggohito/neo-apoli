package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class DamageConditionCategory extends ConditionCategory<DamageCondition> {

	DamageConditionCategory() {

	}

	@Override
	public RegistryKey<? extends Registry<DamageCondition>> registryRef() {
		return NeoApoliRegistryKeys.DAMAGE_CONDITION;
	}

	@Override
	public PacketCodec<RegistryByteBuf, DamageCondition> packetCodec() {
		return DamageCondition.PACKET_CODEC;
	}

	@Override
	public Codec<DamageCondition> codec() {
		return DamageCondition.CODEC;
	}

	@Override
	public MapCodec<DamageCondition> mapCodec() {
		return DamageCondition.MAP_CODEC;
	}

}
