package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.server.world.ServerWorld;

@EqualsAndHashCode
@Data
public final class DamageBiEntityAction extends BiEntityAction {

	public static final MapCodec<DamageBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegistryFixedCodec.of(RegistryKeys.DAMAGE_TYPE).fieldOf("damage_type").forGetter(DamageBiEntityAction::damageType),
		NumberProvider.CODEC.fieldOf("amount").forGetter(DamageBiEntityAction::amount)
	).apply(instance, DamageBiEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, DamageBiEntityAction> PACKET_CODEC = PacketCodec.tuple(
		DamageType.ENTRY_PACKET_CODEC, DamageBiEntityAction::damageType,
		NumberProvider.PACKET_CODEC, DamageBiEntityAction::amount,
		DamageBiEntityAction::new
	);

	private final RegistryEntry<DamageType> damageType;
	private final NumberProvider amount;

	public DamageBiEntityAction(RegistryEntry<DamageType> damageType, NumberProvider amount) {
		this.damageType = damageType;
		this.amount = amount;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.DAMAGE;
	}

	@Override
	protected void impl(Context context) {

		if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		Context amountContext = context.makeChild(".amount");
		float amount = amount().nextFloat(amountContext);

		if (!amountContext.hasErrors()) {
			return;
		}

		Entity actor = context.required(ContextParameters.ACTOR);
		Entity target = context.required(ContextParameters.TARGET);

		DamageSource damageSource = new DamageSource(this.damageType(), actor);
		target.damage(serverWorld, damageSource, amount);

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		amount().validate(reporter.makeChild(".amount"));
	}

}
