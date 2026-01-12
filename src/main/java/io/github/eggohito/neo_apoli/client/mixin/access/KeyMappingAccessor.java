package io.github.eggohito.neo_apoli.client.mixin.access;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {

	@Accessor("ALL")
	static Map<String, KeyMapping> getKeysById() {
		throw new AssertionError();
	}

}
