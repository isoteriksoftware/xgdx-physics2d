package io.github.isoteriktech.xgdx.physics2d;

import io.github.isoteriktech.xgdx.Component;

/**
 * A Physics2d component defines callbacks for various physics events like collisions and updates.
 * This component's callbacks gets called by {@link PhysicsManager2d} whenever a physics events involving the host gameObject occurs.
 *
 * @author isoteriksoftware
 */
public class Physics2d extends Component {
    /**
     * Called when the physics engine is updated.
     * This is where you'll typically put physics related update codes.
     * @param timeStep the physics time step.
     */
    public void fixedUpdate2d(float timeStep) {}

    /**
     * Called when the game object starts colliding.
     * @param collision the collision data
     */
    public void onCollisionEnter2d(Collision2d collision) {}

    /**
     * Called when the game object stops colliding.
     * @param collision the collision data
     */
    public void onCollisionExit2d(Collision2d collision) {}

    /**
     * Called when the game object's sensor starts colliding.
     * @param collision the collision data
     */
    public void onSensorEnter2d(Collision2d collision) {}

    /**
     * Called when the host game object's sensor stops colliding.
     * @param collision the collision data
     */
    public void onSensorExit2d(Collision2d collision) {}
}
