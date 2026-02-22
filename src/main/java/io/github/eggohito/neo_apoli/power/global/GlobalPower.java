package io.github.eggohito.neo_apoli.power.global;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.util.tag.LazyTagLike;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public record GlobalPower(LazyTagLike<EntityType<?>> entityTypes, LazyTagLike<PowerEntry<?>> powers, boolean replace, int order) implements ContextUser, Comparable<GlobalPower> {

	public static final ResourceLocation POWER_SOURCE = NeoApoli.id("global");

	public static final MapCodec<GlobalPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.LAZY_ENTITY_TYPE_TAG_LIKE.optionalFieldOf("entity_types", new LazyTagLike.Builder<>(BuiltInRegistries.ENTITY_TYPE).build()).forGetter(GlobalPower::entityTypes),
		NeoApoliCodecs.LAZY_POWER_TAG_LIKE.fieldOf("powers").forGetter(GlobalPower::powers),
		Codec.BOOL.optionalFieldOf("replace", false).forGetter(GlobalPower::replace),
		Codec.INT.optionalFieldOf("order", 0).forGetter(GlobalPower::order)
	).apply(instance, GlobalPower::new));

	@Override
	public void validate(Context.Validator validator) {
		entityTypes().loadCache().ifError(error -> validator.forChild(".entity_types").reportProblem(error.message()));
		powers().loadCache().ifError(error -> validator.forChild(".powers").reportProblem(error.message()));
	}

	@Override
	public int compareTo(@NotNull GlobalPower that) {
		return Integer.compare(this.order(), that.order());
	}

	public boolean doesApply(Entity entity) {
		ImmutableList<EntityType<?>> entityTypeElements = entityTypes().elements();
		return entityTypeElements.isEmpty()
			|| entityTypeElements.contains(entity.getType());
	}

	public record WithSource(GlobalPower set, ResourceLocation id, String source) {

	}

}
