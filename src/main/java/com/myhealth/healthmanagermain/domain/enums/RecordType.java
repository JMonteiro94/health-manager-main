package com.myhealth.healthmanagermain.domain.enums;

public enum RecordType {
  LOAD, VOLUME, DISTANCE, SPEED, REPS, SETS;

  public static RecordType of(int recordType) {
    if (recordType < 1 || recordType > 6) {
      throw new IllegalArgumentException("Invalid value for recordType: " + recordType);
    }
    return RecordType.values()[recordType - 1];
  }
}
