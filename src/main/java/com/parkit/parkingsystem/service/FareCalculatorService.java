package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean halfHour) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        /**Changed getHour in getTime,
         * to have the time in milliseconds
         *
         * The duration is a double not an integer,
         * Time in milliseconds is divided by one hour
         *
         */
        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();
        double duration = (double) (outHour - inHour) / (1000 * 3600);

        /**Set a boolean parameter that represents the 30 min free time
         *
         * If the duration is less than 30 min, the duration is 0
         * else, 30 min is subtracted from the duration
         */

        if (halfHour){
            duration = (duration - 0.5 < 0) ? 0 : (duration - 0.5);}


        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}