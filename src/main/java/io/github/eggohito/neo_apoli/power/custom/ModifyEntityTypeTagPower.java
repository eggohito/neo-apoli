package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliNestedTagCaches;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyEntityTypeTagPower extends Power {

	public static final MapCodec<ModifyEntityTypeTagPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		RegistryUtil.validateTag(validator.forChild(".tag"), this.getTag());
	}

	public static class Instance extends Power.Instance<ModifyEntityTypeTagPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyEntityTypeTagPower power) {
			super(holder, power);
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

	public static Context createContext(@NotNull Entity entity) {
		return PowerTypes.MODIFY_ENTITY_TYPE_TAG.contextBuilder()
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.THIS_POS, entity.position())
			.build(entity.level());
	}

	public static boolean doesApply(Context context, TagKey<EntityType<?>> tag) {

		Entity entity = context.nullable(NeoApoliContextKeys.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(entity, Instance.class);

		for (var instance : instances) {

			Context.Validator validator = instance.createValidator();
			Context instanceContext = new Context.Builder(context)
				.withValidator(validator)
				.build(context.getLevel());

			try {

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext) && instance.doesApply(tag)) {
					return true;
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return false;

	}

	public static boolean doesApply(Context context, HolderSet<EntityType<?>> tagsEntryList) {
		return tagsEntryList.unwrapKey()
			.map(tag -> doesApply(context, tag))
			.orElse(false);
	}

}
