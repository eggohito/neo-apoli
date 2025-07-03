package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class UuidStringProvider extends StringProvider {

	public static final MapCodec<UuidStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityParameter.CODEC.fieldOf("source").forGetter(UuidStringProvider::source)
	).apply(instance, UuidStringProvider::new));

	public static final PacketCodec<RegistryByteBuf, UuidStringProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityParameter.PACKET_CODEC, UuidStringProvider::source,
		UuidStringProvider::new
	);

	private final EntityParameter source;

	public UuidStringProvider(EntityParameter source) {
		this.source = source;
	}

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.UUID;
	}

	@Override
	protected String impl(Context context) {
		return context.required(source().getParameter()).getUuidAsString();
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(source().getParameter());
	}

}
