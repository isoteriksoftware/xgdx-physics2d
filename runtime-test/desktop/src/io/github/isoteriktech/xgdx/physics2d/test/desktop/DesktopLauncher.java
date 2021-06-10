package io.github.isoteriktech.xgdx.physics2d.test.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.isoteriktech.xgdx.physics2d.test.XGdxPhysics2dTest;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		new Lwjgl3Application(new XGdxPhysics2dTest(), config);
	}
}
