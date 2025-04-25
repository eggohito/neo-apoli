package io.github.eggohito.neo_apoli.action.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface ActionType<A extends Action<?>> {

	MapCodec<A> mapCodec();

	PacketCodec<RegistryByteBuf, A> packetCodec();

}
