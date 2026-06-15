package io.github.eggohito.neo_apoli.power.custom.misc;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.AttributedModifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

public interface AttributeModifyingPower extends Power {

	List<AttributedModifier> modifiers();

	BooleanProvider sendUpdate();

	@Override
	AttributeModifyingPower.Instance<?> createInstance();

	abstract class Instance<P extends AttributeModifyingPower> extends Power.Instance<P> {

		protected Instance(@NotNull P power) {
			super(power);
		}

		protected void processModifiers(Entity holder, Context context, BiConsumer<AttributeInstance, AttributeModifier> processor) {

			if (!(holder instanceof LivingEntity livingHolder) || livingHolder.level().isClientSide()) {
				return;
			}

			Context sendUpdateContext = context.forChild(".send_update");
			boolean sendUpdate = power.sendUpdate().getBoolean(sendUpdateContext);

			if (!sendUpdateContext.hasErrors()) {

				for (var attributeModifier: power.modifiers()) {

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

		protected void addModifiersPersistently(Entity holder, Context context) {
			this.processModifiers(holder, context, AttributeInstance::addOrReplacePermanentModifier);
		}

		protected void addModifiersTemporarily(Entity holder, Context context) {
			this.processModifiers(holder, context, (attributeInstance, modifier) -> {

				if (!attributeInstance.hasModifier(modifier.id())) {
					attributeInstance.addTransientModifier(modifier);
				}

			});
		}

		protected void removeModifiers(Entity holder, Context context) {
			this.processModifiers(holder, context, AttributeInstance::removeModifier);
		}

	}

	static <P extends AttributeModifyingPower> Products.P2<RecordCodecBuilder.Mu<P>, List<AttributedModifier>, BooleanProvider> addFields(RecordCodecBuilder.Instance<P> instance) {
		return instance.group(
			NeoApoliCodecs.NONEMPTY_ATTRIBUTE_MODIFIERS.fieldOf("modifiers").forGetter(AttributeModifyingPower::modifiers),
			BooleanProvider.CODEC.optionalFieldOf("send_update", new ConstantBooleanProvider(true)).forGetter(AttributeModifyingPower::sendUpdate)
		);
	}

}
