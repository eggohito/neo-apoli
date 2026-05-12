package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.api.key.KeyStateManager;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public record KeyPressedTimeNumberProvider(StringProvider id) implements NumberProvider {

	public static final MapCodec<KeyPressedTimeNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(KeyPressedTimeNumberProvider::id)
	).apply(instance, KeyPressedTimeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressedTimeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, KeyPressedTimeNumberProvider::id,
		KeyPressedTimeNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.KEY_PRESSED_TIME;
	}

	@Override
	public double nextDouble(Context context) {
		return this.nextLong(context);
	}

	@Override
	public long nextLong(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return 0L;
		}

		Context idContext = context.forChild(".id");
		String id = id().nextString(idContext);

		if (idContext.hasErrors() || id.isEmpty()) {
			return 0L;
		}

		UUID uuid = context.getRequired(NeoApoliContextParams.THIS_ENTITY).getUUID();
		return KeyStateManager.getState(uuid, id)
			.filter(KeyState::pressed)
			.map(KeyState::pressedTime)
			.orElse(0L);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.THIS_ENTITY);
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		id().validate(validator.forChild(".id"));
	}

}
