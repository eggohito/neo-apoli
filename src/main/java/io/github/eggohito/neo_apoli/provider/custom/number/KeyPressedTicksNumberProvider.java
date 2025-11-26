package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.key.KeyState;
import io.github.eggohito.neo_apoli.key.KeyStateManager;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public record KeyPressedTicksNumberProvider(StringProvider id) implements NumberProvider {

	public static final MapCodec<KeyPressedTicksNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(KeyPressedTicksNumberProvider::id)
	).apply(instance, KeyPressedTicksNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressedTicksNumberProvider> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, KeyPressedTicksNumberProvider::id,
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

		Level world = context.getWorld();
		UUID uuid = context.required(NeoApoliContextKeys.THIS_ENTITY).getUUID();

		return KeyStateManager.getState(uuid, id)
			.filter(KeyState::pressed)
			.map(KeyState::pressedTime)
			.map(pressedTime -> world.getGameTime() - pressedTime)
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
