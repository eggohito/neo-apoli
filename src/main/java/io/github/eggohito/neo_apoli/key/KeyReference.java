package io.github.eggohito.neo_apoli.key;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record KeyReference(StringProvider id, BooleanProvider continuous) implements ContextUser {

	public static final MapCodec<KeyReference> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(KeyReference::id),
		BooleanProvider.CODEC.optionalFieldOf("continuous", new ConstantBooleanProvider(false)).forGetter(KeyReference::continuous)
	).apply(instance, KeyReference::new));

	public static final Codec<KeyReference> CODEC = new MultiAlternativeCodec<>(
		MAP_CODEC.codec(),
		StringProvider.CODEC.xmap(id -> new KeyReference(id, new ConstantBooleanProvider(false)), KeyReference::id)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, KeyReference> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, KeyReference::id,
		BooleanProvider.STREAM_CODEC, KeyReference::continuous,
		KeyReference::new
	);

	@Override
	public void validate(Context.Validator validator) {

		ContextUser.super.validate(validator);

		id().validate(validator.forChild(".id"));
		continuous().validate(validator.forChild(".continuous"));

	}

	public String id(Context context) {
		return id().getString(context.forChild(".id"));
	}

	public boolean continuous(Context context) {
		return continuous().getBoolean(context.forChild(".continuous"));
	}

}
