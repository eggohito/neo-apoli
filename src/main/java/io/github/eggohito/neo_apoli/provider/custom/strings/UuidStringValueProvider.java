package io.github.eggohito.neo_apoli.provider.custom.strings;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.StringValueProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.strings.StringValueProviderType;
import io.github.eggohito.neo_apoli.provider.type.strings.StringValueProviderTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record UuidStringValueProvider(LootContext.EntityTarget source) implements StringValueProvider {

	public static final MapCodec<UuidStringValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		LootContext.EntityTarget.CODEC.fieldOf("source").forGetter(UuidStringValueProvider::source)
	).apply(instance, UuidStringValueProvider::new));

	public static final PacketCodec<RegistryByteBuf, UuidStringValueProvider> PACKET_CODEC = NeoApoliPacketCodecs.ENTITY_TARGET.xmap(
		UuidStringValueProvider::new,
		UuidStringValueProvider::source
	).cast();

	@Override
	public StringValueProviderType<?> getType() {
		return StringValueProviderTypes.UUID;
	}

	@Override
	public String get(ValueProviderContext context) {
		return context.requireParameter(source().getParameter()).getUuidAsString();
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(source().getParameter());
	}

}
