package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.NotNull;

@Getter
public class CraftingRecipePower extends Power implements Prioritized<CraftingRecipePower> {

	public static final MapCodec<CraftingRecipePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance)
		.and(NeoApoliMapCodecs.CRAFTING_RECIPE_ENTRY.codec().fieldOf("recipe").forGetter(CraftingRecipePower::getRecipeEntry))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(CraftingRecipePower::getPriority))
		.apply(instance, CraftingRecipePower::new));

	public static final PacketCodec<RegistryByteBuf, CraftingRecipePower> PACKET_CODEC = createCommonPacketCodec(
		(buf, power) -> {
			NeoApoliPacketCodecs.CRAFTING_RECIPE_ENTRY.encode(buf, power.getRecipeEntry());
			buf.writeInt(power.getPriority());
		},
		(buf, properties) -> new CraftingRecipePower(properties,
			NeoApoliPacketCodecs.CRAFTING_RECIPE_ENTRY.decode(buf),
			buf.readInt()
		)
	);

	private final RecipeEntry<CraftingRecipe> recipeEntry;
	private final int priority;

	public CraftingRecipePower(Properties properties, RecipeEntry<CraftingRecipe> recipeEntry, int priority) {
		super(properties);
		this.recipeEntry = recipeEntry;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CRAFTING_RECIPE;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<CraftingRecipePower> {

		protected Instance(@NotNull Entity holder, @NotNull CraftingRecipePower power) {
			super(holder, power);
		}

		public RecipeEntry<CraftingRecipe> getRecipeEntry() {
			return this.getPower().getRecipeEntry();
		}

	}

}
