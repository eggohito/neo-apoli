package io.github.eggohito.neo_apoli.mixin.impl.event.component_contents_registration;

import com.google.common.collect.ImmutableList;
import io.github.eggohito.neo_apoli.event.ComponentContentsRegistration;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentSerialization.class)
public abstract class ComponentSerializationMixin {

	@ModifyArg(method = "createCodec", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/ComponentSerialization;createLegacyComponentMatcher([Lnet/minecraft/util/StringRepresentable;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"))
	private static StringRepresentable[] addCustomComponentContents(StringRepresentable[] original) {

		ImmutableList.Builder<StringRepresentable> typesBuilder = ImmutableList.builder();

		for (var type : original) {
			typesBuilder.add(type);
		}

		ComponentContentsRegistration.EVENT.invoker().register(typesBuilder::add);
		return typesBuilder.build().toArray(StringRepresentable[]::new);

	}

}
