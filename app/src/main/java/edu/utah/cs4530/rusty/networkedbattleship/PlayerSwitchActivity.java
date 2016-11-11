package edu.utah.cs4530.rusty.networkedbattleship;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class PlayerSwitchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean player1Wins = getIntent().getBooleanExtra("player_1_wins", false);
        boolean player2Wins = getIntent().getBooleanExtra("player_2_wins", false);
        int missileIndex = getIntent().getIntExtra("missileIndex", 0);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("missileIndex", missileIndex);
        setResult(RESULT_OK, returnIntent);
        Button returnButton = new Button(this);

        if (player1Wins) {
            returnButton.setText("Game over. Player 1 wins");
        }
        else if (player2Wins) {
            returnButton.setText("Game over. Player 2 wins");
        }
        else {
            returnButton.setText("Next Player's turn. \n Hand the device to your opponent.\n " +
                    "Next Player: Click here when ready");
        }
        setContentView(returnButton);

        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }
}
