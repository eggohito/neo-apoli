package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.key.KeyState;
import io.github.eggohito.neo_apoli.key.manager.KeyStateManager;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record KeyPressedTimeNumberProvider(StringProvider id, EntityProvider entity) implements NumberProvider {

	public static final MapCodec<KeyPressedTimeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(KeyPressedTimeNumberProvider::id),
		EntityProvider.CODEC.fieldOf("entity").forGetter(KeyPressedTimeNumberProvider::entity)
	).apply(instance, KeyPressedTimeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressedTimeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, KeyPressedTimeNumberProvider::id,
		EntityProvider.STREAM_CODEC, KeyPressedTimeNumberProvider::entity,
		KeyPressedTimeNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.KEY_PRESSED_TIME;
	}

	@Override
	public double getDouble(Context context) {
		return this.getLong(context);
	}

	@Override
	public long getLong(Context context) {
		return id().getString(context.forChild(".id"))
			.flatMap(id -> entity().getEntity(context.forChild(".entity"))
				.flatMap(entity -> KeyStateManager.getInstance().getState(entity.getUUID(), id)
					.map(KeyState::pressedTime))).orElse(0L);
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		id().validate(validator.forChild(".id"));
		entity().validate(validator.forChild(".entity"));
	}

}
