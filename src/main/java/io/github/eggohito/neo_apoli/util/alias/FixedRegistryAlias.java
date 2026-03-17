package io.github.eggohito.neo_apoli.util.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public class FixedRegistryAlias<T> extends CustomRegistryAlias<T> {

	private final Registry<T> registry;

	private FixedRegistryAlias(Registry<T> registry, ResourceLocationAlias aliases) {
		super(registry.key().location(), aliases, registry::getKey, registry::getValue);
		this.registry = registry;
	}

	private FixedRegistryAlias(Registry<T> registry) {
		this(registry, new ResourceLocationAlias());
	}

	@Override
	public Codec<T> createCodec(String defaultNamespace) {
		return this.createReferenceCodec(defaultNamespace).flatComapMap(Holder.Reference::value, value -> safeCastToReference(registry.wrapAsHolder(value)));
	}

	public DataResult<Holder.Reference<T>> resolveReference(ResourceLocation id) {
		return registry.get(aliases.resolve(id, registry::containsKey))
			.map(DataResult::success)
			.orElse(DataResult.error(() -> "Unknown key in registry \"" + registryId + "\": \"" + id + "\""));
	}

	public Codec<Holder.Reference<T>> createReferenceCodec(String defaultNamespace) {

		Codec<Holder.Reference<T>> codec = ResourceLocationUtil.codecWithDefaultNamespace(defaultNamespace).comapFlatMap(
			this::resolveReference,
			reference -> reference.key().location()
		);

		return ExtraCodecs.overrideLifecycle(
			codec,
			holder -> registry.registrationInfo(holder.key())
				.map(RegistrationInfo::lifecycle)
				.orElseGet(Lifecycle::experimental)
		);

	}

	public Codec<Holder.Reference<T>> createReferenceCodec() {
		return this.createReferenceCodec(ResourceLocation.DEFAULT_NAMESPACE);
	}

	public Codec<Holder<T>> createHolderCodec(String defaultNamespace) {
		return createReferenceCodec(defaultNamespace).flatComapMap(Function.identity(), this::safeCastToReference);
	}

	public Codec<Holder<T>> createHolderCodec() {
		return this.createHolderCodec(ResourceLocation.DEFAULT_NAMESPACE);
	}

	private DataResult<Holder.Reference<T>> safeCastToReference(Holder<T> holder) {
		return holder instanceof Holder.Reference<T> reference
			? DataResult.success(reference)
			: DataResult.error(() -> "Unregistered holder in registry \"" + registryId + "\": " + holder);
	}

	public static <T, U extends T> FixedRegistryAlias<U> extended(Registry<U> registry, FixedRegistryAlias<T> fixed) {
		return new FixedRegistryAlias<>(registry, fixed.aliases);
	}

	public static <T> FixedRegistryAlias<T> of(Registry<T> registry) {
		return new FixedRegistryAlias<>(registry);
	}

}
