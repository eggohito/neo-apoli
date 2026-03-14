package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public record EquippedEnchantmentLevelNumberProvider(Holder<Enchantment> enchantment, EquipmentSlotGroup slotGroup, Calculation calculation, ContextParameter<Entity> entity) implements NumberProvider {

	public static final MapCodec<EquippedEnchantmentLevelNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Enchantment.CODEC.fieldOf("enchantment").forGetter(EquippedEnchantmentLevelNumberProvider::enchantment),
		EquipmentSlotGroup.CODEC.optionalFieldOf("slot_group", EquipmentSlotGroup.ANY).forGetter(EquippedEnchantmentLevelNumberProvider::slotGroup),
		Calculation.CODEC.optionalFieldOf("calculation", Calculation.MAX).forGetter(EquippedEnchantmentLevelNumberProvider::calculation),
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(EquippedEnchantmentLevelNumberProvider::entity)
	).apply(instance, EquippedEnchantmentLevelNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EquippedEnchantmentLevelNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Enchantment.STREAM_CODEC, EquippedEnchantmentLevelNumberProvider::enchantment,
		EquipmentSlotGroup.STREAM_CODEC, EquippedEnchantmentLevelNumberProvider::slotGroup,
		Calculation.STREAM_CODEC, EquippedEnchantmentLevelNumberProvider::calculation,
		NeoApoliContextParams.StreamCodecs.ENTITY, EquippedEnchantmentLevelNumberProvider::entity,
		EquippedEnchantmentLevelNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.EQUIPPED_ENCHANTMENT_LEVEL;
	}

	@Override
	public double nextDouble(Context context) {

		Registry<Enchantment> enchantmentRegistry = context.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		ResourceLocation enchantmentId = enchantment().unwrap().map(ResourceKey::location, enchantmentRegistry::getKey);

		switch (context.getNullable(entity())) {
			case LivingEntity livingEntity -> {
				return calculation().getValue(livingEntity, enchantment(), slotGroup());
			}
			case null ->
				context.reportProblem("Couldn't get enchantment levels of enchantment \"" + enchantmentId + "\" from entity from parameter \"" + entity().name() + "\", which didn't exist!");
			default ->
				context.reportProblem("Couldn't get enchantment levels of enchantment \"" + enchantmentId + "\" from entity from parameter \"" + entity().name() + "\", which cannot equip items!");
		}

		return 0;

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
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
