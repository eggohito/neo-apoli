package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.StringProviderTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record UuidStringProvider(LootContext.EntityTarget source) implements StringProvider {

	public static final MapCodec<UuidStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		LootContext.EntityTarget.CODEC.fieldOf("source").forGetter(UuidStringProvider::source)
	).apply(instance, UuidStringProvider::new));

	public static final PacketCodec<RegistryByteBuf, UuidStringProvider> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.ENTITY_TARGET, UuidStringProvider::source,
		UuidStringProvider::new
	);

	@Override
	public String get(ErrorReporter reporter, ValueProviderContext context) {
		return context.requireParameter(source.getParameter()).getUuidAsString();
	}

	@Override
	public Type<?> getType() {
		return StringProviderTypes.UUID;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(source().getParameter());
	}

	@Override
	public void validate(ErrorReporter reporter) {
		StringProvider.super.validate(reporter.makeChild("source"));
	}

}
