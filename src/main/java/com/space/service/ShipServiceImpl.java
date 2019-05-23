package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.*;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship getShipById(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteShipById(Long id) {
        shipRepository.delete(getShipById(id));
    }

    @Override
    public void editShip(Ship ship) {
        shipRepository.saveAndFlush(ship);
    }

    @Override
    public void createShip(Ship ship) {
        if (ship.getUsed() == null)
            ship.setUsed(false);
        Double rating = calculateShipRating(ship);
        ship.setRating(rating);
        shipRepository.saveAndFlush(ship);
    }

    @Override
    public List<Ship> getUnsortedShipList(String name, String planet, ShipType shipType, Long after, Long before,
                                          Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                          Integer maxCrewSize, Double minRating, Double maxRating) {
        Date minDate = (after == null ? null : new Date(after));
        Date maxDate = (before == null ? null : new Date(before));
        List<Ship> unsortedShipList = new ArrayList<>();
        for (Ship ship : shipRepository.findAll()) {
            if (name != null && !ship.getName().contains(name)) continue;
            if (planet != null && !ship.getPlanet().contains(planet)) continue;
            if (shipType != null && ship.getShipType() != shipType) continue;
            if (minDate != null && ship.getProdDate().before(minDate)) continue;
            if (maxDate != null && ship.getProdDate().after(maxDate)) continue;
            if (isUsed != null && ship.getUsed().booleanValue() != isUsed.booleanValue()) continue;
            if (minSpeed != null && ship.getSpeed().compareTo(minSpeed) < 0) continue;
            if (maxSpeed != null && ship.getSpeed().compareTo(maxSpeed) > 0) continue;
            if (minCrewSize != null && ship.getCrewSize().compareTo(minCrewSize) < 0) continue;
            if (maxCrewSize != null && ship.getCrewSize().compareTo(maxCrewSize) > 0) continue;
            if (minRating != null && ship.getRating().compareTo(minRating) < 0) continue;
            if (maxRating != null && ship.getRating().compareTo(maxRating) > 0) continue;
            unsortedShipList.add(ship);
        }
        return unsortedShipList;
    }

    @Override
    public void sortShipList(List<Ship> ships, ShipOrder order) {
        ships.sort((o1, o2) -> {
            switch (order) {
                case ID:
                    return o1.getId().compareTo(o2.getId());
                case SPEED:
                    return o1.getSpeed().compareTo(o2.getSpeed());
                case DATE:
                    return o1.getProdDate().compareTo(o2.getProdDate());
                case RATING:
                    return o1.getRating().compareTo(o2.getRating());
                default:
                    return 0;
            }
        });
    }

    @Override
    public List<Ship> getPagedShipList(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        int fromIndex = pageNumber * pageSize;
        int toIndex = fromIndex + pageSize > ships.size() ? ships.size() : fromIndex + pageSize;
        return ships.subList(fromIndex, toIndex);
    }

    @Override
    public Integer getShipsCount(String name, String planet, ShipType shipType, Long after, Long before,
                                 Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                 Integer maxCrewSize, Double minRating, Double maxRating) {
        return getUnsortedShipList(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @Override
    public Long stringToLong(String id) {
        if (id == null || id.equals("")) return null;
        try {
            long value = Long.parseLong(id);
            if (value <= 0) return null;
            return value;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    public Boolean areFieldsInValid(Ship ship) {
        boolean isCalendarOk = false;
        if (ship.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ship.getProdDate());
            isCalendarOk = (calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019);
        }
        return (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50)
                || ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50)
                || ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
                || ship.getSpeed() != null && (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99)
                || isCalendarOk);
    }

    @Override
    public HttpStatusAndIdStorage getStorage(String id) {
        Long shipId = stringToLong(id);
        if (shipId == null) return new HttpStatusAndIdStorage(HttpStatus.BAD_REQUEST, null);
        return shipRepository.existsById(shipId) ? new HttpStatusAndIdStorage(HttpStatus.OK, shipId)
                : new HttpStatusAndIdStorage(HttpStatus.NOT_FOUND, null);
    }

    @Override
    public Double calculateShipRating(Ship ship) {
        double speed = ship.getSpeed();
        double ratio = ship.getUsed() ? 0.5 : 1;
        int currentYear = 3019;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int shipProdYear = calendar.get(Calendar.YEAR);
        BigDecimal rating =
                new BigDecimal(((80 * speed * ratio) / (currentYear - shipProdYear + 1)));
        rating = rating.setScale(2, RoundingMode.HALF_UP);
        return rating.doubleValue();
    }

    @Override
    public void setShipParameters(Ship original, Ship edited) {
        if (original.getName() != null) edited.setName(original.getName());
        if (original.getPlanet() != null) edited.setPlanet(original.getPlanet());
        if (original.getShipType() != null) edited.setShipType(original.getShipType());
        if (original.getProdDate() != null) edited.setProdDate(original.getProdDate());
        if (original.getSpeed() != null) edited.setSpeed(original.getSpeed());
        if (original.getUsed() != null) edited.setUsed(original.getUsed());
        if (original.getCrewSize() != null) edited.setCrewSize(original.getCrewSize());
        Double rating = calculateShipRating(edited);
        edited.setRating(rating);
    }

    /*    @Override
    public Boolean isShipPresented(Long id) {
        return shipRepository.existsById(id);
    }
    */

}
