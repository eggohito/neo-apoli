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
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public record DamageItemAction(EntityTarget entity, NumberProvider amount, BooleanProvider ignoreUnbreaking) implements ItemAction {

	public static final MapCodec<DamageItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.optionalFieldOf("entity", EntityTarget.THIS).forGetter(DamageItemAction::entity),
		NumberProvider.CODEC.optionalFieldOf("amount", new ConstantNumberProvider(1)).forGetter(DamageItemAction::amount),
		BooleanProvider.CODEC.optionalFieldOf("ignore_unbreaking", new ConstantBooleanProvider(false)).forGetter(DamageItemAction::ignoreUnbreaking)
	).apply(instance, DamageItemAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageItemAction> STREAM_CODEC = StreamCodec.composite(
		EntityTarget.STREAM_CODEC, DamageItemAction::entity,
		NumberProvider.STREAM_CODEC, DamageItemAction::amount,
		BooleanProvider.STREAM_CODEC, DamageItemAction::ignoreUnbreaking,
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

		SlotAccess stackReference = context.required(NeoApoliContextKeys.STACK_REFERENCE);
		ItemStack stack = stackReference.get();

		Context amountContext = context.makeChild(".amount");
		int amount = Math.abs(amount().nextInt(amountContext));

		if (!amountContext.hasErrors()) {

			Context ignoreUnbreakingContext = context.makeChild(".ignore_unbreaking");
			boolean ignoreUnbreaking = ignoreUnbreaking().next(ignoreUnbreakingContext);

			if (!ignoreUnbreakingContext.hasErrors()) {

				if (ignoreUnbreaking) {

					if (amount >= stack.getMaxDamage()) {
						stack.shrink(1);
					}

					else {
						stack.setDamageValue(stack.getDamageValue() + amount);
					}

				}

				else {

					ServerPlayer itemHolder = context.optional(entity().getParameter())
						.filter(ServerPlayer.class::isInstance)
						.map(ServerPlayer.class::cast)
						.orElse(null);

					stack.hurtAndBreak(amount, context.getWorld(), itemHolder, item -> {});

				}

			}

		}

	}

	@Override
	public void validate(ProblemReporter reporter) {

		ItemAction.super.validate(reporter);

		amount().validate(reporter.forChild(".amount"));
		ignoreUnbreaking().validate(reporter.forChild(".ignore_unbreaking"));

	}

}
