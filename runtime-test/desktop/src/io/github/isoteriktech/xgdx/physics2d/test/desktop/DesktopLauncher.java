package io.github.isoteriktech.xgdx.physics2d.test.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.isoteriktech.xgdx.physics2d.test.XGdxPhysics2dTest;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new XGdxPhysics2dTest(), config);
	}
}
