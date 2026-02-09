package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.mixin.access.ResourceLocationAccessor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ResourceLocationUtil {

	private static final char PLACEHOLDER = '*';

	@Nullable
	@Getter
	@Setter
	public static ResourceLocation current;

	public static ResourceLocation nonEmpty(ResourceLocation id) {

		if (id.getNamespace().isEmpty() || id.getPath().isEmpty()) {
			throw new ResourceLocationException("Empty " + (id.getNamespace().isEmpty() ? "namespace" : "path") + " in identifier \"" + id + "\" is not allowed!");
		}

		else {
			return id;
		}

	}

	public static String replaceWithCurrent(String input, Function<ResourceLocation, String> replacement) {

		if (getCurrent() != null && NeoApoli.getConfig().placeholderIdentifier.enabled) {
			return input.replace(String.valueOf(PLACEHOLDER), replacement.apply(getCurrent()));
		}

		else {
			return input;
		}

	}

	public static ResourceLocation bySeparatorAndDefaultNamespace(String input, String separator, String defaultNamespace) {

		int separatorIndex = input.indexOf(separator);
		if (separatorIndex >= 0) {

			String namespace = input.substring(0, separatorIndex);
			String path = input.substring(separatorIndex + 1);

			if (separatorIndex != 0) {
				return ResourceLocationAccessor.callCreateUntrusted(namespace, path);
			}

			else {
				return ResourceLocationAccessor.callCreateUntrusted(defaultNamespace, path);
			}

		}

		else {
			return ResourceLocationAccessor.callCreateUntrusted(defaultNamespace, input);
		}

	}

	public static Codec<ResourceLocation> codecWithSeparatorAndDefaultNamespace(String separator, String defaultNamespace) {
		return Codec.STRING.comapFlatMap(
			input -> {

				try {
					return DataResult.success(bySeparatorAndDefaultNamespace(input, separator, defaultNamespace));
				}

				catch (ResourceLocationException rle) {
					return DataResult.error(rle::getMessage);
				}

			},
			ResourceLocation::toString
		);
	}

	public static Codec<ResourceLocation> codecWithDefaultNamespace(String defaultNamespace) {
		return codecWithSeparatorAndDefaultNamespace(":", defaultNamespace);
	}

	public static boolean isEnabledAndPlaceholder(char ch) {
		return getCurrent() != null
			&& NeoApoli.getConfig().placeholderIdentifier.enabled
			&& ch == PLACEHOLDER;
	}

}
