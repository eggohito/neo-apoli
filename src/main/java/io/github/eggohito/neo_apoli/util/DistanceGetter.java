package io.github.eggohito.neo_apoli.util;

@FunctionalInterface
public interface DistanceGetter {

    double getDistance(double x, double y, double z);

}
