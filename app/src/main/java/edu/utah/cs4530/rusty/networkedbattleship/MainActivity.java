package edu.utah.cs4530.rusty.networkedbattleship;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements GameListFragment.OnGameChosenListener{

    public static final String OPPONENT_GRID_FRAGMENT_TAG = "GridsFragment";
    public static final String GAME_LIST_FRAGMENT_TAG = "GameListFragment";
    private GameListFragment _gameListFragment;
    private GridsFragment _gridsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout rootLayout = new LinearLayout(this);
        setContentView(rootLayout);

        LinearLayout gridsLayout = new LinearLayout(this);
        gridsLayout.setOrientation(LinearLayout.VERTICAL);

        FrameLayout opponentGrid = new FrameLayout(this);
        opponentGrid.setId(11);
        gridsLayout.addView(opponentGrid, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 2));

        FrameLayout gameListView = new FrameLayout(this);
        gameListView.setId(12);

        rootLayout.addView(gameListView, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

        rootLayout.addView(gridsLayout, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 3));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        _gridsFragment = (GridsFragment)
                getSupportFragmentManager().findFragmentByTag(OPPONENT_GRID_FRAGMENT_TAG);
        _gameListFragment = (GameListFragment)
                getSupportFragmentManager().findFragmentByTag(GAME_LIST_FRAGMENT_TAG);

        if (_gridsFragment == null) {
            _gridsFragment = GridsFragment.newInstance();
            _gameListFragment = GameListFragment.newInstance();
            transaction.add(opponentGrid.getId(), _gridsFragment, OPPONENT_GRID_FRAGMENT_TAG);
            transaction.add(gameListView.getId(), _gameListFragment, GAME_LIST_FRAGMENT_TAG);
        }
        else {
            transaction.replace(opponentGrid.getId(), _gridsFragment);
            transaction.replace(gameListView.getId(), _gameListFragment);
        }
        _gameListFragment.setOnGameChosenListener(this);

        transaction.commit();
    }

    @Override
    public void onGameChosen(int gameId) {
        _gridsFragment.setCurrentGame(gameId);
    }

    @Override
    public void onNewGame() {
        _gridsFragment.addNewGame();
    }
}
