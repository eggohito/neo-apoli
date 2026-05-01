package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record AttributeNumberProvider(Holder<Attribute> attribute, Context.Parameter<Entity> entity) implements NumberProvider {

	public static final MapCodec<AttributeNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Attribute.CODEC.fieldOf("attribute").forGetter(AttributeNumberProvider::attribute),
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(AttributeNumberProvider::entity)
	).apply(instance, AttributeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttributeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Attribute.STREAM_CODEC, AttributeNumberProvider::attribute,
		NeoApoliContextParams.StreamCodecs.ENTITY, AttributeNumberProvider::entity,
		AttributeNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.ATTRIBUTE;
	}

	@Override
	public double nextDouble(Context context) {

		try {

			Entity entity = context.getNullable(entity());
			ResourceLocation attributeLocation = attribute().unwrap().map(ResourceKey::location, BuiltInRegistries.ATTRIBUTE::getKey);

			switch (entity) {
				case LivingEntity livingEntity when context.visitor().push(this) -> {

					if (livingEntity.getAttributes().hasAttribute(attribute())) {
						return livingEntity.getAttributeValue(attribute());
					}

					else {
						context.forChild(".entity").reportProblem("Entity didn't have the attribute \"" + attributeLocation + "\"!");
					}

				}
				case LivingEntity ignored -> {
					//  No-op because this provider was recursively invoked
				}
				case null ->
					context.forChild(".entity").reportProblem("Entity doesn't exist!");
				default ->
					context.forChild(".entity").reportProblem("Entity can't have attributes!");
			}

			return 0.0D;

		}

		finally {
			context.visitor().pop(this);
		}

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
