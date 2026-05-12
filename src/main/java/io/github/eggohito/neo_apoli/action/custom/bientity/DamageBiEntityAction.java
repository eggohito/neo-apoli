package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBiEntityActionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public record DamageBiEntityAction(Holder<DamageType> damageType, NumberProvider amount) implements BiEntityAction {

	public static final MapCodec<DamageBiEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DamageType.CODEC.fieldOf("damage_type").forGetter(DamageBiEntityAction::damageType),
		NumberProvider.CODEC.fieldOf("amount").forGetter(DamageBiEntityAction::amount)
	).apply(instance, DamageBiEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageBiEntityAction> STREAM_CODEC = StreamCodec.composite(
		DamageType.STREAM_CODEC, DamageBiEntityAction::damageType,
		NumberProvider.STREAM_CODEC, DamageBiEntityAction::amount,
		DamageBiEntityAction::new
	);

	@Override
	public BiEntityAction.Type<?> getType() {
		return NeoApoliBiEntityActionTypes.DAMAGE;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		Entity actor = context.getNullable(NeoApoliContextParams.ACTOR_ENTITY);
		Entity target = context.getNullable(NeoApoliContextParams.TARGET_ENTITY);

		if (actor == null || target == null) {
			return;
		}

		float amount = amount().nextFloat(context.forChild(".amount"));
		target.hurtServer(serverLevel, new DamageSource(this.damageType(), actor), amount);

	}

	@Override
	public void validate(Context.Validator validator) {
		BiEntityAction.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
	}

}
