package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.category.Category;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface ConditionCategory<C extends Condition<?>> extends Category<C> {

	Codec<ConditionCategory<?>> CODEC = NeoApoliRegistries.CONDITION_CATEGORY.getCodec();
	PacketCodec<RegistryByteBuf, ConditionCategory<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.CONDITION_CATEGORY);

}
