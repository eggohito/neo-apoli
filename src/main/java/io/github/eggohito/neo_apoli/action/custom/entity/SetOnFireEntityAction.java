package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.context.entity.EntityActionContext;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextType;

public record SetOnFireEntityAction(NumberProvider ticks) implements EntityAction {

	public static final MapCodec<SetOnFireEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("ticks").forGetter(SetOnFireEntityAction::ticks)
	).apply(instance, SetOnFireEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, SetOnFireEntityAction> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, SetOnFireEntityAction::ticks,
		SetOnFireEntityAction::new
	);

	public static final ContextType CONTEXT_TYPE = new ContextType.Builder()
		.require(LootContextParameters.THIS_ENTITY)
		.require(LootContextParameters.ORIGIN)
		.allow(LootContextParameters.ATTACKING_ENTITY)
		.allow(LootContextParameters.LAST_DAMAGE_PLAYER)
		.build();

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SET_ON_FIRE;
	}

	@Override
	public void execute(ErrorReporter reporter, EntityActionContext context) {

		if (context.entity().isEmpty()) {
			return;
		}

		Entity entity = context.entity().get();
		ValueProviderContext providerContext = ValueProviderContext.builder(CONTEXT_TYPE)
			.add(LootContextParameters.THIS_ENTITY, entity)
			.add(LootContextParameters.ORIGIN, entity.getPos())
			.addOptional(LootContextParameters.ATTACKING_ENTITY, entity instanceof LivingEntity livingEntity ? livingEntity.getAttacker() : null)
			.addOptional(LootContextParameters.LAST_DAMAGE_PLAYER, entity instanceof LivingEntity livingEntity ? livingEntity.getAttackingPlayer() : null)
			.build(entity.getWorld());

		ErrorReporter finalReporter = reporter.withContextType(CONTEXT_TYPE);
		this.validate(finalReporter);

		finalReporter.getErrorsAsString().ifPresentOrElse(
			error -> NeoApoli.LOGGER.warn("Error executing entity action due to error {}", error),
			() -> entity.setOnFireForTicks(ticks().get(reporter, providerContext).intValue())
		);

	}

	@Override
	public void validate(ErrorReporter reporter) {
		ticks().validate(reporter.makeChild("ticks"));
	}

}
