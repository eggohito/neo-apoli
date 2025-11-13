package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingState;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingStateHolder;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public record KeyPressedTimeNumberProvider(StringProvider id) implements NumberProvider {

	public static final MapCodec<KeyPressedTimeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(KeyPressedTimeNumberProvider::id)
	).apply(instance, KeyPressedTimeNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, KeyPressedTimeNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, KeyPressedTimeNumberProvider::id,
		KeyPressedTimeNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.KEY_PRESSED_TIME;
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

		UUID uuid = context.required(ContextParameters.THIS_ENTITY).getUuid();
		return KeyBindingStateHolder.getState(uuid, id)
			.filter(KeyBindingState::pressed)
			.map(KeyBindingState::pressedTime)
			.orElse(0L);

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.THIS_ENTITY);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		id().validate(reporter.makeChild(".id"));
	}

}
