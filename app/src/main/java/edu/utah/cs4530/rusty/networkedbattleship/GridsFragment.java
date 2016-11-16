package edu.utah.cs4530.rusty.networkedbattleship;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**Controller for Battleship
 *
 * Created by Rusty on 10/24/2016.
 */
public class GridsFragment extends Fragment implements GridLayoutCustom.OnMissileFiredListener,
        GameLobbyService.GameBoardsReceivedListener, GameLobbyService.MakeGuessResponseReceivedListener,
        GameLobbyService.GameTurnResponseReceivedListener{
    public static String GAME_OBJECT_LIST_FILENAME = "Game_object_list.dat";
    private String _currentGameId;
    private String _currentPlayerId;
    GridLayoutCustom _ownGrid;
    GridLayoutCustom _opponentGrid;

    GameLobbyService _gameLobbyService = null;

    public static GridsFragment newInstance() {
        //this stuff was included when I just tabbed to write newInstance()
//        Bundle args = new Bundle();
//
//        GalleryFragment fragment = new GalleryFragment();
//        fragment.setArguments(args);
//        return fragment;
        return new GridsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _gameLobbyService = GameLobbyService.getInstance();
        _gameLobbyService.setGameBoardsReceivedListener(this);
        _gameLobbyService.setMakeGuessResponseReceivedListener(this);
        _gameLobbyService.setGameTurnResponseReceivedListener(this);

        LinearLayout rootLayout = new LinearLayout(getActivity());
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        View leftSideView = new View(getActivity());
        leftSideView.setBackgroundColor(Color.BLACK);

        linearLayout.addView(leftSideView, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

        _ownGrid = new GridLayoutCustom(getActivity());
        _ownGrid.setEnabled(false);
        for (int i = 0; i < 100; i++) {
            GridSpaceView gridSpaceView = new GridSpaceView(getActivity());
            gridSpaceView.setEnabled(false);
            _ownGrid.addView(gridSpaceView);

        }
        _ownGrid.setOnMissileFiredListener(this);

        linearLayout.addView(_ownGrid, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 3));

        View rightSideView = new View(getActivity());
        rightSideView.setBackgroundColor(Color.BLACK);

        linearLayout.addView(rightSideView, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

        //opponent Grid starts
        _opponentGrid = new GridLayoutCustom(getActivity());
        _opponentGrid.setEnabled(true);
        for (int i = 0; i < 100; i++) {
            GridSpaceView gridSpaceView = new GridSpaceView(getActivity());
            _opponentGrid.addView(gridSpaceView);
        }
        _opponentGrid.setOnMissileFiredListener(this);

        rootLayout.addView(linearLayout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        rootLayout.addView(_opponentGrid, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 2));

        //TODO: updateViews();

        return rootLayout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameList.setInstance(loadFromFile(GAME_OBJECT_LIST_FILENAME));
        if (GameList.getInstance().getCount() == 0) {
//            GameList.getInstance().createNewGame(); //????
        }

    }

    @Override
    public void onMissileFired(int missileIndex) {
        Guess guess = new Guess();
        guess.gameId = _currentGameId;
        guess.playerId = _currentPlayerId;
        guess.xPos = missileIndex % 10;
        guess.yPos = missileIndex / 10;
        GameLobbyService.getInstance().makeGuess(guess);
    }

    /**
     * This method will save the GameList to disk using a Serializable fileStream
     */
    public void saveToFile() {
        try {
            FileOutputStream fos = getActivity().openFileOutput(GAME_OBJECT_LIST_FILENAME, Activity.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(GameList.getInstance());
            oos.close();
            Log.i("saving", "saving to file");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method extracts a Gallery from a file saved to disk.
     * @param gameObjectListFileName is the file name where the Gallery is saved.
     * @return the Gallery that was previously saved to disk.
     */
    private GameList loadFromFile(String gameObjectListFileName) {
        GameList gamelist = null;
        try {
            FileInputStream fis = getActivity().openFileInput(gameObjectListFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            gamelist = (GameList) ois.readObject();
            ois.close();
            Log.i("loading", "loading from file");
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return gamelist;
    }


    private void updateViews(Map<Integer, Integer> player1State, Map<Integer, Integer> player2State) {

        for (Integer hitMissIndex : player1State.keySet()) {
            if (player1State.get(hitMissIndex) == 0) {
                _ownGrid.setSpaceColor(hitMissIndex, Color.WHITE);
            } else if (player1State.get(hitMissIndex) == 1) {
                _ownGrid.setSpaceColor(hitMissIndex, Color.RED);
            } else if (player1State.get(hitMissIndex) == 2) {
                _ownGrid.setSpaceColor(hitMissIndex, Color.GRAY);
            } else {
                _ownGrid.setSpaceColor(hitMissIndex, Color.BLUE);
            }
        }
        for (Integer hitMissIndex : player2State.keySet()) {
            if (player2State.get(hitMissIndex) == 0) {
                _opponentGrid.setSpaceColor(hitMissIndex, Color.WHITE);
            } else if (player2State.get(hitMissIndex) == 1) {
                _opponentGrid.setSpaceColor(hitMissIndex, Color.RED);
            } else if (player2State.get(hitMissIndex) == 2) {
                _opponentGrid.setSpaceColor(hitMissIndex, Color.GRAY);
            } else {
                _opponentGrid.setSpaceColor(hitMissIndex, Color.BLUE);
            }
        }
    }

    public void setCurrentGame(String gameId, String playerId) {
        _currentGameId = gameId;
        _currentPlayerId = playerId;
    }

//    public void addNewGame() {
//        //GameObjectList.getInstance().createNewGame();
//        GameLobbyService.getInstance().createNewGame("WinnerTakesAll", "Luigi");
//        _currentGameIndex = GameObjectList.getInstance().getGameObjectsCount() - 1;
//        updateViews();
//    }

    @Override
    public void onGameBoardsReceivedListener(boolean success, GameBoards gameBoards) {

        saveToFile();
        List<Position> playerBoardList = gameBoards.playerBoardList;
        List<Position> opponentBoardList = gameBoards.opponentBoardList;
        Map<Integer, Integer> playerBoardState = new HashMap<>();
        Map<Integer, Integer> opponentBoardState = new HashMap<>();
        setCurrentGame(gameBoards.gameId, gameBoards.playerId);
        for (int i = 0; i < playerBoardList.size(); i++) {
            int hitCode;
            if (playerBoardList.get(i).status.equals("HIT")) {
                hitCode = 1;
            }
            else if (playerBoardList.get(i).status.equals("MISS")) {
                hitCode = 0;
            }
            else if (playerBoardList.get(i).status.equals("SHIP")) {
                hitCode = 2;
            }
            else { //status must be "NONE" hopefully....
                hitCode = -1;
            }
            playerBoardState.put(i, hitCode);
        }
        for (int i = 0; i < opponentBoardList.size(); i++) {
            int hitCode;
            if (opponentBoardList.get(i).status.equals("HIT")) {
                hitCode = 1;
            }
            else if (opponentBoardList.get(i).status.equals("MISS")) {
                hitCode = 0;
            }
            else if (opponentBoardList.get(i).status.equals("SHIP")) {
                hitCode = 2;
            }
            else { //status must be "NONE" hopefully....
                hitCode = -1;
            }
            opponentBoardState.put(i, hitCode);
        }

        updateViews(playerBoardState, opponentBoardState);
    }

    @Override
    public void onMakeGuessResponseReceived(boolean success) {
        GameLobbyService.getInstance().getPlayerBoards(_currentGameId, _currentPlayerId);
    }


    @Override
    public void onGameTurnResponseReceivedListener(boolean success, IsYourTurn isYourTurn) {
        if (!isYourTurn.isYourTurn) {
            Intent startWaitScreen = new Intent();
            startWaitScreen.putExtra("gameId", isYourTurn.gameId);
            startWaitScreen.putExtra("playerId", isYourTurn.playerId);
            startWaitScreen.setClass(getActivity(), WaitScreenActivity.class);
            startActivity(startWaitScreen);
        }
    }
}
