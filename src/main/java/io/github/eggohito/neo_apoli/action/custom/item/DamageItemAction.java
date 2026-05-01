package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public record DamageItemAction(Context.Parameter<Entity> entity, NumberProvider amount, BooleanProvider ignoreEnchantments) implements ItemAction {

	public static final MapCodec<DamageItemAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliContextParams.Codecs.ENTITY.optionalFieldOf("entity", NeoApoliContextParams.THIS_ENTITY).forGetter(DamageItemAction::entity),
		NumberProvider.CODEC.optionalFieldOf("amount", new ConstantNumberProvider(1)).forGetter(DamageItemAction::amount),
		BooleanProvider.CODEC.optionalFieldOf("ignore_enchantments", new ConstantBooleanProvider(false)).forGetter(DamageItemAction::ignoreEnchantments)
	).apply(instance, DamageItemAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageItemAction> STREAM_CODEC = StreamCodec.composite(
		NeoApoliContextParams.StreamCodecs.ENTITY, DamageItemAction::entity,
		NumberProvider.STREAM_CODEC, DamageItemAction::amount,
		BooleanProvider.STREAM_CODEC, DamageItemAction::ignoreEnchantments,
		DamageItemAction::new
	);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.DAMAGE;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		SlotAccess stackAccess = context.getRequired(NeoApoliContextParams.SLOT_ACCESS);
		ItemStack stack = stackAccess.get();

		boolean ignoreEnchantments = ignoreEnchantments().nextBoolean(context.forChild(".ignore_enchantments"));
		int amount = Math.abs(amount().nextInt(context.forChild(".amount")));

		if (ignoreEnchantments) {

			if (amount >= stack.getMaxDamage()) {
				stack.shrink(1);
			}

			else {
				stack.setDamageValue(stack.getDamageValue() + amount);
			}

		}

		else {

			ServerPlayer itemHolder = context.getOptional(entity())
				.filter(ServerPlayer.class::isInstance)
				.map(ServerPlayer.class::cast)
				.orElse(null);

			stack.hurtAndBreak(amount, serverLevel, itemHolder, item -> {});

		}

	}

	@Override
	public void validate(Context.Validator validator) {

		ItemAction.super.validate(validator);

		amount().validate(validator.forChild(".amount"));
		ignoreEnchantments().validate(validator.forChild(".ignore_enchantments"));

	}

}
