package dev.implario.bukkit.world;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public interface V3 {

    static V3 of(double x, double y, double z) {
        return new Impl(x, y, z);
    }

    double getX();

    double getY();

    double getZ();

    default V3 rotate(double degrees, V3 axis) {
        return multiply(axis.toRotationMatrix(degrees));
    }

    default double[][] toRotationMatrix(double degrees) {
        degrees = Math.toRadians(degrees);
        double cos = cos(degrees);
        double sin = sin(degrees);
        double nCos = 1 - cos;
        double x = getX();
        double y = getY();
        double z = getZ();

        return new double[][] {
                {cos + nCos * x * x, nCos * x * y - sin * z, nCos * x * z + sin * y},
                {nCos * y * x + sin * z, cos + nCos * y * y, nCos * y * z - sin * x},
                {nCos * z * x - sin * y, nCos * z * y + sin * x, cos + nCos * z * z}
        };
    }

    default V3 multiply(double[][] matrix) {
        double[] newVector = new double[3];
        double[] data = this.toArray();
        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                newVector[j] += matrix[j][i] * data[i];
        return V3.of(newVector[0], newVector[1], newVector[2]);
    }

    default double[] toArray() {
        return new double[] {getX(), getY(), getZ()};
    }

    default V3 plus(V3 that) {
        return new Impl(
                this.getX() + that.getX(),
                this.getY() + that.getY(),
                this.getZ() + that.getZ()
        );
    }

    default V3 multiply(double number) {
        return new Impl(
                this.getX() * number,
                this.getY() * number,
                this.getZ() * number
        );
    }

    default V3 withX(double x) {
        return new Impl(x, getY(), getZ());
    }

    default V3 withY(double y) {
        return new Impl(getX(), y, getZ());
    }

    default V3 withZ(double z) {
        return new Impl(getX(), getY(), z);
    }

    default V3 negative() {
        return new Impl(-getX(), -getY(), -getZ());
    }

    default double getLength() {
        double x = this.getX(), y = this.getY(), z = this.getZ();
        return Math.sqrt(x * x + y * y + z * z);
    }

    default V3 normalize() {
        double length = this.getLength();
        return new Impl(this.getX() / length, this.getY() / length, this.getZ() / length);
    }

    default V3 denormalize(double length) {
        return new Impl(this.getX() * length, this.getY() * length, this.getZ() * length);
    }

    default V3 bitwiseNegate() {
        return normalize();
    }

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    class Impl implements V3 {

        public final double x, y, z;

        @Override
        public String toString() {
            return "[" + x + ", " + y + ", " + z + "]";
        }

    }

    default V3 normalY() {
        double normalY = Math.max(0.0000000001, Math.sqrt(1 - this.getY() * this.getY()));
        if (normalY == 0) return Axis.PLUS_Y;
        double convert = -this.getY() / normalY;
        return V3.of(getX() * convert, normalY, getZ() * convert);
    }

    default V3[] rayTriangle(double angle, int rayAmount) {
        V3 normal = this.normalY();
        V3 currentVector = this.rotate(-angle / 2, normal);
        V3[] rays = new V3[rayAmount];
        double step = angle / rayAmount;

        for (int i = 0; i < rayAmount; i++) {
            rays[i] = currentVector;
            currentVector = currentVector.rotate(step, normal);
        }

        return rays;
    }

    default V3[] rayCone(double angle, int rayAmount) {

        V3 normal = this.normalY();
        V3 currentVector = this.rotate(angle / 2, normal);
        double step = 360.0 / rayAmount;
        V3[] rays = new V3[rayAmount];

        for (int i = 0; i < rayAmount; i++) {
            rays[i] = currentVector;
            currentVector = currentVector.rotate(step, this);
        }

        return rays;
    }

}
