package com.nttdata.enums;

public enum ProfileType {
    LOW,
    MEDIUM,
    HIGH;

    public static ProfileType fromString(String profile) {
        try {
            return ProfileType.valueOf(profile.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid profile: " + profile);
        }
    }
}
