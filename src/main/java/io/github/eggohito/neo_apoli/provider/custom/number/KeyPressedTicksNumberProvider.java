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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public record KeyPressedTicksNumberProvider(StringProvider id) implements NumberProvider {

	public static final MapCodec<KeyPressedTicksNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(KeyPressedTicksNumberProvider::id)
	).apply(instance, KeyPressedTicksNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressedTicksNumberProvider> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, KeyPressedTicksNumberProvider::id,
		KeyPressedTicksNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.KEY_PRESSED_TICKS;
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

		Level level = context.level();
		UUID uuid = context.getRequired(NeoApoliContextParams.THIS_ENTITY).getUUID();

		return KeyStateManager.getState(uuid, id)
			.filter(KeyState::pressed)
			.map(KeyState::pressedTime)
			.map(pressedTime -> level.getGameTime() - pressedTime)
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
