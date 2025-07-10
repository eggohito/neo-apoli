package io.github.eggohito.neo_apoli.action.category;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.category.Category;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public abstract class ActionCategory<A extends Action> implements Category<A> {

	public static final Codec<ActionCategory<?>> CODEC = NeoApoliRegistries.ACTION_CATEGORY.getCodec();
	public static final PacketCodec<RegistryByteBuf, ActionCategory<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ACTION_CATEGORY);

	private final Codec<A> entryCodec = new ValueSuppliedElementCodec<>(this.baseCodec(), true, id -> ActionManager.getAsResult(ActionCategory.this, id), ActionManager::getIdAsResult);

	@Override
	public Codec<A> entryCodec() {
		return entryCodec;
	}

}
