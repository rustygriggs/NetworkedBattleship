package edu.utah.cs4530.rusty.networkedbattleship;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Rusty on 10/24/2016.
 */
public class GameListFragment extends Fragment implements ListAdapter, View.OnClickListener {

    private ListView _listView;

    public static GameListFragment newInstance() {
        //this stuff was included when I just tabbed to write newInstance()
//        Bundle args = new Bundle();
//
//        GalleryFragment fragment = new GalleryFragment();
//        fragment.setArguments(args);
//        return fragment;

//
        return new GameListFragment();
    }

    public interface OnGameChosenListener {
        void onGameChosen(int gameId);
        void onNewGame();
    }

    OnGameChosenListener _onGameChosenListener = null;

    public void setOnGameChosenListener(OnGameChosenListener listener) {
        _onGameChosenListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout rootLayout = new LinearLayout(getActivity());
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        _listView = new ListView(getActivity());
        _listView.setAdapter(this);

        Button newGameButton = new Button(getActivity());
        newGameButton.setText("New Game");

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _onGameChosenListener.onNewGame();
                _listView.invalidateViews();
            }
        });

        rootLayout.addView(_listView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 4));
        rootLayout.addView(newGameButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        return rootLayout;

    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return GameObjectList.getInstance().getGameObjectsCount();
    }

    @Override
    public Object getItem(int i) {
        return GameObjectList.getInstance().readGame(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //TODO: write this method to show information
        TextView textView = new TextView(getActivity());
        String gameNumber = "Game " + (i + 1);
        textView.setText(gameNumber);
        textView.setBackgroundColor(Color.RED);
        textView.setId(i);

        textView.setOnClickListener(this);

        return textView;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        if (getCount() == 0) {
            return true;
        }
        else return false;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        _onGameChosenListener.onGameChosen(viewId);
    }
}
