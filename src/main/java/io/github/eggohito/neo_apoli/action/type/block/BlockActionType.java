package io.github.eggohito.neo_apoli.action.type.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record BlockActionType<A extends BlockAction>(MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) implements ActionType<A> {

}
