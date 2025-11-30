package io.github.eggohito.neo_apoli.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public final class DynamicResourceLocation {

	public static final Codec<ResourceLocation> CODEC = createCodec(ResourceLocation.DEFAULT_NAMESPACE);

	@Getter
	@Setter
	private static ResourceLocation current;

	public static Pair<String, String> split(String input, String defaultNamespace) {

		String[] strings = input.split(String.valueOf(ResourceLocation.NAMESPACE_SEPARATOR));
		int length = strings.length;

		if (length > 2) {
			throw new ResourceLocationException("Expected identifier \"" + input + "\" to have 1 colon, but found " + (length - 1) + "!");
		}

		else if (length == 1) {
			return Pair.of(defaultNamespace, strings[0]);
		}

		else {
			return Pair.of(strings[0], strings[1]);
		}

	}

	public static Pair<String, String> split(String input) {
		return split(input, ResourceLocation.DEFAULT_NAMESPACE);
	}

	public static ResourceLocation of(String namespace, String path) {

		if (namespace.contains("*")) {

			if (getCurrent() != null) {
				namespace = namespace.replace("*", getCurrent().getNamespace());
			}

			else {
				throw new ResourceLocationException("Identifiers may only contain the placeholder '*' in its namespace in reload listeners that support it!");
			}

		}

		if (path.contains("*")) {

			if (getCurrent() != null) {
				path = path.replace("*", getCurrent().getPath());
			}

			else {
				throw new ResourceLocationException("Identifiers may only contain the placeholder '*' in its path in reload listeners that support it!");
			}

		}

		return ResourceLocation.fromNamespaceAndPath(namespace, path);

	}

	public static DataResult<ResourceLocation> parse(String input, String defaultNamespace) {

		try {

			Pair<String, String> namespaceAndPath = split(input, defaultNamespace);

			return DataResult.success(of(namespaceAndPath.getFirst(), namespaceAndPath.getSecond()));

		}

		catch (ResourceLocationException rle) {
			return DataResult.error(rle::getMessage);
		}

	}

	public static boolean isAllowed(char ch) {
		return ch == '*'
			|| ResourceLocation.isAllowedInResourceLocation(ch);
	}

	public static DataResult<ResourceLocation> parse(String input) {
		return parse(input, ResourceLocation.DEFAULT_NAMESPACE);
	}

	public static Codec<ResourceLocation> createCodec(String defaultNamespace) {
		return Codec.STRING.comapFlatMap(input -> parse(input, defaultNamespace), ResourceLocation::toString);
	}

}
