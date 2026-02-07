package io.github.eggohito.neo_apoli.power.misc;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.AttributedModifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@EqualsAndHashCode
@Getter
public abstract class AttributeModifying extends Power {

	private final List<AttributedModifier> modifiers;
	private final BooleanProvider sendUpdate;

	public AttributeModifying(Optional<Condition> activeCondition, List<AttributedModifier> modifiers, BooleanProvider sendUpdate) {
		super(activeCondition);
		this.modifiers = modifiers;
		this.sendUpdate = sendUpdate;
	}

	public AttributeModifying(List<AttributedModifier> modifiers, BooleanProvider sendUpdate) {
		this.modifiers = modifiers;
		this.sendUpdate = sendUpdate;
	}

	@Override
	public abstract io.github.eggohito.neo_apoli.power.misc.AttributeModifying.Instance<?> createInstance(Entity holder);

	public static abstract class Instance<P extends AttributeModifying> extends Power.Instance<P> {

		protected Instance(@NotNull Entity holder, @NotNull P power) {
			super(holder, power);
		}

		protected void processModifiers(Context context, BiConsumer<AttributeInstance, net.minecraft.world.entity.ai.attributes.AttributeModifier> processor) {

			if (!(holder instanceof LivingEntity livingHolder) || livingHolder.level().isClientSide()) {
				return;
			}

			Context sendUpdateContext = context.forChild(".send_update");
			boolean sendUpdate = power.getSendUpdate().next(sendUpdateContext);

			if (!sendUpdateContext.hasErrors()) {

				for (var attributeModifier: power.getModifiers()) {

					AttributeInstance attributeInstance = livingHolder.getAttribute(attributeModifier.attribute());
					net.minecraft.world.entity.ai.attributes.AttributeModifier modifier = attributeModifier.modifier();

					if (attributeInstance == null) {
						continue;
					}

					float prevMaxHealth = livingHolder.getMaxHealth();
					float prevMaxHealthPercent = livingHolder.getHealth() / prevMaxHealth;

					processor.accept(attributeInstance, modifier);
					float currMaxHealth = livingHolder.getMaxHealth();

					if (sendUpdate && prevMaxHealth != currMaxHealth) {
						livingHolder.setHealth(currMaxHealth * prevMaxHealthPercent);
					}

				}

			}

		}

		protected void addModifiersPersistently(Context context) {
			this.processModifiers(context, AttributeInstance::addOrReplacePermanentModifier);
		}

		protected void addModifiersTemporarily(Context context) {
			this.processModifiers(context, (attributeInstance, modifier) -> {

				if (!attributeInstance.hasModifier(modifier.id())) {
					attributeInstance.addTransientModifier(modifier);
				}

			});
		}

		protected void removeModifiers(Context context) {
			this.processModifiers(context, AttributeInstance::removeModifier);
		}

	}

	protected static <P extends AttributeModifying> Products.P2<RecordCodecBuilder.Mu<P>, List<AttributedModifier>, BooleanProvider> addAttributeModifyingFields(RecordCodecBuilder.Instance<P> instance) {
		return instance.group(
			NeoApoliCodecs.NONEMPTY_ATTRIBUTE_MODIFIERS.fieldOf("modifiers").forGetter(AttributeModifying::getModifiers),
			BooleanProvider.CODEC.optionalFieldOf("send_update", new ConstantBooleanProvider(true)).forGetter(AttributeModifying::getSendUpdate)
		);
	}

	protected static <P extends AttributeModifying> Products.P3<RecordCodecBuilder.Mu<P>, Optional<Condition>, List<AttributedModifier>, BooleanProvider> addConditionalAttributeModifyingAndFields(RecordCodecBuilder.Instance<P> instance) {
		return addActiveConditionField(instance)
			.and(NeoApoliCodecs.NONEMPTY_ATTRIBUTE_MODIFIERS.fieldOf("modifiers").forGetter(AttributeModifying::getModifiers))
			.and(BooleanProvider.CODEC.optionalFieldOf("send_update", new ConstantBooleanProvider(true)).forGetter(AttributeModifying::getSendUpdate));
	}

}
