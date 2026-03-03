package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliNestedTagCaches;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyEntityTypeTagPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyEntityTypeTagPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(TagKey.hashedCodec(Registries.ENTITY_TYPE).fieldOf("tag").forGetter(ModifyEntityTypeTagPower::getTag))
		.apply(instance, ModifyEntityTypeTagPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyEntityTypeTagPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		TagKey.streamCodec(Registries.ENTITY_TYPE), ModifyEntityTypeTagPower::getTag,
		ModifyEntityTypeTagPower::new
	);

	private final TagKey<EntityType<?>> tag;

	public ModifyEntityTypeTagPower(Optional<Condition> activeCondition, TagKey<EntityType<?>> tag) {
		super(activeCondition);
		this.tag = tag;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ENTITY_TYPE_TAG;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		RegistryUtil.validateTag(validator.forChild(".tag"), this.getTag());
	}

	public static class Instance extends Power.Instance<ModifyEntityTypeTagPower> {

		protected Instance(@NotNull ModifyEntityTypeTagPower power) {
			super(power);
		}

		public boolean doesApply(TagKey<EntityType<?>> tag) {

			if (Objects.equals(power.getTag(), tag)) {
				return true;
			}

			else {

				for (var nestedTag : NeoApoliNestedTagCaches.ENTITY_TYPE.getOrEmpty(tag)) {

					if (this.doesApply(nestedTag)) {
						return true;
					}

				}

				return false;

			}

		}

	}

	public static boolean modify(Entity entity, HolderSet<EntityType<?>> directTag) {
		return directTag.unwrapKey()
			.map(tag -> modify(entity, tag))
			.orElse(false);
	}

	public static boolean modify(Entity entity, TagKey<EntityType<?>> tag) {

		for (var instance : PowersComponent.getInstances(entity, Instance.class)) {

			Context context = instance.createHolderContext(entity);

			try {

				if (VISITOR.push(instance) && instance.isActive(context) && instance.doesApply(tag)) {
					return true;
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return false;

	}

}
