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
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return 0L;
		}

		Context idContext = context.forChild(".id");
		String id = id().getString(idContext);

		if (idContext.hasErrors() || id.isEmpty()) {
			return 0L;
		}

		UUID uuid = entity().getEntity(context.forChild(".entity"))
			.map(Entity::getUUID)
			.orElse(null);

		if (uuid == null) {
			return 0L;
		}

		return KeyStateManager.getInstance().getState(uuid, id)
			.filter(KeyState::pressed)
			.map(KeyState::pressedTime)
			.orElse(0L);

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		id().validate(validator.forChild(".id"));
		entity().validate(validator.forChild(".entity"));
	}

}
