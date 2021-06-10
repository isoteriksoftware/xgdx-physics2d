package io.github.isoteriktech.xgdx.physics2d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.isoteriktech.xgdx.Component;
import io.github.isoteriktech.xgdx.physics2d.colliders.Collider;

/**
 * This component is a wrapper for a box2d {@link com.badlogic.gdx.physics.box2d.Body} instance. Automatically updates the transform of a gameObject to match
 * the physics body transform.
 * It relies on one or more {@link io.github.isoteriktech.xgdx.physics2d.colliders.Collider}s to generate collision shapes for the host game object.
 *
 * <strong>Note:</strong> this component uses the host game object as the user data for the {@link com.badlogic.gdx.physics.box2d.Body} for internal uses. Do not override the user data!
 *
 * @author isoteriksoftware
 */
public class RigidBody2d extends Physics2d {
    /** A dynamic body type. Dynamic bodies have positive mass, non-zero velocity determined by forces and moved by solver */
    public static final BodyDef.BodyType DynamicBody = BodyDef.BodyType.DynamicBody;

    /** A static body type. Static bodies have zero mass, zero velocity and may be manually moved */
    public static final BodyDef.BodyType StaticBody = BodyDef.BodyType.StaticBody;

    /** A kinematic body type. Kinematic bodies have zero mass and non-zero velocity set by user */
    public static final BodyDef.BodyType KinematicBody = BodyDef.BodyType.KinematicBody;

    private Body body;
    private BodyDef.BodyType bodyType;
    private final PhysicsMaterial2d material;

    private Array<Collider> colliders;

    protected boolean interpolate;

    protected final PhysicsManager2d physicsManager2d;

    /**
     * Creates a new instance given a body type and a physics material.
     * @param bodyType the body type
     * @param material the physics material
     * @param physicsManager2d the physics manager to use
     */
    public RigidBody2d(BodyDef.BodyType bodyType, PhysicsMaterial2d material, PhysicsManager2d physicsManager2d) {
        this.bodyType = bodyType;
        this.material = material;
        this.physicsManager2d = physicsManager2d;

        colliders = new Array<>();
        interpolate = true;
    }

    /**
     * Creates a new instance given a body type and no physics material. A default {@link PhysicsMaterial2d#PhysicsMaterial2d()} is used
     * @param bodyType the body type
     * @param physicsManager2d the physics manager to use
     */
    public RigidBody2d(BodyDef.BodyType bodyType, PhysicsManager2d physicsManager2d)
    { this(bodyType, new PhysicsMaterial2d(), physicsManager2d); }


    /**
     * This creates the physics body if it is not created yet and returns it.
     * <strong>Note:</strong> null will be returned if this component is not attached to a game object yet. If there is an existing body,
     * that body will be returned
     * @return the created physics body or null if this component is not attached yet.
     */
    public Body getBody() {
        if (body != null)
            return body;

        if (gameObject == null)
            return null;

        createBody(physicsManager2d.getPhysicsWorld());
        return body;
    }

    /**
     * Determines if the physics body is interpolated to prevent temporal aliasing
     * @param interpolate if interpolation should be enabled
     */
    public void setInterpolate(boolean interpolate) {
        this.interpolate = interpolate;
    }

    /**
     *
     * @return whether the physics body is interpolated to prevent temporal aliasing
     */
    public boolean isInterpolate() {
        return interpolate;
    }

    private void createAndAttachCollider(Collider collider) {
        FixtureDef fdef = collider.__getFixtureDef();
        if (fdef == null)
            return;

        fdef.friction = material.friction;
        fdef.restitution = material.bounciness;
        fdef.density = material.density;
        fdef.isSensor = collider.isSensor();
        fdef.filter.categoryBits = collider.getCategoryBits();
        fdef.filter.groupIndex = collider.getGroupIndex();
        fdef.filter.maskBits = collider.getMaskBits();

        Fixture fixture = body.createFixture(fdef);
        fixture.setUserData(collider.getUserData());
        collider.__setFixture(fixture);
        collider.__disposeShape();
    }

