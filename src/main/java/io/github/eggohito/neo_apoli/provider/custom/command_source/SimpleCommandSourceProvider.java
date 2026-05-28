package io.github.eggohito.neo_apoli.provider.custom.command_source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;

import java.util.function.Function;

public interface SimpleCommandSourceProvider<P extends SimpleCommandSourceProvider<P>> extends CommandSourceProvider, CommandSourceProvider.Type<P> {

	Codec<SimpleCommandSourceProvider<?>> CODEC = Type.CODEC.comapFlatMap(SimpleCommandSourceProvider::validate, Function.identity());

	@Override
	default SimpleCommandSourceProvider<P> getType() {
		return this;
	}

	private static DataResult<SimpleCommandSourceProvider<?>> validate(CommandSourceProvider.Type<?> type) {

		if (type instanceof SimpleCommandSourceProvider<?> self) {
			return DataResult.success(self);
		}

		else {
			return DataResult.error(() -> "Command source provider type \"" + RegistryUtil.getId(NeoApoliRegistries.COMMAND_SOURCE_PROVIDER_TYPE, type) + "\" requires parameters!");
		}

	}

}
