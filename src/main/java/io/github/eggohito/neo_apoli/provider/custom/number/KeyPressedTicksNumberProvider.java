package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.key.manager.KeyStateManager;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record KeyPressedTicksNumberProvider(StringProvider id, EntityProvider entity) implements NumberProvider {

	public static final MapCodec<KeyPressedTicksNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(KeyPressedTicksNumberProvider::id),
		EntityProvider.CODEC.fieldOf("entity").forGetter(KeyPressedTicksNumberProvider::entity)
	).apply(instance, KeyPressedTicksNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressedTicksNumberProvider> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, KeyPressedTicksNumberProvider::id,
		EntityProvider.STREAM_CODEC, KeyPressedTicksNumberProvider::entity,
		KeyPressedTicksNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.KEY_PRESSED_TICKS;
	}

	@Override
	public double getDouble(Context context) {
		return this.getLong(context);
	}

	@Override
	public long getLong(Context context) {
		return id().getString(context.forChild(".id"))
			.flatMap(id -> entity().getEntity(context.forChild(".entity"))
				.flatMap(entity -> KeyStateManager.getInstance().getCurrentState(entity.getUUID(), id)
					.map(state -> context.level().getGameTime() - state.pressedTime()))).orElse(0L);
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		id().validate(validator.forChild(".id"));
		entity().validate(validator.forChild(".entity"));
	}

}
