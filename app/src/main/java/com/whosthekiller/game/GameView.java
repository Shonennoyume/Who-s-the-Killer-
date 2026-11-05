
package com.whosthekiller.game;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GameView extends GLSurfaceView {

    private final GameRenderer renderer;

    public GameView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        renderer = new GameRenderer();

        setRenderer(renderer);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
