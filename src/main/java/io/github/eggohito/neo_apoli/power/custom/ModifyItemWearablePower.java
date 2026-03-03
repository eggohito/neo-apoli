package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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

@EqualsAndHashCode
@Getter
public class ModifyItemWearablePower extends Power {

	private static final Codec<EnumMap<EquipmentSlot, Condition>> SLOTS_CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap(EquipmentSlot.CODEC, Condition.CODEC)).xmap(EnumMap::new, Function.identity());
	private static final StreamCodec<RegistryFriendlyByteBuf, EnumMap<EquipmentSlot, Condition>> SLOTS_STREAM_CODEC = ByteBufCodecs.map(size -> new EnumMap<>(EquipmentSlot.class), EquipmentSlot.STREAM_CODEC, Condition.STREAM_CODEC);

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyItemWearablePower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SLOTS_CODEC.fieldOf("slots").forGetter(ModifyItemWearablePower::getSlots),
		BooleanProvider.CODEC.fieldOf("allow").forGetter(ModifyItemWearablePower::getAllow)
	).apply(instance, ModifyItemWearablePower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyItemWearablePower> STREAM_CODEC = StreamCodec.composite(
		SLOTS_STREAM_CODEC, ModifyItemWearablePower::getSlots,
		BooleanProvider.STREAM_CODEC, ModifyItemWearablePower::getAllow,
		ModifyItemWearablePower::new
	);

	private final EnumMap<EquipmentSlot, Condition> slots;
	private final BooleanProvider allow;

	public ModifyItemWearablePower(EnumMap<EquipmentSlot, Condition> slots, BooleanProvider allow) {
		this.slots = slots;
		this.allow = allow;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ITEM_WEARABLE;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);

		Context.Validator slotsValidator = validator.forChild(".slots");
		for (var entry : this.getSlots().entrySet()) {
			entry.getValue().validate(slotsValidator.forChild("." + entry.getKey().getSerializedName()));
		}

	}

	//	TODO: Unequip all previously equipped items and either drop or insert them to the inventory
	public static class Instance extends Power.Instance<ModifyItemWearablePower> {

		protected Instance(@NotNull ModifyItemWearablePower power) {
			super(power);
		}

		public Context createContext(Entity holder, ItemStack stack) {
			return this.createHolderContextBuilder(holder)
				.withRequired(NeoApoliContextParams.ITEM_STACK, stack)
				.buildWithRequirements(holder.level(), PowerTypes.MODIFY_ITEM_WEARABLE.keySet());
		}

		public EnumMap<EquipmentSlot, Condition> getSlots() {
			return power.getSlots();
		}

		public boolean isAllowed(Context context) {
			return power.getAllow().nextBoolean(context.forChild(".allow"));
		}

	}

	public static boolean modify(Entity equipper, ItemStack equippedStack, EquipmentSlot targetSlot, BooleanSupplier defaultValue) {

		boolean allowed = false;
		for (var instance : PowersComponent.getInstances(equipper, Instance.class)) {

			Context instanceContext = instance.createContext(equipper, equippedStack);

			try {

				if (!VISITOR.push(instance) || !instance.isActive(instanceContext)) {
					continue;
				}

				for (var entry : instance.getSlots().entrySet()) {

					EquipmentSlot slot = entry.getKey();
					Condition condition = entry.getValue();

					if (slot != targetSlot) {
						continue;
					}

					Context slotsContext = instanceContext.forChild(".slots");

					if (condition.test(slotsContext.forChild("." + slot.getSerializedName()))) {

						if (instance.isAllowed(instanceContext)) {
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
