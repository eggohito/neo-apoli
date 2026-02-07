package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public record DamageEntityAction(Holder<DamageType> damageType, NumberProvider amount) implements EntityAction {

	public static final MapCodec<DamageEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DamageType.CODEC.fieldOf("damage_type").forGetter(DamageEntityAction::damageType),
		NumberProvider.CODEC.fieldOf("amount").forGetter(DamageEntityAction::amount)
	).apply(instance, DamageEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageEntityAction> STREAM_CODEC = StreamCodec.composite(
		DamageType.STREAM_CODEC, DamageEntityAction::damageType,
		NumberProvider.STREAM_CODEC, DamageEntityAction::amount,
		DamageEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.DAMAGE;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Entity entity = context.getRequired(NeoApoliContextParams.THIS_ENTITY);
		float amount = amount().nextFloat(context.forChild("amount"));

		entity.hurtServer(serverLevel, new DamageSource(this.damageType()), amount);

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityAction.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
	}

}
