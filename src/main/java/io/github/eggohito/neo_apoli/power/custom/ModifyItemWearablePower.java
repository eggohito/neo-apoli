package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ItemContextParameter;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.Case;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public record ModifyItemWearablePower(EnumMap<EquipmentSlot, Case<Condition, BooleanProvider>> slots) implements Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();
	public static final Context.Parameter<ItemStack> WORN_ITEM = NeoApoliContextParams.registerInternal("worn_item", ItemContextParameter::new);

	private static final Codec<EnumMap<EquipmentSlot, Case<Condition, BooleanProvider>>> SLOTS_CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap(EquipmentSlot.CODEC, Case.codec(Condition.CODEC.fieldOf("condition"), BooleanProvider.CODEC.fieldOf("allow")))).xmap(EnumMap::new, Function.identity());
	private static final StreamCodec<RegistryFriendlyByteBuf, EnumMap<EquipmentSlot, Case<Condition, BooleanProvider>>> SLOTS_STREAM_CODEC = ByteBufCodecs.map(size -> new EnumMap<>(EquipmentSlot.class), EquipmentSlot.STREAM_CODEC, Case.streamCodec(Condition.STREAM_CODEC, BooleanProvider.STREAM_CODEC));

	public static final MapCodec<ModifyItemWearablePower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(SLOTS_CODEC.fieldOf("slots").forGetter(ModifyItemWearablePower::slots))
		.apply(instance, ModifyItemWearablePower::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyItemWearablePower> STREAM_CODEC = StreamCodec.composite(
		SLOTS_STREAM_CODEC, ModifyItemWearablePower::slots,
		ModifyItemWearablePower::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_ITEM_WEARABLE;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {

		Power.super.validate(validator);
		Context.Validator slotsValidator = validator.forChild(".slots");

		for (var entry : this.slots().entrySet()) {

			Context.Validator caseValidator = slotsValidator.forChild("." + entry.getKey().getSerializedName());
			Case<Condition, BooleanProvider> aCase = entry.getValue();

			aCase.condition().validate(caseValidator.forChild(".condition"));
			aCase.value().validate(caseValidator.forChild(".allow"));

		}

	}

	//	TODO: Unequip all previously equipped items and either drop or insert them to the inventory
	public static class Instance extends Power.Instance<ModifyItemWearablePower> {

		protected Instance(@NotNull ModifyItemWearablePower power) {
			super(power);
		}

		public Context createContext(Entity holder, ItemStack stack) {
			return this.createHolderContextBuilder(holder)
				.withRequired(WORN_ITEM, stack)
				.build(holder.level());
		}

		public EnumMap<EquipmentSlot, Case<Condition, BooleanProvider>> slots() {
			return power.slots();
		}

	}

	public static boolean modify(Entity equipper, ItemStack equippedStack, EquipmentSlot targetSlot, BooleanSupplier defaultValue) {

		boolean allowed = false;
		for (var instance : Powers.getInstances(equipper, Instance.class)) {

			Context instanceContext = instance.createContext(equipper, equippedStack);

			try {

				if (!VISITOR.push(instance) || !instance.isActive(instanceContext)) {
					continue;
				}

				for (var entry : instance.slots().entrySet()) {

					EquipmentSlot slot = entry.getKey();
					Case<Condition, BooleanProvider> aCase = entry.getValue();

					if (slot != targetSlot) {
						continue;
					}

					Context caseContext = instanceContext.forChild(".slots").forChild("." + slot.getSerializedName());

					if (aCase.condition().test(caseContext.forChild(".condition"))) {

						if (aCase.value().getBoolean(caseContext.forChild(".allow"))) {
							allowed = true;
						}

						else {
							return false;
						}

					}

				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return allowed
			|| defaultValue.getAsBoolean();

	}

}
