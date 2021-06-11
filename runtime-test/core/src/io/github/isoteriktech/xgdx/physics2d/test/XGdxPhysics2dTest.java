package io.github.isoteriktech.xgdx.physics2d.test;

import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.XGdxGame;
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions;

public class XGdxPhysics2dTest extends XGdxGame {

	@Override
	protected Scene initGame() {
		//splashTransition = SceneTransitions.fade(1f);
		return new MultipleColliderTest();
	}
}