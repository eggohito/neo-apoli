package io.github.eggohito.neo_apoli.action.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.MetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MetaActionType<A extends MetaAction>(MapCodec<A> mapCodec, StreamCodec<RegistryFriendlyByteBuf, A> packetCodec) implements ActionType<A> {

}
