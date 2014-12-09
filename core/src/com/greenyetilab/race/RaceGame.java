package com.greenyetilab.race;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.ScreenUtils;
import com.greenyetilab.utils.log.NLog;

import java.util.Stack;

/**
 * Created by aurelien on 21/11/14.
 */
public class RaceGame extends Game {
    private Assets mAssets;
    private Stack<Screen> mScreenStack = new Stack<Screen>();

    public Assets getAssets() {
        return mAssets;
    }

    @Override
    public void create() {
        mAssets = new Assets();
        Box2D.init();
        showMainMenu();
    }

    public void showMainMenu() {
        Screen screen = new MainMenuScreen(this);
        setScreenAndDispose(screen);
    }

    public void start(MapInfo mapInfo) {
        NLog.i("mapName=%s", mapInfo.getTitle());
        Screen screen = new RaceGameScreen(this, mapInfo);
        setScreenAndDispose(screen);
    }

    private void setScreenAndDispose(Screen screen) {
        if (!mScreenStack.isEmpty()) {
            mScreenStack.pop().dispose();
        }
        pushScreen(screen);
    }

    public void showGameOverOverlay(MapInfo mapInfo) {
        showOverlay(mapInfo, "Game Over");
    }

    public void showFinishedOverlay(MapInfo mapInfo, float time) {
        float best = mapInfo.getBestTime();
        String text = "Finished in " + StringUtils.formatRaceTime(time);
        if (best == 0 || time < best) {
            text += "\n\nNew record!";
            if (best != 0) {
                text += "\n\nOld record was " + StringUtils.formatRaceTime(best);
            }
            mapInfo.setBestTime(time);
        } else if (best != 0) {
            text += "\n\nRecord is " + StringUtils.formatRaceTime(best);
        }
        showOverlay(mapInfo, text);
    }

    private void showOverlay(MapInfo mapInfo, String text) {
        TextureRegion bg = ScreenUtils.getFrameBufferTexture();
        setScreenAndDispose(new OverlayScreen(this, mapInfo, bg, text));
    }

    public static Preferences getPreferences() {
        return Gdx.app.getPreferences("com.greenyetilab.race");
    }

    public void pushScreen(Screen screen) {
        mScreenStack.push(screen);
        setScreen(screen);
    }

    public void popScreen() {
        assert(!mScreenStack.isEmpty());
        mScreenStack.pop().dispose();
        assert(!mScreenStack.isEmpty());
        setScreen(mScreenStack.peek());
    }
}
