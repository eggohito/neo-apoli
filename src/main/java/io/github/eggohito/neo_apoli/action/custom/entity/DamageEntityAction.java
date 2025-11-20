package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public record DamageEntityAction(RegistryEntry<DamageType> damageType, NumberProvider amount) implements EntityAction {

	public static final MapCodec<DamageEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DamageType.ENTRY_CODEC.fieldOf("damage_type").forGetter(DamageEntityAction::damageType),
		NumberProvider.CODEC.fieldOf("amount").forGetter(DamageEntityAction::amount)
	).apply(instance, DamageEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, DamageEntityAction> PACKET_CODEC = PacketCodec.tuple(
		DamageType.ENTRY_PACKET_CODEC, DamageEntityAction::damageType,
		NumberProvider.PACKET_CODEC, DamageEntityAction::amount,
		DamageEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.DAMAGE;
	}

	@Override
	public void execute(Context context) {

		World world = context.getWorld();
		Entity entity = context.nullable(NeoApoliContextParameters.THIS_ENTITY);

		if (!(world instanceof ServerWorld serverWorld) || entity == null) {
			return;
		}

		Context amountContext = context.makeChild(".amount");
		float amount = amount().nextFloat(amountContext);

		if (!amountContext.hasErrors()) {
			entity.damage(serverWorld, new DamageSource(this.damageType()), amount);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		EntityAction.super.validate(reporter);
		amount().validate(reporter.makeChild(".amount"));
	}

}
