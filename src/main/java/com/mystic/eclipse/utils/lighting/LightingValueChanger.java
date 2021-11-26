package com.mystic.eclipse.utils.lighting;

public class LightingValueChanger {
    public static int getLightLevel(int x) {
        if (x < - 16) {
            return 0;
        } else if (x <  -15) {
            return 3;
        } else if (x < -14) {
            return 6;
        } else if (x < 14) {
            return 7;
        } else if (x < 15) {
            return 9;
        } else if (x < 16) {
            return 12;
        } else {
            return 15;
        }
    }
}