    /* Creates the physics body for the host game object */
    private void createBody(World physicsWorld) {
        // Offset the current game object position by half its dimension
        Vector2 pos = new Vector2(gameObject.transform.position.x,
                gameObject.transform.position.y);
        pos.add(gameObject.transform.size.x * .5f,
                gameObject.transform.size.y * .5f);

        // The origin of the game object must be at the center for simulation to work
        gameObject.transform.origin.set(gameObject.transform.size.x * .5f,
                gameObject.transform.size.y * .5f, 0);

        BodyDef bdef = new BodyDef();
        bdef.type = bodyType;
        bdef.angle = gameObject.transform.getRotation() * MathUtils.degreesToRadians;
        bdef.position.set(pos);

        // Create the body
        body = physicsWorld.createBody(bdef);
        body.setUserData(gameObject);

        // Create the collision shapes using available colliders
        for (Collider collider : colliders) {
            createAndAttachCollider(collider);
        }
    }

    /**
     * Interpolates the physic body to avoid temporal aliasing.
     * This method is called internally by the system and should never be called directly.
     * @param alpha the ratio of the time spent by the renderer to a fixed time steps
     */
    public void __interpolate(float alpha) {
        if (!interpolate)
            return;

        // We bail out if the body is either null or inactive
        if (body == null || !body.isActive())
            return;

        // Get the transform data from the physics body
        com.badlogic.gdx.physics.box2d.Transform transform =
                body.getTransform();
        Vector2 bodyPosition = transform.getPosition();

        // Offset the current body position by half the dimension of the game object
        // This effectively move the position from the center of the physics body to its lower left
        bodyPosition.sub(gameObject.transform.size.x * .5f,
                gameObject.transform.size.y * .5f);

        // Get the position of the game object
        Vector3 position = gameObject.transform.position;

        // Get the rotation
        float angle = gameObject.transform.getRotation();

        // Convert the physics body angle from radians to degrees
        float bodyAngle = transform.getRotation() * MathUtils.radiansToDegrees;

        // Interpolate the position
        position.x = bodyPosition.x * alpha + position.x * (1.0f - alpha);
        position.y = bodyPosition.y * alpha + position.y * (1.0f - alpha);

        // Interpolate the rotation
        gameObject.transform.setRotation(bodyAngle * alpha + angle * (1.0f - alpha));
    }

    private void __disposeBody() {
        if (body == null)
            return;

        physicsManager2d.destroyPhysicsBody(body);
        body = null;
        colliders.clear();
    }

    @Override
    public void attach() {
        // This body cannot have more than one instance of this Component
        if (hasComponent(RigidBody2d.class))
            throw new UnsupportedOperationException("A GameObject can have only one instance of RigidBody2d attached!");

        body = null;
        colliders.clear();

        // Grab available colliders
        Array<Collider> colls = getComponents(Collider.class);
        if (!colls.isEmpty()) {
            for (Collider coll : colls) {
                colliders.add(coll);
            }
        }

        // Creates the physics body
        createBody(physicsManager2d.getPhysicsWorld());
    }

    @Override
    public void detach() {
        // Destroy the physics body associated with this RigidBody
        this.__disposeBody();
    }

    @Override
    public void componentAdded(Component component) {
        // If the component added is a Collider then we have to add it to our list of colliders

        if (component instanceof Collider) {
            Collider collider = (Collider)component;
            if (!colliders.contains(collider,true)) {
                colliders.add(collider);

                // If we have a non-null physics body already then we have to attach this collider immediately
                if (body != null) {
                    createAndAttachCollider(collider);
                }
            }
        }
    }

    @Override
    public void componentRemoved(Component component) {
        // If the component removed is a Collider then we have to remove it from our list of colliders.
        // We also need to detach it from the body

        if (component instanceof Collider) {
            Collider collider = (Collider)component;
            if (!colliders.contains(collider,true))
                return;

            colliders.removeValue(collider, true);

            if (body != null) {
                body.destroyFixture(collider.getFixture());
            }
        }
    }

    @Override
    public void fixedUpdate2d(float timeStep) {
        // Attempt to create a body if none exists.
        // This should never happen but just in case
        if (body == null)
            createBody(physicsManager2d.getPhysicsWorld());

        // Apply updates only when we have a valid body
        if (body != null) {
            Transform transform = body.getTransform();
            Vector2 pos = transform.getPosition();
            float rotation = transform.getRotation();

            // Offset the position from the origin (center)
            pos.sub(gameObject.transform.size.x * 0.5f, gameObject.transform.size.y * 0.5f);

            // Convert the angle to degrees
            rotation *= MathUtils.radiansToDegrees;

            // Update the transform
            gameObject.transform.position.set(pos, 0);
            gameObject.transform.setRotation(rotation);
        }
    }
}