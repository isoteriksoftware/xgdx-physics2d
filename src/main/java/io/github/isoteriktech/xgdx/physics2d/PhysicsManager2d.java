package io.github.isoteriktech.xgdx.physics2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Scene;

public class PhysicsManager2d extends Component implements ContactListener {
    /** The Box2D physics world */
    protected final World physicsWorld;

    /** Velocity iterations. Defaults to 8 */
    protected int velocityIterations;
    /** Position iterations. Defaults to 3 */
    protected int positionIterations;

    /** Enables/Disables physics simulations */
    protected boolean simulatePhysics;
    /** Whether the physics debug lines should be rendered. Useful for debugging physics bodies. */
    protected boolean renderPhysicsDebugLines;

    /** The list of physics bodies to be destroyed */
    protected Array<Body> garbagePhysicsBodies = new Array<>();

    /* For interpolating physics bodies. */
    private double accumulator;
    private double currentTime;

    /** The fixed time step for the Box2D physics engine. Defaults to 1f/60f (60 frames per second). */
    protected float physicsTimeStep = 1.0f/60.0f;

    /** For rendering physics debug lines. */
    protected Box2DDebugRenderer physicsDebugRenderer;

    private final GameObject.__ComponentIterationListener fixedUpdateIter, iterAEnter, iterBEnter, iterAExit, iterBExit;

    // The collision pool
    private final Collision2d.CollisionPool collisionPool;

    // For collision detection listening
    private Collision2d collisionA, collisionB;
    private boolean isSensorA, isSensorB;

    /**
     * Creates a new instance given the gravity of the physics world.
     * @param gravity the gravity
     */
    public PhysicsManager2d(Vector2 gravity) {
        velocityIterations = 8;
        positionIterations = 3;

        accumulator = 0.0;
        currentTime = TimeUtils.millis() / 1000.0;

        simulatePhysics = true;
        renderPhysicsDebugLines = false;

        physicsWorld = new World(gravity,true);
        physicsWorld.setContactListener(this);

        physicsDebugRenderer = new Box2DDebugRenderer();

        fixedUpdateIter = component -> {
            if (!component.isEnabled())
                return;

            Physics2d physics2d = toPhysics2d(component);
            if (physics2d != null)
                physics2d.fixedUpdate2d(physicsTimeStep);
        };

        iterAEnter = component -> {
            if (!component.isEnabled())
                return;

            Physics2d physics2d = toPhysics2d(component);
            if (physics2d == null)
                return;

            if (isSensorA)
                physics2d.onSensorEnter2d(collisionA);
            else
                physics2d.onCollisionEnter2d(collisionA);
        };

        iterBEnter = component -> {
            if (!component.isEnabled())
                return;

            Physics2d physics2d = toPhysics2d(component);
            if (physics2d == null)
                return;

            if (isSensorB)
                physics2d.onSensorEnter2d(collisionB);
            else
                physics2d.onCollisionEnter2d(collisionB);
        };

        iterAExit = component -> {
            if (!component.isEnabled())
                return;

            Physics2d physics2d = toPhysics2d(component);
            if (physics2d == null)
                return;

            if (isSensorA)
                physics2d.onSensorExit2d(collisionA);
            else
                physics2d.onCollisionExit2d(collisionA);
        };

        iterBExit = component -> {
            if (!component.isEnabled())
                return;

            Physics2d physics2d = toPhysics2d(component);
            if (physics2d == null)
                return;

            if (isSensorB)
                physics2d.onSensorExit2d(collisionB);
            else
                physics2d.onCollisionExit2d(collisionB);
        };

        collisionPool = new Collision2d.CollisionPool();
    }

    /**
     * Creates a new instance with gravity set to (0, -9.8f)
     */
    public PhysicsManager2d() {
        this(new Vector2(0, -9.8f));
    }

    private Physics2d toPhysics2d(Component component) {
        if (component instanceof Physics2d)
            return (Physics2d) component;

        return null;
    }

