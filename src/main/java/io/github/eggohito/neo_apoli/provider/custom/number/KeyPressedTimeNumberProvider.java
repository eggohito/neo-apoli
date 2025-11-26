package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.keybinding.KeyState;
import io.github.eggohito.neo_apoli.keybinding.KeyStateManager;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public record KeyPressedTimeNumberProvider(StringProvider id) implements NumberProvider {

	public static final MapCodec<KeyPressedTimeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(KeyPressedTimeNumberProvider::id)
	).apply(instance, KeyPressedTimeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressedTimeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, KeyPressedTimeNumberProvider::id,
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

		UUID uuid = context.required(NeoApoliContextKeys.THIS_ENTITY).getUUID();
		return KeyStateManager.getState(uuid, id)
			.filter(KeyState::pressed)
			.map(KeyState::pressedTime)
			.orElse(0L);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.THIS_ENTITY);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		NumberProvider.super.validate(reporter);
		id().validate(reporter.forChild(".id"));
	}

}
