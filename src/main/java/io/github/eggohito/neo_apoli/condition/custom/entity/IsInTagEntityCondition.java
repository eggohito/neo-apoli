package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.EntityType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class IsInTagEntityCondition extends EntityCondition {

	public static final MapCodec<IsInTagEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.unprefixedCodec(RegistryKeys.ENTITY_TYPE).fieldOf("tag").forGetter(IsInTagEntityCondition::tag)
	).apply(instance, IsInTagEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsInTagEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		TagKey.packetCodec(RegistryKeys.ENTITY_TYPE), IsInTagEntityCondition::tag,
		IsInTagEntityCondition::new
	);

	private final TagKey<EntityType<?>> tag;

	public IsInTagEntityCondition(TagKey<EntityType<?>> tag) {
		this.tag = tag;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_IN_TAG;
	}

	@Override
	protected boolean impl(Context context) {
		return context.required(ContextParameters.THIS_ENTITY).getType().isIn(this.tag());
	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);
		Optional<RegistryEntryLookup<EntityType<?>>> entityTypeRegistry = reporter.getWrapperLookup().flatMap(wrapperLookup -> wrapperLookup.getOptional(this.tag().registryRef()));

		entityTypeRegistry.ifPresent(lookup -> lookup.getOptional(this.tag()).ifPresentOrElse(entries -> {}, () -> reporter.makeChild(".tag").report("Entity type tag \"" + this.tag().id() + "\" does not exist!")));

	}

}
