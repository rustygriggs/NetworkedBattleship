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
import java.util.List;
import java.util.Map;

/**
 * Created by Rusty on 10/24/2016.
 */
public class GridsFragment extends Fragment implements GridLayoutCustom.OnMissileFiredListener {
    public static String GAME_OBJECT_LIST_FILENAME = "Game_object_list.dat";
    private int _currentGameIndex = 0;
    GridLayoutCustom _ownGrid;
    GridLayoutCustom _opponentGrid;

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

        updateViews();
        return rootLayout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameObjectList.setInstance(loadFromFile(GAME_OBJECT_LIST_FILENAME));
        if (GameObjectList.getInstance().getGameObjectsCount() == 0) {
            GameObjectList.getInstance().createNewGame();
        }
    }

    @Override
    public void onMissileFired(int missileIndex) {
        //start PlayerSwitchActivity
        Intent showPlayerSwitch = new Intent();
        showPlayerSwitch.setClass(getActivity(), PlayerSwitchActivity.class);
        showPlayerSwitch.putExtra("player_1_wins", GameObjectList.getInstance().readGame(_currentGameIndex)._player1Wins);
        showPlayerSwitch.putExtra("player_2_wins", GameObjectList.getInstance().readGame(_currentGameIndex)._player2Wins);
        showPlayerSwitch.putExtra("missileIndex", missileIndex);
        startActivityForResult(showPlayerSwitch, 0);
    }

    /**
     * This method will save the Gallery to disk using a Serializable fileStream
     */
    void saveToFile() {
        try {
            FileOutputStream fos = getActivity().openFileOutput(GAME_OBJECT_LIST_FILENAME, Activity.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(GameObjectList.getInstance());
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
    private GameObjectList loadFromFile(String gameObjectListFileName) {
        GameObjectList gameObjectList = null;
        try {
            FileInputStream fis = getActivity().openFileInput(gameObjectListFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            gameObjectList = (GameObjectList) ois.readObject();
            ois.close();
            Log.i("loading", "loading from file");
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return gameObjectList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                int missileIndex = data.getIntExtra("missileIndex", 0);
                int hitCode = GameObjectList.getInstance().updateGame(_currentGameIndex, missileIndex);

                updateViews();
            }
        }
    }

    private void updateViews() {
        Map<Integer, Integer> player1State = GameObjectList.getInstance().readGame(_currentGameIndex)._player1State;
        Map<Integer, Integer> player2State = GameObjectList.getInstance().readGame(_currentGameIndex)._player2State;

        if (GameObjectList.getInstance().readGame(_currentGameIndex)._currentPlayer == 1) {
            for (Integer hitMissIndex : player2State.keySet()) {
                if (player2State.get(hitMissIndex) == 0) {
                    _opponentGrid.setSpaceColor(hitMissIndex, Color.WHITE);
                } else if (player2State.get(hitMissIndex) == 1) {
                    _opponentGrid.setSpaceColor(hitMissIndex, Color.RED);
                } else {
                    _opponentGrid.setSpaceColor(hitMissIndex, Color.BLUE);
                }
            }
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
        } else {
            for (Integer hitMissIndex : player1State.keySet()) {
                if (player1State.get(hitMissIndex) == 0) {
                    _opponentGrid.setSpaceColor(hitMissIndex, Color.WHITE);
                } else if (player1State.get(hitMissIndex) == 1) {
                    _opponentGrid.setSpaceColor(hitMissIndex, Color.RED);
                } else {
                    _opponentGrid.setSpaceColor(hitMissIndex, Color.BLUE);
                }
            }
            for (Integer hitMissIndex : player2State.keySet()) {
                if (player2State.get(hitMissIndex) == 0) {
                    _ownGrid.setSpaceColor(hitMissIndex, Color.WHITE);
                } else if (player2State.get(hitMissIndex) == 1) {
                    _ownGrid.setSpaceColor(hitMissIndex, Color.RED);
                } else if (player2State.get(hitMissIndex) == 2) {
                    _ownGrid.setSpaceColor(hitMissIndex, Color.GRAY);
                } else {
                    _ownGrid.setSpaceColor(hitMissIndex, Color.BLUE);
                }
            }
        }
        saveToFile();
    }

    public void setCurrentGame(int gameId) {
        _currentGameIndex = gameId;
        updateViews();
    }

    public void addNewGame() {
        GameObjectList.getInstance().createNewGame();
        _currentGameIndex = GameObjectList.getInstance().getGameObjectsCount() - 1;
        updateViews();
    }
}