    /**
     * Sets the physics time step used by physics engine.
     * Currently the physics engine simulates at a fixed time step of (1f / 60f) 60 frames per second.
     * @param physicsTimeStep the fixed time step for physics simulation.
     */
    public void setPhysicsTimeStep(float physicsTimeStep)
    { this.physicsTimeStep = physicsTimeStep; }

    /**
     * Returns the current physics time step.
     * @return the fixed time step for physics simulation.
     */
    public float getPhysicsTimeStep()
    { return physicsTimeStep; }

    /**
     * Sets the gravity used for physics simulation.
     * @param gravity the gravity
     */
    public void setGravity(Vector2 gravity)
    { physicsWorld.setGravity(gravity); }

    /**
     * Toggles rendering of debug lines around physics bodies.
     * Debug lines can be rendered around physics bodies. This is very useful for debugging.
     * @param renderPhysicsDebugLines whether physics bodies debug lines should be rendered.
     */
    public void setRenderPhysicsDebugLines(boolean renderPhysicsDebugLines)
    { this.renderPhysicsDebugLines = renderPhysicsDebugLines; }

    /**
     *
     * @return whether debug lines are rendered for physics bodies
     */
    public boolean isRenderPhysicsDebugLines()
    { return renderPhysicsDebugLines; }

    /**
     * This is the recommended way to destroy a physics body.
     * The bodies are not destroyed immediately; they are queued until the current frame is completed before they get destroyed.
     * <strong>NEVER MANUALLY DELETE A BODY; IT COULD BREAK THE SIMULATION AND CRASH THE GAME</strong>
     * @param body the body to destroy
     */
    public void destroyPhysicsBody(Body body) {
        if (body != null && !garbagePhysicsBodies.contains(body, true))
            garbagePhysicsBodies.add(body);
    }

    /**
     *
     * @return the physics world ({@link World}).
     */
    public World getPhysicsWorld()
    { return physicsWorld; }

    /**
     * Sets the velocity iterations for physics simulation. High values produces more realistic simulations but higher values also eat up processing power.
     * Defaults to 8. Change it only when necessary.
     * @param velocityIterations the velocity iterations for physics simulation.
     */
    public void setVelocityIterations(int velocityIterations)
    { this.velocityIterations = velocityIterations; }

    /**
     *
     * @return the velocity iterations for physics simulation.
     */
    public int getVelocityIterations()
    { return velocityIterations; }

    /**
     * Sets the position iterations for physics simulation. High values produces more realistic simulations but higher values also eat up processing power.
     * Defaults to 3. Change it only when necessary.
     * @param positionIterations the position iterations for physics simulation.
     */
    public void setPositionIterations(int positionIterations)
    { this.positionIterations = positionIterations; }

    /**
     *
     * @return the position iterations for physics simulation.
     */
    public int getPositionIterations()
    { return positionIterations; }

    /**
     * Use this to determine if physics should be simulated or not.
     * @param simulatePhysics whether physics should be simulated or not.
     */
    public void setSimulatePhysics(boolean simulatePhysics)
    { this.simulatePhysics = simulatePhysics; }

    /**
     *
     * @return whether physics is simulated or not.
     */
    public boolean isSimulatePhysics()
    { return simulatePhysics; }

    /* Steps the physics world */
    protected void stepPhysicsWorld() {
        double newTime = TimeUtils.millis() / 1000.0;
        double frameTime = Math.min(newTime - currentTime, 0.25);

        currentTime = newTime;
        accumulator += frameTime;

        // Step once
        physicsWorld.step(physicsTimeStep, velocityIterations, positionIterations);

        // Update components
        Array<GameObject> gameObjects = scene.getGameObjects();
        for (GameObject go : gameObjects) {
            go.__forEachComponent(fixedUpdateIter);
        }

        // Interpolate the physics bodies to avoid temporal aliasing
        while(accumulator >= physicsTimeStep) {
            physicsWorld.step(physicsTimeStep, velocityIterations, positionIterations);
            accumulator -= physicsTimeStep;

            for (GameObject go : gameObjects) {
                go.__forEachComponent(fixedUpdateIter);
            }

            interpolateTransforms(gameObjects, (float)accumulator/physicsTimeStep);
        }
    }

