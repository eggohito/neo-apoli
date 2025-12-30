package io.github.eggohito.neo_apoli.power;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.tag.LazyTagLike;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public record GlobalPowerSet(LazyTagLike<EntityType<?>> entityTypes, LazyTagLike<PowerEntry<?>> powers, boolean replace, int order) implements ContextAware, Comparable<GlobalPowerSet> {

	public static final ResourceLocation SOURCE = NeoApoli.id("global");

	public static final MapCodec<GlobalPowerSet> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.LAZY_ENTITY_TYPE_TAG_LIKE.optionalFieldOf("entity_types", new LazyTagLike.Builder<>(BuiltInRegistries.ENTITY_TYPE).build()).forGetter(GlobalPowerSet::entityTypes),
		NeoApoliCodecs.LAZY_POWER_TAG_LIKE.fieldOf("powers").forGetter(GlobalPowerSet::powers),
		Codec.BOOL.optionalFieldOf("replace", false).forGetter(GlobalPowerSet::replace),
		Codec.INT.optionalFieldOf("order", 0).forGetter(GlobalPowerSet::order)
	).apply(instance, GlobalPowerSet::new));

	@Override
	public void validate(Context.Validator validator) {
		entityTypes().loadCache().ifError(error -> validator.forChild(".entity_types").report(error.message()));
		powers().loadCache().ifError(error -> validator.forChild(".powers").report(error.message()));
	}

	@Override
	public int compareTo(@NotNull GlobalPowerSet that) {
		return Integer.compare(this.order(), that.order());
	}

	public boolean doesApply(Entity entity) {
		ImmutableList<EntityType<?>> entityTypeElements = entityTypes().elements();
		return entityTypeElements.isEmpty()
			|| entityTypeElements.contains(entity.getType());
	}

	public record WithSource(GlobalPowerSet set, ResourceLocation id, String source) {

	}

}
