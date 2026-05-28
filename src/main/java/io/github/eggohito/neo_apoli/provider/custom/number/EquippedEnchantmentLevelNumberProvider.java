package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public record EquippedEnchantmentLevelNumberProvider(Holder<Enchantment> enchantment, EquipmentSlotGroup slotGroup, Calculation calculation, EntityProvider entity) implements NumberProvider {

	public static final MapCodec<EquippedEnchantmentLevelNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Enchantment.CODEC.fieldOf("enchantment").forGetter(EquippedEnchantmentLevelNumberProvider::enchantment),
		EquipmentSlotGroup.CODEC.optionalFieldOf("slot_group", EquipmentSlotGroup.ANY).forGetter(EquippedEnchantmentLevelNumberProvider::slotGroup),
		Calculation.CODEC.optionalFieldOf("calculation", Calculation.MAX).forGetter(EquippedEnchantmentLevelNumberProvider::calculation),
		EntityProvider.CODEC.fieldOf("entity").forGetter(EquippedEnchantmentLevelNumberProvider::entity)
	).apply(instance, EquippedEnchantmentLevelNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EquippedEnchantmentLevelNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Enchantment.STREAM_CODEC, EquippedEnchantmentLevelNumberProvider::enchantment,
		EquipmentSlotGroup.STREAM_CODEC, EquippedEnchantmentLevelNumberProvider::slotGroup,
		Calculation.STREAM_CODEC, EquippedEnchantmentLevelNumberProvider::calculation,
		EntityProvider.STREAM_CODEC, EquippedEnchantmentLevelNumberProvider::entity,
		EquippedEnchantmentLevelNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.EQUIPPED_ENCHANTMENT_LEVEL;
	}

	@Override
	public double getDouble(Context context) {

		Context entityContext = context.forChild(".entity");
		Optional<Entity> entity = entity().getEntity(entityContext);

		if (entity.isPresent() && entity.get() instanceof LivingEntity livingEntity) {
			return calculation().getValue(livingEntity, enchantment(), slotGroup());
		}

		else {
			return 0;
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		entity().validate(validator.forChild(".entity"));
	}

	public enum Calculation {

		SUM {

			@Override
			public int getValue(LivingEntity wearer, Holder<Enchantment> enchantment, EquipmentSlotGroup slotGroup) {

				Map<EquipmentSlot, ItemStack> slotItems = enchantment.value().getSlotItems(wearer);
				int levels = 0;

				for (var entry : slotItems.entrySet()) {

					EquipmentSlot slot = entry.getKey();
					ItemStack stack = entry.getValue();

					if (slotGroup.test(slot)) {
						levels += stack.getEnchantments().getLevel(enchantment);
					}

				}

				return levels;

			}

		},

		MAX {

			@Override
			public int getValue(LivingEntity wearer, Holder<Enchantment> enchantment, EquipmentSlotGroup slotGroup) {

				Map<EquipmentSlot, ItemStack> slotItems = enchantment.value().getSlotItems(wearer);
				int levels = 0;

				for (var entry : slotItems.entrySet()) {

					EquipmentSlot slot = entry.getKey();
					ItemStack stack = entry.getValue();

					if (slotGroup.test(slot)) {
						levels = Math.max(levels, stack.getEnchantments().getLevel(enchantment));
					}

				}

				return levels;

			}

		};

		public static final Codec<Calculation> CODEC = CodecUtil.enumType(Calculation.class);
		public static final StreamCodec<ByteBuf, Calculation> STREAM_CODEC = StreamCodecUtil.enumType(Calculation.class);

		public abstract int getValue(LivingEntity wearer, Holder<Enchantment> enchantmentHolder, EquipmentSlotGroup slotGroup);

	}

}
