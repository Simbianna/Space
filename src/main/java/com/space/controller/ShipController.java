package com.space.controller;

import com.space.model.*;
import com.space.service.ShipService;
import com.space.service.HttpStatusAndIdStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class ShipController {

    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") String shipId) {
        HttpStatusAndIdStorage httpStatusAndIdStorage = shipService.getStorage(shipId);
        if (httpStatusAndIdStorage.getId() == null) return new ResponseEntity<>(httpStatusAndIdStorage.getStatus());
        return new ResponseEntity<>(shipService.getShipById(httpStatusAndIdStorage.getId()), httpStatusAndIdStorage.getStatus());
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> deleteShip(@PathVariable(value = "id") String shipId) {
        HttpStatusAndIdStorage httpStatusAndIdStorage = shipService.getStorage(shipId);
        if (httpStatusAndIdStorage.getId() == null) return new ResponseEntity<>(httpStatusAndIdStorage.getStatus());
        shipService.deleteShipById(httpStatusAndIdStorage.getId());
        return new ResponseEntity<>(shipService.getShipById(httpStatusAndIdStorage.getId()), httpStatusAndIdStorage.getStatus());
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(@PathVariable(value = "id") String shipId, @RequestBody Ship ship) {
        HttpStatusAndIdStorage status = shipService.getStorage(shipId);
        Long id = status.getId();
        if (id == null) return new ResponseEntity<>(status.getStatus());
        if (shipService.areFieldsInValid(ship)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Ship editedShip = shipService.getShipById(id);
        shipService.setShipParameters(ship, editedShip);
        shipService.editShip(editedShip);
        return new ResponseEntity<>(editedShip, HttpStatus.OK);
    }

    @RequestMapping(value = "/ships", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null
                || shipService.areFieldsInValid(ship))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        shipService.createShip(ship);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping(value = "/ships", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getAllShips(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false) ShipOrder shipOrder,
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize) {

        List<Ship> ships = shipService.getUnsortedShipList(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        if (shipOrder != null) shipService.sortShipList(ships, shipOrder);
        return new ResponseEntity<>(shipService.getPagedShipList(ships, pageNumber, pageSize), HttpStatus.OK);
    }

    @RequestMapping(value = "/ships/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Integer> getShipsCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating) {

        return new ResponseEntity<>(shipService.getShipsCount(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating), HttpStatus.OK);
    }
}
