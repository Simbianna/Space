package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.*;

import java.util.List;

public interface ShipService {

    Ship getShipById(Long id);

    void deleteShipById(Long id);

    void editShip(Ship ship);

    void createShip(Ship ship);

    List<Ship> getUnsortedShipList(String name, String planet, ShipType shipType, Long after, Long before,
                                   Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                   Integer maxCrewSize, Double minRating, Double maxRating);

    Integer getShipsCount(String name, String planet, ShipType shipType, Long after, Long before,
                          Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                          Integer maxCrewSize, Double minRating, Double maxRating);

    void sortShipList(List<Ship> ships, ShipOrder order);

    List<Ship> getPagedShipList(List<Ship> ships, Integer pageNumber, Integer pageSize);

    Long stringToLong(String id);

    Boolean areFieldsInValid(Ship ship);

    HttpStatusAndIdStorage getStorage(String id);

    Double calculateShipRating(Ship ship);

    void setShipParameters(Ship original, Ship edited);

    //  Boolean isShipPresented(Long id);

}
