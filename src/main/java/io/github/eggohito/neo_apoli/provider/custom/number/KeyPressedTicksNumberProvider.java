package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingState;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingStateHolder;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public record KeyPressedTicksNumberProvider(StringProvider id) implements NumberProvider {

	public static final MapCodec<KeyPressedTicksNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(KeyPressedTicksNumberProvider::id)
	).apply(instance, KeyPressedTicksNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, KeyPressedTicksNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, KeyPressedTicksNumberProvider::id,
		KeyPressedTicksNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.KEY_PRESSED_TICKS;
	}

	@Override
	public @NotNull Number next(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return 0L;
		}

		Context idContext = context.makeChild(".id");
		String id = id().next(idContext);

		if (idContext.hasErrors()) {
			return 0L;
		}

		World world = context.getWorld();
		UUID uuid = context.required(NeoApoliContextParameters.THIS_ENTITY).getUuid();

		return KeyBindingStateHolder.getState(uuid, id)
			.filter(KeyBindingState::pressed)
			.map(KeyBindingState::pressedTime)
			.map(pressedTime -> world.getTime() - pressedTime)
			.orElse(0L);

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.THIS_ENTITY);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		id().validate(reporter.makeChild(".id"));
	}

}
