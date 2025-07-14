package io.github.eggohito.neo_apoli.util;

import net.minecraft.util.math.ColorHelper;

public record Color(float alpha, float red, float green, float blue) {

	public static final Color DEFAULT = new Color(1.0F, 1.0F, 1.0F, 1.0F);

	//	TODO: Maybe add an argument that determines how the colors are mixed?
	public Color mix(Color other) {
		return new Color(
			alpha() * other.alpha(),
			red() * other.red(),
			green() * other.green(),
			blue() * other.blue()
		);
	}

	public int toArgb() {
		return ColorHelper.fromFloats(alpha(), red(), green(), blue());
	}

	public static Color fromArgb(int argb) {
		return new Color(
			ColorHelper.getAlphaFloat(argb),
			ColorHelper.getRedFloat(argb),
			ColorHelper.getGreenFloat(argb),
			ColorHelper.getBlueFloat(argb)
		);
	}

}