    protected void interpolateTransforms(Array<GameObject> gameObjects, float alpha) {
        for (GameObject go : gameObjects) {
            RigidBody2d rigidBody2d = go.getComponent(RigidBody2d.class);
            if (rigidBody2d != null)
                rigidBody2d.__interpolate(alpha);
        }
    }

    protected void destroyPhysicsBodies() {
        for (Body body : garbagePhysicsBodies) {
            if (body != null) {
                physicsWorld.destroyBody(body);
            }
        }

        garbagePhysicsBodies.clear();
    }

    @Override
    public void update(float deltaTime) {
        if (simulatePhysics) {
            stepPhysicsWorld();
        }

        // destroy physics bodies scheduled for removal
        destroyPhysicsBodies();
    }

    @Override
    public void postRender(Array<GameObject> gameObjects) {
        if (renderPhysicsDebugLines)
            physicsDebugRenderer.render(physicsWorld, scene.getMainCamera().getCamera().combined);
    }

    @Override
    public void destroy() {
        physicsDebugRenderer.dispose();
        physicsWorld.dispose();
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {}

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        GameObject goA = null;
        GameObject goB = null;

        if (bodyA.getUserData() instanceof GameObject)
            goA = (GameObject)bodyA.getUserData();

        if (bodyB.getUserData() instanceof GameObject)
            goB = (GameObject)bodyB.getUserData();

        if (goA == null && goB == null)
            return;

        collisionA = collisionPool.obtain(contact, goB, contact.getFixtureA(), contact.getFixtureB());
        collisionB = collisionPool.obtain(contact, goA, contact.getFixtureB(), contact.getFixtureA());

        isSensorA = contact.getFixtureA().isSensor();
        isSensorB = contact.getFixtureB().isSensor();

        if (goA != null) {
            goA.__forEachComponent(iterAEnter);
        }

        if (goB != null) {
            goB.__forEachComponent(iterBEnter);
        }

        collisionPool.free(collisionA);
        collisionPool.free(collisionB);
    }

    @Override
    public void endContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        GameObject goA = null;
        GameObject goB = null;

        if (bodyA.getUserData() instanceof GameObject)
            goA = (GameObject)bodyA.getUserData();

        if (bodyB.getUserData() instanceof GameObject)
            goB = (GameObject)bodyB.getUserData();

        if (goA == null && goB == null)
            return;

        collisionA = collisionPool.obtain(contact, goB, contact.getFixtureA(), contact.getFixtureB());
        collisionB = collisionPool.obtain(contact, goA, contact.getFixtureB(), contact.getFixtureA());

        isSensorA = contact.getFixtureA().isSensor();
        isSensorB = contact.getFixtureB().isSensor();

        if (goA != null) {
            goA.__forEachComponent(iterAExit);
        }

        if (goB != null) {
            goB.__forEachComponent(iterBExit);
        }

        collisionPool.free(collisionA);
        collisionPool.free(collisionB);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    /**
     * Creates a new PhysicsManager2d, setup a gameObject to host it, add the gameObject to the scene, then returns the instance.
     * @param scene the host scene
     * @param gravity the gravity for the physics world
     * @return an instance of {@link PhysicsManager2d}
     */
    public static PhysicsManager2d setup(Scene scene, Vector2 gravity) {
        PhysicsManager2d physicsManager2d = new PhysicsManager2d(gravity);
        GameObject gameObject = GameObject.newInstance("PhysicsManager2d");
        gameObject.addComponent(physicsManager2d);
        scene.addGameObject(gameObject);

        return physicsManager2d;
    }

    /**
     * Creates a new PhysicsManager2d, setup a gameObject to host it, add the gameObject to the scene, then returns the instance.
     * Gravity defaults to (0, -9.8f)
     * @param scene the host scene
     * @return an instance of {@link PhysicsManager2d}
     */
    public static PhysicsManager2d setup(Scene scene) {
        return setup(scene, new Vector2(0, -9.8f));
    }
}