package io.github.eggohito.neo_apoli.action.category;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.category.Category;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface ActionCategory<A extends Action> extends Category<A> {

	Codec<ActionCategory<?>> CODEC = NeoApoliRegistries.ACTION_CATEGORY.getCodec();
	PacketCodec<RegistryByteBuf, ActionCategory<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ACTION_CATEGORY);

}
