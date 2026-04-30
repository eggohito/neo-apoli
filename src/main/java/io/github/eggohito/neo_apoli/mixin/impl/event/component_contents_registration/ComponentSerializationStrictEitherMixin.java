package io.github.eggohito.neo_apoli.mixin.impl.event.component_contents_registration;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraft/network/chat/ComponentSerialization$StrictEither")
public abstract class ComponentSerializationStrictEitherMixin<T> {

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
