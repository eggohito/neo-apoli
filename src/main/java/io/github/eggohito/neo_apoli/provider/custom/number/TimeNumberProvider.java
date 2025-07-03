package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.World;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class TimeNumberProvider extends NumberProvider {

	public static final MapCodec<TimeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("modulo").forGetter(TimeNumberProvider::modulo)
	).apply(instance, TimeNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, TimeNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(NumberProvider.PACKET_CODEC), TimeNumberProvider::modulo,
		TimeNumberProvider::new
	);

	private final Optional<NumberProvider> modulo;

	public TimeNumberProvider(Optional<NumberProvider> modulo) {
		this.modulo = modulo;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.TIME;
	}

	@Override
	protected Number impl(Context context) {

		World world = context.getWorld();
		long time = world.getTime();

		if (modulo().isPresent()) {

			Context moduloContext = context.makeChild(".modulo");
			long modulo = modulo().get().nextLong(moduloContext);

			if (!moduloContext.hasErrors()) {
				time %= modulo;
			}

		}

		return time;

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		modulo().ifPresent(modulo -> modulo.validate(reporter.makeChild(".modulo")));
	}

}
