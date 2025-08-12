package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

@EqualsAndHashCode(callSuper = false)
@Data
public final class Vec3dProvider extends ValueProvider<Vec3d> {

	public static final MapCodec<Vec3dProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("x").forGetter(Vec3dProvider::x),
		NumberProvider.CODEC.fieldOf("y").forGetter(Vec3dProvider::y),
		NumberProvider.CODEC.fieldOf("z").forGetter(Vec3dProvider::z)
	).apply(instance, Vec3dProvider::new));

	public static final PacketCodec<RegistryByteBuf, Vec3dProvider> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, Vec3dProvider::x,
		NumberProvider.PACKET_CODEC, Vec3dProvider::y,
		NumberProvider.PACKET_CODEC, Vec3dProvider::z,
		Vec3dProvider::new
	);

	public static final ValueProviderType<Vec3dProvider> TYPE = new ValueProviderType<>() {

		@Override
		public MapCodec<Vec3dProvider> mapCodec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, Vec3dProvider> packetCodec() {
			return PACKET_CODEC;
		}

	};

	private final NumberProvider x;
	private final NumberProvider y;
	private final NumberProvider z;

	@Override
	public ValueProviderType<?> getType() {
		return TYPE;
	}

	@Override
	public Vec3d next(Context context) {
		return provideValue("vec3d", context, ctx -> new Vec3d(x().nextDouble(ctx.makeChild(".x")), y().nextDouble(ctx.makeChild(".y")), z().nextDouble(ctx.makeChild(".z"))), () -> Vec3d.ZERO);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		x().validate(reporter.makeChild(".x"));
		y().validate(reporter.makeChild(".y"));
		z().validate(reporter.makeChild(".z"));

	}

	@Override
	public String asDisplayString() {
		return "Vec3d provider";
	}

}
