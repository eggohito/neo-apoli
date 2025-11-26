package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public record DamageBiEntityAction(Holder<DamageType> damageType, NumberProvider amount) implements BiEntityAction {

	public static final MapCodec<DamageBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DamageType.CODEC.fieldOf("damage_type").forGetter(DamageBiEntityAction::damageType),
		NumberProvider.CODEC.fieldOf("amount").forGetter(DamageBiEntityAction::amount)
	).apply(instance, DamageBiEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageBiEntityAction> STREAM_CODEC = StreamCodec.composite(
		DamageType.STREAM_CODEC, DamageBiEntityAction::damageType,
		NumberProvider.STREAM_CODEC, DamageBiEntityAction::amount,
		DamageBiEntityAction::new
	);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.DAMAGE;
	}

	@Override
	public void execute(Context context) {

		if (!(context.getWorld() instanceof ServerLevel serverWorld)) {
			return;
		}

		Context amountContext = context.makeChild(".amount");
		float amount = amount().nextFloat(amountContext);

		if (amountContext.hasErrors()) {
			return;
		}

		Entity actor = context.nullable(NeoApoliContextKeys.ACTOR);
		Entity target = context.nullable(NeoApoliContextKeys.TARGET);

		if (actor != null && target != null) {
			target.hurtServer(serverWorld, new DamageSource(this.damageType(), actor), amount);
		}

	}

	@Override
	public void validate(ProblemReporter reporter) {
		BiEntityAction.super.validate(reporter);
		amount().validate(reporter.forChild(".amount"));
	}

}
