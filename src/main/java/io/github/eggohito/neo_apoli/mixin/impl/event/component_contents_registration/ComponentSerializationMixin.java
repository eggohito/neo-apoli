package io.github.eggohito.neo_apoli.mixin.impl.event.component_contents_registration;

import io.github.eggohito.neo_apoli.event.ComponentContentsRegistration;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ComponentSerialization.class)
public abstract class ComponentSerializationMixin {

	@ModifyVariable(method = "createCodec", at = @At("STORE"))
	private static ComponentContents.Type<?>[] modify(ComponentContents.Type<?>[] original) {

		List<ComponentContents.Type<?>> modified = new ObjectArrayList<>(original);

		ComponentContentsRegistration.EVENT.invoker().register(type -> {

			for (var registeredType : modified) {

				if (registeredType.id().equals(type.id())) {
					throw new IllegalArgumentException("Component content type with ID \"" + type.id() + "\" was already registered!");
				}

			}

			modified.add(type);

		});

		return modified.toArray(ComponentContents.Type[]::new);

	}

}
