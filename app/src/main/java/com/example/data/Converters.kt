package com.example.data

import androidx.room.TypeConverter
import com.example.model.CoachingMethod
import com.example.model.CoachingType
import com.example.model.FeeType

class Converters {
  @TypeConverter
  fun fromCoachingType(type: CoachingType): String = type.name

  @TypeConverter
  fun toCoachingType(name: String): CoachingType = CoachingType.fromName(name)

  @TypeConverter
  fun fromFeeType(feeType: FeeType): String = feeType.name

  @TypeConverter
  fun toFeeType(name: String): FeeType = FeeType.fromName(name)

  @TypeConverter
  fun fromCoachingMethod(method: CoachingMethod): String = method.name

  @TypeConverter
  fun toCoachingMethod(name: String): CoachingMethod = CoachingMethod.fromName(name)
}
