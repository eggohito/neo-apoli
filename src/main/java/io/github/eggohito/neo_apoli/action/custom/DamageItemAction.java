package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.slot.SlotProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record DamageItemAction(NumberProvider amount, SlotProvider slot, BooleanProvider ignoreEnchantments, Optional<EntityProvider> itemHolder) implements Action {

	public static final MapCodec<DamageItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("amount", new ConstantNumberProvider(1)).forGetter(DamageItemAction::amount),
		SlotProvider.CODEC.fieldOf("slot").forGetter(DamageItemAction::slot),
		BooleanProvider.CODEC.optionalFieldOf("ignore_enchantments", new ConstantBooleanProvider(false)).forGetter(DamageItemAction::ignoreEnchantments),
		EntityProvider.CODEC.optionalFieldOf("item_holder").forGetter(DamageItemAction::itemHolder)
	).apply(instance, DamageItemAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageItemAction> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, DamageItemAction::amount,
		SlotProvider.STREAM_CODEC, DamageItemAction::slot,
		BooleanProvider.STREAM_CODEC, DamageItemAction::ignoreEnchantments,
		ByteBufCodecs.optional(EntityProvider.STREAM_CODEC), DamageItemAction::itemHolder,
		DamageItemAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.DAMAGE_ITEM;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		SlotAccess slotAccess = slot().getSlot(context.forChild(".slot"));
		ItemStack stack = slotAccess.get();

		if (slotAccess == SlotAccess.NULL || stack.isEmpty() || !stack.isDamageableItem()) {
			return;
		}

		boolean ignoreEnchantments = ignoreEnchantments().getBoolean(context.forChild(".ignore_enchantments"));
		int amount = Math.abs(amount().getInt(context.forChild(".amount")));

		if (ignoreEnchantments) {

			if (amount >= stack.getMaxDamage()) {
				stack.shrink(1);
			}

			else {
				stack.setDamageValue(stack.getDamageValue() + amount);
			}

		}

		else {

			ServerPlayer itemHolder = itemHolder()
				.flatMap(p -> p.getEntity(context.forChild(".entity")))
				.filter(ServerPlayer.class::isInstance)
				.map(ServerPlayer.class::cast)
				.orElse(null);

			stack.hurtAndBreak(amount, serverLevel, itemHolder, item -> {});

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
		slot().validate(validator.forChild(".slot"));
		ignoreEnchantments().validate(validator.forChild(".ignore_enchantments"));
		itemHolder().ifPresent(p -> p.validate(validator.forChild(".item_holder")));
	}

}
