package io.github.eggohito.neo_apoli.power.internal;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.event.PowerParsingEvents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.context.PowerContextTypes;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class MultiplePower extends Power {

	public static final Identifier ID = NeoApoli.id("multiple");

	//	TODO: This set of filters should be controllable via config
	public static final Set<Pattern> SUB_POWER_KEY_FILTERS = Util.make(new ObjectOpenHashSet<>(), filters -> {

		Properties.CODEC.keys(JavaOps.INSTANCE)
			.map(Object::toString)
			.distinct()
			.map(Pattern::compile)
			.forEach(filters::add);

		filters.add(Pattern.compile(Power.TYPE_KEY));
		filters.add(Pattern.compile("^\\$"));

	});

	public static final MapCodec<MultiplePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance)
		.and(MapCodec.assumeMapUnsafe(new SubPowersCodec()).forGetter(MultiplePower::getSubPowers))
		.apply(instance, MultiplePower::new));

	public static final PacketCodec<RegistryByteBuf, MultiplePower> PACKET_CODEC = createCommonPacketCodec(
		(buf, multiplePower) -> {

			ImmutableMap<PowerReference.SubPower, Power> subPowers = multiplePower.getSubPowers();
			buf.writeVarInt(subPowers.size());

			subPowers.forEach((subReference, subPower) -> {
				PowerReference.PACKET_CODEC.encode(buf, subReference);
				Power.BASE_PACKET_CODEC.encode(buf, subPower);
			});

		},
		(buf, properties) -> {

			ImmutableMap.Builder<PowerReference.SubPower, Power> subPowersBuilder = ImmutableMap.builder();
			int size = buf.readVarInt();

			for (int i = 0; i < size; i++) {

				PowerReference reference = PowerReference.PACKET_CODEC.decode(buf);
				Power subPower = Power.BASE_PACKET_CODEC.decode(buf);

				if (reference instanceof PowerReference.SubPower subReference) {
					subPowersBuilder.put(subReference, subPower);
				}

				else {
					throw new IllegalStateException("Expected a sub-power, but received " + reference + "!");
				}

			}

			return new MultiplePower(properties, subPowersBuilder.build());

		}
	);

	private final ImmutableMap<PowerReference.SubPower, Power> subPowers;

	public MultiplePower(Properties properties, ImmutableMap<PowerReference.SubPower, Power> subPowers) {
		super(properties);
		this.subPowers = subPowers;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MULTIPLE;
	}

	@Override
	public ContextType getContextType() {
		return PowerContextTypes.GENERIC;
	}

	@Override
	public Impl<?> createImpl(Entity holder) {
		return new Impl<>(holder, this) {};
	}

	public ImmutableMap<PowerReference.SubPower, Power> getSubPowers() {
		return subPowers;
	}

	/**
	 * 	<p>Prepend the ID of the super-power to the sub-powers to prepare for decoding since the map codec for sub-powers can only parse a sub-power reference and a power key-value pair.</p>
	 */
	@ApiStatus.Internal
	public static void preProcessSubPowers(Identifier id, PowerManager.Entry entry, String directoryPath, RegistryOps<JsonElement> ops) {

		JsonObject powerJson = entry.element();
		DataResult<PowerType<?>> powerTypeResult = PowerTypes.CODEC.parse(ops, powerJson.get(TYPE_KEY));

		if (powerTypeResult.isSuccess() && Objects.equals(powerTypeResult.getOrThrow(), PowerTypes.MULTIPLE)) {

			powerJson.entrySet().removeIf(e -> !isKeyIgnored(e.getKey()) && !MiscUtil.isResourceConditionFulfilled(id, e.getValue(), directoryPath, ops));
			JsonObject copy = powerJson.deepCopy();

			powerJson.asMap().clear();
			copy.asMap().forEach((key, value) -> {

				String modifiedKey = isKeyIgnored(key)
					? key
					: id + Character.toString(PowerReference.SubPower.SEPARATOR) + key;

				powerJson.add(modifiedKey, value);

			});

		}

	}

	public static DataResult<String> validateSubPowerName(String name) {

		if (name.isEmpty()) {
			return DataResult.error(() -> "Empty sub-power names are not allowed!");
		}

		else if (Identifier.isPathValid(name)) {
			return DataResult.success(name);
		}

		else {
			DataResult<String> result = DataResult.error(() -> "Non [a-z0-9/._-] character in sub-power name: " + name);
			return result.setPartial(name);
		}

	}

	public static boolean isKeyIgnored(String key) {

		for (var filter : SUB_POWER_KEY_FILTERS) {

			if (filter.matcher(key).find()) {
				return true;
			}

		}

		return false;

	}

	private static class SubPowersCodec implements Codec<ImmutableMap<PowerReference.SubPower, Power>> {

		@Override
		public <T> DataResult<Pair<ImmutableMap<PowerReference.SubPower, Power>, T>> decode(DynamicOps<T> ops, T input) {
			return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(mapInput -> this.decodeMap(ops, mapInput)).map(map -> Pair.of(map, input));
		}

		@Override
		public <T> DataResult<T> encode(ImmutableMap<PowerReference.SubPower, Power> input, DynamicOps<T> ops, T prefix) {
			return this.encodeMap(input, ops, ops.mapBuilder()).build(prefix);
		}

		private <I> DataResult<ImmutableMap<PowerReference.SubPower, Power>> decodeMap(DynamicOps<I> ops, MapLike<I> mapInput) {

			Object2ObjectMap<PowerReference.SubPower, Power> succeeded = new Object2ObjectOpenHashMap<>();
			DataResult<Unit> result = mapInput.entries().reduce(
				DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
				(r, pair) -> {

					DataResult<String> keyResult = Codec.STRING.parse(ops, pair.getFirst());
					if (keyResult.mapOrElse(MultiplePower::isKeyIgnored, error -> false)) {
						return r;
					}

					DataResult<PowerType<?>> typeResult = PowerTypes.CODEC.fieldOf(Power.TYPE_KEY).decode(ops, mapInput);
					if (typeResult.isError()) {
						return r.apply2stable((unit, obj) -> unit, typeResult);
					}

					DataResult<PowerReference.SubPower> subReferenceResult = keyResult.flatMap(PowerReference::ofValidated).flatMap(
						reference -> reference instanceof PowerReference.SubPower subReference
							? DataResult.success(subReference)
							: DataResult.error(() -> "A sub-power must have a sub-power reference!")
					);

					if (subReferenceResult.isSuccess()) {

						PowerReference.SubPower subReference = subReferenceResult.getOrThrow();
						DataResult<Power> subPowerResult = Objects.requireNonNullElseGet(PowerParsingEvents.DECODING.invoker().decode(subReference, typeResult.getOrThrow(), ops, mapInput), () -> Power.BASE_CODEC.parse(ops, pair.getSecond()));

						if (subPowerResult.isSuccess()) {

							if (succeeded.putIfAbsent(subReference, subPowerResult.getOrThrow()) != null) {
								return r.apply2stable((u, o) -> u, DataResult.error(() -> "Duplicate entry for key: '" + subReference.name() + "'"));
							}

						}

						return r.apply2stable((u, o) -> u, subPowerResult);

					}

					return r.apply2stable((u, o) -> u, subReferenceResult);

				},
				(r1, r2) ->
					r1.apply2stable((u1, u2) -> u1, r2)
			);

			ImmutableMap<PowerReference.SubPower, Power> elements = ImmutableMap.copyOf(succeeded);
			return result.map(u -> elements).setPartial(elements);

		}

		private <I> RecordBuilder<I> encodeMap(ImmutableMap<PowerReference.SubPower, Power> mapInput, DynamicOps<I> ops, RecordBuilder<I> prefix) {

			mapInput.forEach((subReference, subPower) -> {

				RecordBuilder<I> powerBuilder = Power.BASE_MAP_CODEC.encode(subPower, ops, ops.mapBuilder());
				PowerParsingEvents.ENCODING.invoker().encode(subReference, subPower, ops, powerBuilder);

				prefix.add(subReference.toString(), powerBuilder.build(ops.empty()));

			});

			return prefix;

		}

	}

}
