package io.github.eggohito.neo_apoli.power.custom;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.event.PowerParsingEvents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Getter
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
		.and(SubPowersCodec.INSTANCE.forGetter(MultiplePower::getSubPowers))
		.apply(instance, MultiplePower::new));

	public static final PacketCodec<RegistryByteBuf, MultiplePower> PACKET_CODEC = createCommonPacketCodec(
		(buf, multiplePower) ->
			SubPowersPacketCodec.INSTANCE.encode(buf, multiplePower.getSubPowers()),
		(buf, properties) -> new MultiplePower(properties,
			SubPowersPacketCodec.INSTANCE.decode(buf)
		)
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
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
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

	public static final class Impl extends Power.Impl<MultiplePower> {

		private Impl(@NotNull Entity holder, @NotNull MultiplePower power) {
			super(holder, power);
		}

	}

	public static final class SubPowersCodec extends MapCodec<ImmutableMap<PowerReference.SubPower, Power>> {

		public static final SubPowersCodec INSTANCE = new SubPowersCodec();

		private SubPowersCodec() {

		}

		@Override
		public <I> Stream<I> keys(DynamicOps<I> ops) {
			return Stream.empty();
		}

		@Override
		public <I> DataResult<ImmutableMap<PowerReference.SubPower, Power>> decode(DynamicOps<I> ops, MapLike<I> mapInput) {

			Object2ObjectMap<PowerReference.SubPower, Power> succeeded = new Object2ObjectOpenHashMap<>();
			DataResult<Unit> result = mapInput.entries().reduce(
				DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
				(r, pair) -> {

					DataResult<String> keyResult = Codec.STRING.parse(ops, pair.getFirst());
					if (keyResult.mapOrElse(MultiplePower::isKeyIgnored, error -> false)) {
						return r;
					}

					DataResult<MapLike<I>> subMapResult = ops.getMap(pair.getSecond());
					if (subMapResult.isError()) {
						return r.apply2stable((unit, obj) -> unit, subMapResult);
					}

					MapLike<I> subMap = subMapResult.getOrThrow();
					DataResult<PowerReference.SubPower> subReferenceResult = keyResult.flatMap(PowerReference::ofValidated)
						.flatMap(reference -> reference instanceof PowerReference.SubPower subReference
							? DataResult.success(subReference)
							: DataResult.error(() -> "A sub-power must have a sub-power reference!"));

					if (subReferenceResult.isSuccess()) {

						PowerReference.SubPower subReference = subReferenceResult.getOrThrow();
						DataResult<PowerType<?>> typeResult = PowerTypes.CODEC.fieldOf(Power.TYPE_KEY).decode(ops, subMap)
							.flatMap(type -> Objects.equals(type, PowerTypes.MULTIPLE)
								? DataResult.error(() -> subReference.asDisplayString() + " uses the \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, type) + "\" power type, which isn't allowed!")
								: DataResult.success(type));

						if (typeResult.isSuccess()) {

							PowerType<?> type = typeResult.getOrThrow();
							DataResult<Power> subPowerResult = Power.BASE_MAP_CODEC.decode(ops, subMap)
								.flatMap(subPower -> PowerParsingEvents.DECODING.invoker().decode(subReference, type, ops, subMap)
									.map(unit -> subPower));

							if (subPowerResult.isSuccess()) {

								if (succeeded.putIfAbsent(subReference, subPowerResult.getOrThrow()) != null) {
									return r.apply2stable((u, o) -> u, DataResult.error(() -> "Duplicate entry for key: \"" + subReference.name() + "\""));
								}

							}

							return r.apply2stable((u, o) -> u, subPowerResult);

						}

						else {
							return r.apply2stable((u, o) -> u, typeResult);
						}

					}

					return r.apply2stable((u, o) -> u, subReferenceResult);

				},
				(r1, r2) ->
					r1.apply2stable((u1, u2) -> u1, r2)
			);

			ImmutableMap<PowerReference.SubPower, Power> elements = ImmutableMap.copyOf(succeeded);
			return result.map(u -> elements).setPartial(elements);

		}

		@Override
		public <I> RecordBuilder<I> encode(ImmutableMap<PowerReference.SubPower, Power> mapInput, DynamicOps<I> ops, RecordBuilder<I> prefix) {

			mapInput.forEach((subReference, subPower) -> {

				RecordBuilder<I> powerBuilder = Power.BASE_MAP_CODEC.encode(subPower, ops, ops.mapBuilder());
				PowerParsingEvents.ENCODING.invoker().encode(subReference, subPower, ops, powerBuilder);

				prefix.add(subReference.toString(), powerBuilder.build(ops.empty()));

			});

			return prefix;

		}

	}

	public static final class SubPowersPacketCodec implements PacketCodec<RegistryByteBuf, ImmutableMap<PowerReference.SubPower, Power>> {

		public static final SubPowersPacketCodec INSTANCE = new SubPowersPacketCodec();

		private SubPowersPacketCodec() {

		}

		@Override
		public ImmutableMap<PowerReference.SubPower, Power> decode(RegistryByteBuf buf) {

			ImmutableMap.Builder<PowerReference.SubPower, Power> subPowersBuilder = ImmutableMap.builder();
			int size = buf.readVarInt();

			for (int i = 0; i < size; i++) {

				PowerReference reference = PowerReference.PACKET_CODEC.decode(buf);
				Power subPower = Power.BASE_PACKET_CODEC.decode(buf);

				if (reference instanceof PowerReference.SubPower subReference) {
					subPowersBuilder.put(subReference, subPower);
				}

				else {
					throw new IllegalArgumentException("Expected a sub-power reference, but got " + reference + "!");
				}

			}

			return subPowersBuilder.build();

		}

		@Override
		public void encode(RegistryByteBuf buf, ImmutableMap<PowerReference.SubPower, Power> map) {

			buf.writeVarInt(map.size());

			map.forEach((subReference, subPower) -> {
				PowerReference.PACKET_CODEC.encode(buf, subReference);
				Power.BASE_PACKET_CODEC.encode(buf, subPower);
			});

		}

	}

}
