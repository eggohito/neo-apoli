package io.github.eggohito.neo_apoli.mixin.misc;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
import io.github.eggohito.neo_apoli.network.chat.contents.ForcedTranslatableContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentSerialization.class)
public abstract class ComponentSerializationMixin {

	@ModifyArg(method = "createCodec", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/ComponentSerialization;createLegacyComponentMatcher([Lnet/minecraft/util/StringRepresentable;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"))
	private static StringRepresentable[] addForcedTranslatableType(StringRepresentable[] original) {
		return ArrayUtils.add(original, ForcedTranslatableContents.TYPE);
	}

	@Mixin(targets = "net/minecraft/network/chat/ComponentSerialization$StrictEither")
	public static abstract class StrictEitherMixin<T> {

		@Shadow
		@Final
		private MapCodec<T> typed;

		/**
		 * @author eggohito
		 * @reason This ensures that when the text content is either sent to the client or to be converted
		 * 		to a different type, the client/decoder will know how to specifically decode the text content instead of
		 * 		relying on fuzzy matching. (e.g: if a mod adds another text content type that has the same fields as a
		 * 		vanilla text content type)
		 */
		@Overwrite
		public <O> RecordBuilder<O> encode(T input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
			return typed.encode(input, ops, prefix);
		}

	}

}
