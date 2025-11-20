package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;

public record DamageItemAction(EntityTarget entity, NumberProvider amount, BooleanProvider ignoreUnbreaking) implements ItemAction {

	public static final MapCodec<DamageItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.optionalFieldOf("entity", EntityTarget.THIS).forGetter(DamageItemAction::entity),
		NumberProvider.CODEC.optionalFieldOf("amount", new ConstantNumberProvider(1)).forGetter(DamageItemAction::amount),
		BooleanProvider.CODEC.optionalFieldOf("ignore_unbreaking", new ConstantBooleanProvider(false)).forGetter(DamageItemAction::ignoreUnbreaking)
	).apply(instance, DamageItemAction::new));

	public static final PacketCodec<RegistryByteBuf, DamageItemAction> PACKET_CODEC = PacketCodec.tuple(
		EntityTarget.PACKET_CODEC, DamageItemAction::entity,
		NumberProvider.PACKET_CODEC, DamageItemAction::amount,
		BooleanProvider.PACKET_CODEC, DamageItemAction::ignoreUnbreaking,
		DamageItemAction::new
	);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.DAMAGE;
	}

	@Override
	public void serverExecute(ServerContext context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		StackReference stackReference = context.required(NeoApoliContextParameters.STACK_REFERENCE);
		ItemStack stack = stackReference.get();

		Context amountContext = context.makeChild(".amount");
		int amount = Math.abs(amount().nextInt(amountContext));

		if (!amountContext.hasErrors()) {

			Context ignoreUnbreakingContext = context.makeChild(".ignore_unbreaking");
			boolean ignoreUnbreaking = ignoreUnbreaking().next(ignoreUnbreakingContext);

			if (!ignoreUnbreakingContext.hasErrors()) {

				if (ignoreUnbreaking) {

					if (amount >= stack.getMaxDamage()) {
						stack.decrement(1);
					}

					else {
						stack.setDamage(stack.getDamage() + amount);
					}

				}

				else {

					ServerPlayerEntity itemHolder = context.optional(entity().getParameter())
						.filter(ServerPlayerEntity.class::isInstance)
						.map(ServerPlayerEntity.class::cast)
						.orElse(null);

					stack.damage(amount, context.getWorld(), itemHolder, item -> {});

				}

			}

		}

	}

	@Override
	public void validate(ErrorReporter reporter) {

		ItemAction.super.validate(reporter);

		amount().validate(reporter.makeChild(".amount"));
		ignoreUnbreaking().validate(reporter.makeChild(".ignore_unbreaking"));

	}

}
