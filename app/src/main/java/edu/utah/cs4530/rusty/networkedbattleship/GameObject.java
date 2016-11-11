package edu.utah.cs4530.rusty.networkedbattleship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Rusty on 10/13/2016.
 */
public class GameObject implements Serializable {
    public static final String CARRIER = "carrier";
    public static final String BATTLESHIP = "battleship";
    public static final String CRUISER = "cruiser";
    public static final String SUBMARINE = "submarine";
    public static final String DESTROYER = "destroyer";
    public static final int HIT = 1;
    public static final int MISS = 0;
    public static final int DISALLOW = -1;
    public static final int SHIP_LOCATION = 2;


    Set<Integer> allShips = new HashSet<>();

    Set<Integer> _player1Ships;
    Set<Integer> _player2Ships;

    Set<Integer> _player1Guesses = new HashSet<>();
    Set<Integer> _player2Guesses = new HashSet<>();

    Map<Integer, Integer> _player1State;
    Map<Integer, Integer> _player2State;
    int player1HitCount = 0;
    int player2HitCount = 0;
    Boolean _player1Wins = false;
    Boolean _player2Wins = false;

    int _currentPlayer;

    Random random = new Random();

    /**
     * default constructor. Adds 5 ships to each player's ship map
     */
    GameObject() {
        _currentPlayer = 1;
        //sets the state to be neutral - no misses or hits.
        _player1State = new HashMap<>();
        _player2State = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            _player1State.put(i, 3);
            _player2State.put(i, 3);
        }

        for (int player = 1; player <= 2; player++) {
            placeShip(player, 5, CARRIER); //carrier
            placeShip(player, 4, BATTLESHIP); //battleship
            placeShip(player, 3, CRUISER); //cruiser
            placeShip(player, 3, SUBMARINE); //submarine
            placeShip(player, 2, DESTROYER); //destroyer
            if (player == 1) {
                _player1Ships = new HashSet<>(allShips);
                for (Integer shipSpace : _player1Ships) {
                    _player1State.put(shipSpace, SHIP_LOCATION);
                }
            }
            else {
                _player2Ships = new HashSet<>(allShips);
                for (Integer shipSpace : _player2Ships) {
                    _player2State.put(shipSpace, SHIP_LOCATION);
                }
            }
            allShips.clear();
        }

    }

    private void placeShip(int player, int shipSize, String shipType) {
        //make the horizontal vs vertical random
        //start over:
        Boolean vertical = random.nextBoolean();
        int startingPosition = getStartingPosition(shipSize, vertical);
        Set<Integer> tempSet = new HashSet<>();
        int currentShipPos = startingPosition;

        //this should check to make sure none of ships are overlapping
        boolean isCollision = checkShipCollisions(shipSize, shipType, currentShipPos, vertical, player);

        if (!isCollision) {
            for (int i = 0; i < shipSize; i++) {
                if (vertical) {
                    if (!allShips.contains(currentShipPos))
                        tempSet.add(currentShipPos);
                    allShips.add(currentShipPos);
                    currentShipPos = currentShipPos - 10;
                } else {
                    if (!allShips.contains(currentShipPos)) {
                        tempSet.add(currentShipPos);
                    }
                    allShips.add(currentShipPos);
                    currentShipPos = currentShipPos - 1;
                }
            }
        }
    }

    private boolean checkShipCollisions(int shipSize, String shipType, int nextShipPos, Boolean vertical, int player) {
        int checkPositionTemp = nextShipPos;
            for (int j = 0; j < shipSize; j++) {
                if (allShips.contains(checkPositionTemp)) {
                    //just start the method over which will get a new starting position which will eventually work right?
                    placeShip(player, shipSize, shipType);
                    return true;
                }
                if (vertical) {
                    checkPositionTemp = checkPositionTemp - 10;
                }
                else {
                    checkPositionTemp = checkPositionTemp - 1;
                }
            }
        return false;
    }

    private int getStartingPosition(int shipSize, Boolean vertical) {
        int sp;
        if (vertical) {
            sp = ThreadLocalRandom.current().nextInt((shipSize - 1) * 10, 100);
        }
        else {
            do {
                sp = random.nextInt(100);
            } while (sp % 10 < shipSize - 1);
        }
        return sp;
    }

    public Set<Integer> getPlayer1Ships() {
        return _player1Ships;
    }

    public Set<Integer> getPlayer2Ships() {
        return _player2Ships;
    }

    public Set<Integer> getPlayer1Guesses() {
        return _player1Guesses;
    }

    public Set<Integer> getPlayer2Guesses() {
        return _player2Guesses;
    }

    public int player1LaunchesMissile(int index) {
        int hitCode;
        if (_player1Guesses.contains(index)) {
            //TODO: send a notification that they can't pick this spot or something like that
            hitCode = DISALLOW;
        }
        else if (_player2Ships.contains(index)) {
            //HIT! return red color
            _player1Guesses.add(index);
            _currentPlayer = 2;
            hitCode = HIT;
            _player2State.put(index, HIT);
            player1HitCount++;
            if (player1HitCount == 17) {
                _player1Wins = true;
            }
        }
        else {
            //MISS! return white color
            _player1Guesses.add(index);
            _currentPlayer = 2;
            hitCode = MISS;
            _player2State.put(index, 0);
        }
        return hitCode;
    }

    public int player2LaunchesMissile(int index) {
        int hitCode;
        if (_player2Guesses.contains(index)) {
            //TODO: send a notification that they can't pick this spot or something like that
            hitCode = DISALLOW;
        }
        else if (_player1Ships.contains(index)) {
            //HIT! return red color
            _player2Guesses.add(index);
            _currentPlayer = 1;
            hitCode = HIT;
            _player1State.put(index, 1);
            player2HitCount++;
            if (player2HitCount == 17) {
                _player2Wins = true;
            }
        }
        else {
            //MISS! return white color
            _player2Guesses.add(index);
            _currentPlayer = 1;
            hitCode = MISS;
            _player1State.put(index, 0);
        }
        return hitCode;
    }
}
