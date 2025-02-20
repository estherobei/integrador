package com.example.integrador

import java.util.*
import kotlin.math.ceil

const val MINUTES_IN_MILLISECONDS: Int = 60000

data class ParkingSpace(
    val vehicle: Vehicle,
    val parkedTime: Long =
        (Calendar.getInstance().timeInMillis - vehicle.checkInTime.timeInMillis) / MINUTES_IN_MILLISECONDS,
    val parking: Parking
) {
    fun checkOutVehicle(
        //show the fee of the vehicle at checkout
        plate: String = vehicle.plate,
        onSuccess: (amount: Int) -> Unit,
        onError: () -> Unit)
    {
        if (parking.isInParking(plate)) {
            val vehicle = parking.findVehicle(plate)!!
            val fee = calculateFee(vehicle.type, parkedTime, vehicle.discountCard != null)
            onSuccess(fee)
            parking.addHistory(fee)
            parking.deleteVehicle(plate)

        } else {
            onError()
        }
    }

    private fun calculateFee(
        //calculate the money to pay and if have a discount card
        vehicleType: VehicleType,
        parkedTime: Long,
        hasDiscountCard: Boolean): Int
    {
        val overtimeFraction = 15F
        val fractionatedTime: Int = ceil(parkedTime / overtimeFraction).toInt()
        val extraCost = 5
        val twoHours = 8
        val fee: Int =
            if (fractionatedTime <= twoHours) vehicleType.value
            else vehicleType.value + (extraCost * (fractionatedTime - twoHours))

        return if (hasDiscountCard) (fee * 0.85).toInt()
        else fee
    }
}

