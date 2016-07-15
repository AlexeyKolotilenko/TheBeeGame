package com.test.alex.thebeegame;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.test.alex.thebeegame.R;
import com.test.alex.thebeegame.controller.GameActionHandler;
import com.test.alex.thebeegame.controller.GameController;
import com.test.alex.thebeegame.controller.ProductionFactory;
import com.test.alex.thebeegame.model.BaseLevelLoader;
import com.test.alex.thebeegame.model.Unit;
import com.test.alex.thebeegame.view.BattleFieldFragment;


public class BeeFieldActivity extends AppCompatActivity implements GameActionHandler {

    // Controls
    private BattleFieldFragment viewRepresentation;
    private CoordinatorLayout mainLayout;
    private FloatingActionButton hitBtn;

    //  Data
    private GameController gameController;
    private BaseLevelLoader.LevelLoaderErrorHandler levelLoaderErrorHandler;

    /**
     * Determine how fast will be auto-click (on long click)
     */
    private final int CLICKS_DELAY_MILLIS = 10;
    private boolean isLongClickPerforming;
    private Handler clickScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProductionFactory.init(this);
        initUI();
        initGameLogic();
    }

    private void initUI() {
        setContentView(R.layout.activity_bee_field);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mainLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        setSupportActionBar(toolbar);

        viewRepresentation = (BattleFieldFragment)getSupportFragmentManager().
                findFragmentById(R.id.battle_field_fragment);

        hitBtn = (FloatingActionButton) findViewById(R.id.fab);
        hitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameController.dispatchUserHit();
            }
        });

        hitBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongClickPerforming = true;
                repeatedClick();
                return true;
            }
        });

        hitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    isLongClickPerforming = false;
                }

                return false;
            }
        });
    }

    private void repeatedClick() {
        if(isLongClickPerforming) {
            gameController.dispatchUserHit();
            hitBtn.playSoundEffect(android.view.SoundEffectConstants.CLICK);

            clickScheduler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    repeatedClick();
                }
            }, CLICKS_DELAY_MILLIS);
        }
    }

    private void initGameLogic() {
        isLongClickPerforming = false;
        clickScheduler = new Handler();

        levelLoaderErrorHandler = new BaseLevelLoader.LevelLoaderErrorHandler() {
            @Override
            public void onCantReadDataFile(Exception e) {
                Toast.makeText(BeeFieldActivity.this, R.string.on_cant_read_data_file, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCantParseGameData(Exception e) {
                Toast.makeText(BeeFieldActivity.this, R.string.on_cant_read_data_file, Toast.LENGTH_SHORT).show();
            }
        };

        gameController = new GameController(levelLoaderErrorHandler, this);
        gameController.startRound(null);
    }

    @Override
    public void showNewHitData(Unit hitUnit, int startHP, Unit hitter, boolean isUserHit) {
        viewRepresentation.addHitResult(hitUnit, startHP, hitter, isUserHit);
    }

    @Override
    public void showPlayerWin() {
        Toast.makeText(BeeFieldActivity.this, R.string.user_win_toast_text, Toast.LENGTH_LONG).show();
        showStartNewGameSnackbar(R.string.user_win_toast_text);

        hitBtn.setVisibility(View.GONE);
        isLongClickPerforming = false;
    }

    public void showPlayerLost() {
        Toast.makeText(BeeFieldActivity.this, R.string.user_lost_toast_text, Toast.LENGTH_LONG).show();
        showStartNewGameSnackbar(R.string.user_lost_toast_text);

        hitBtn.setVisibility(View.GONE);
        isLongClickPerforming = false;
    }

    private void showStartNewGameSnackbar(int title) {
        final Snackbar bar = Snackbar.make(mainLayout, title, Snackbar.LENGTH_INDEFINITE);
        bar.setAction(R.string.click_to_start_new_game, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
            }
        });
        bar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);

                startNewRound();
            }
        });

        bar.show();
    }

    private void startNewRound() {
        viewRepresentation.clearList();
        hitBtn.setVisibility(View.VISIBLE);

        gameController.startRound(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bee_field, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_start_new_round) {
            startNewRound();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
