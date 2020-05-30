package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    /** test method  for the processIncomingVehicle()
     * that checks if the ticked is saved in the Database
     * and if the availability of parking slots has been updated
     *
     * @throws Exception
     */

    @Test
    public void testParkingACar() throws Exception {
        //Check the number of available spots before processIncomingVehicle();
        int beforeParkingSpots = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();

        int afterParkingSpots = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        String vegNumber = inputReaderUtil.readVehicleRegistrationNumber();


        //Check if the ticket has been saved
        assertEquals(vegNumber, ticketDAO.getTicket(vegNumber).getVehicleRegNumber());
        //Check if DB is updated with availability
        assertNotEquals(beforeParkingSpots,afterParkingSpots);
    }

    /**Test that checks if the fare is generated
     * and the out time are populated in the database
     *
     *
     * @throws Exception
     */
    @Test
    public void testParkingLotExit() throws Exception {
        String vehNumber = inputReaderUtil.readVehicleRegistrationNumber();
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Add a delay of 2 seconds
        Thread.sleep(2000);

        parkingService.processExitingVehicle();

        //check generated fare is not 0
        assertNotEquals(0, ticketDAO.getTicket(vehNumber).getPrice());
        //check out time is not null
        assertNotNull(ticketDAO.getTicket(vehNumber).getOutTime());
    }

}
