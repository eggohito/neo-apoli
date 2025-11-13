package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;

public record DamageBiEntityAction(RegistryEntry<DamageType> damageType, NumberProvider amount) implements BiEntityAction {

	public static final MapCodec<DamageBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DamageType.ENTRY_CODEC.fieldOf("damage_type").forGetter(DamageBiEntityAction::damageType),
		NumberProvider.CODEC.fieldOf("amount").forGetter(DamageBiEntityAction::amount)
	).apply(instance, DamageBiEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, DamageBiEntityAction> PACKET_CODEC = PacketCodec.tuple(
		DamageType.ENTRY_PACKET_CODEC, DamageBiEntityAction::damageType,
		NumberProvider.PACKET_CODEC, DamageBiEntityAction::amount,
		DamageBiEntityAction::new
	);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.DAMAGE;
	}

	@Override
	public void execute(Context context) {

		if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		Context amountContext = context.makeChild(".amount");
		float amount = amount().nextFloat(amountContext);

		if (amountContext.hasErrors()) {
			return;
		}

		Entity actor = context.nullable(ContextParameters.ACTOR);
		Entity target = context.nullable(ContextParameters.TARGET);

		if (actor != null && target != null) {
			target.damage(serverWorld, new DamageSource(this.damageType(), actor), amount);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		BiEntityAction.super.validate(reporter);
		amount().validate(reporter.makeChild(".amount"));
	}

}
