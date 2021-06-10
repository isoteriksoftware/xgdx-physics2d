package io.github.isoteriktech.xgdx.physics2d.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Utility functions for working with Box2D
 *
 * @author isoteriksoftware
 */
public class Box2dUtil {
    public static final int bottomBoundaryIndex = 0;
    public static final int rightBoundaryIndex = 1;
    public static final int topBoundaryIndex = 2;
    public static final int leftBoundaryIndex = 3;

    public static Body createBoxBody(World world, Vector2 dimens,
                                     Vector2 position, BodyDef.BodyType bodyType, float density,
                                     float restitution, float friction, float rotation) {
        float width = dimens.x;
        float height = dimens.y;
        float x = position.x + width/2;
        float y = position.y + height/2;

        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);
        bdef.angle = rotation * MathUtils.degreesToRadians;

        Body body = world.createBody(bdef);
        body.setType(bodyType);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.friction = friction;
        fdef.restitution = restitution;
        body.createFixture(fdef);

        shape.dispose();
        return(body);
    }

    public static Body createRoundBody(World world, float radius,
                                       Vector2 position, BodyDef.BodyType bodyType, float density,
                                       float restitution, float friction, float rotation) {
        float x = position.x + radius;
        float y = position.y + radius;

        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);
        bdef.angle = rotation * MathUtils.degreesToRadians;

        Body body = world.createBody(bdef);
        body.setType(bodyType);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.friction = friction;
        fdef.restitution = restitution;
        body.createFixture(fdef);

        shape.dispose();
        return(body);
    }

    public static Body createBody(World world,
                                  float[] vertices, BodyDef.BodyType bodyType,
                                  Vector2 dimens, Vector2 position,
                                  float density, float restitution,
                                  float friction, float rotation) {
        float width = dimens.x;
        float height = dimens.y;
        float x = position.x + width;
        float y = position.y + height;

        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);
        bdef.angle = rotation * MathUtils.degreesToRadians;

        Body body = world.createBody(bdef);
        body.setType(bodyType);

        PolygonShape shape = new PolygonShape();
        shape.set(vertices);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.friction = friction;
        fdef.restitution = restitution;
        body.createFixture(fdef);

        shape.dispose();
        return(body);
    }

    public static Body createDynamicBody(World world,
                                         float[] vertices, Vector2 dimens,
                                         Vector2 pos, float density, float restitution,
                                         float friction, float rotation) {
        return(createBody(world, vertices, BodyDef.BodyType.DynamicBody,
                dimens, pos, density,
                restitution, friction, rotation));
    }

    public static Body createStaticBody(World world,
                                        float[] vertices, Vector2 dimens,
                                        Vector2 pos, float density, float restitution,
                                        float friction, float rotation) {
        return(createBody(world, vertices, BodyDef.BodyType.StaticBody,
                dimens,pos, density,
                restitution, friction, rotation));
    }

    public static Body createBoxBody(World world, Vector2 dimens,
                                     Vector2 position, float density,
                                     float restitution, float friction, float rotation) {
        return(createBoxBody(world, dimens, position,
                BodyDef.BodyType.StaticBody, density, restitution,
                friction, rotation));
    }

    public static Body createBoxBody(World world, Vector2 dimens,
                                     Vector2 position, float density,
                                     float restitution, float friction) {
        return(createBoxBody(world, dimens, position,
                BodyDef.BodyType.StaticBody, density, restitution,
                friction, 0));
    }

    public static Body createBoxBody(World world, Vector2 dimens,
                                     Vector2 position) {
        return(createBoxBody(world, dimens, position,
                BodyDef.BodyType.StaticBody, 0, 0,
                0, 0));
    }

    public static Body boxBodyFor(io.github.isoteriktech.xgdx.Transform transform, World world,
                                  float density, float restitution, float friction) {
        Vector2 position = new Vector2(transform.position.x, transform.position.y);
        Vector2 dimens = new Vector2(transform.size.x, transform.size.y);

        return(createBoxBody(world, dimens, position,
                BodyDef.BodyType.StaticBody, density, restitution,
                friction, transform.getRotation()));
    }

    public static Body createDynamicBoxBody(World world, Vector2 dimens,
                                            Vector2 position, float density,
                                            float restitution, float friction, float rotation) {
        return(createBoxBody(world, dimens, position,
                BodyDef.BodyType.DynamicBody, density, restitution,
                friction, rotation));
    }

    public static Body createDynamicBoxBody(World world, Vector2 dimens,
                                            Vector2 position, float density,
                                            float restitution, float friction) {
        return(createBoxBody(world, dimens, position,
                BodyDef.BodyType.DynamicBody, density, restitution,
                friction, 0));
    }

    public static Body createDynamicBoxBody(World world, Vector2 dimens,
                                            Vector2 position) {
        return(createBoxBody(world, dimens, position,
                BodyDef.BodyType.DynamicBody, 0, 0,
                0, 0));
    }

    public static Body dynamicBoxBodyFor(io.github.isoteriktech.xgdx.Transform transform, World world,
                                         float density, float restitution, float friction) {
        Vector2 position = new Vector2(transform.position.x, transform.position.y);
        Vector2 dimens = new Vector2(transform.size.x, transform.size.y);

        return(createDynamicBoxBody(world, dimens, position,
                density, restitution,
                friction, transform.getRotation()));
    }

    public static Body createRoundBody(World world, float radius,
                                       Vector2 position, float density,
                                       float restitution, float friction, float rotation) {
        return(createRoundBody(world, radius, position,
                BodyDef.BodyType.StaticBody, density, restitution,
                friction, rotation));
    }

    public static Body createRoundBody(World world, float radius,
                                       Vector2 position, float density,
                                       float restitution, float friction) {
        return(createRoundBody(world, radius, position,
                BodyDef.BodyType.StaticBody, density, restitution,
                friction, 0));
    }

    public static Body createRoundBody(World world, float radius,
                                       Vector2 position) {
        return(createRoundBody(world, radius, position,
                BodyDef.BodyType.StaticBody, 0, 0,
                0, 0));
    }

    public static Body roundBodyFor(io.github.isoteriktech.xgdx.Transform transform, World world,
                                    float density, float restitution, float friction) {
        Vector2 position = new Vector2(transform.position.x, transform.position.y);
        float radius = transform.size.x/2;
        return(createRoundBody(world, radius, position,
                BodyDef.BodyType.StaticBody, density, restitution,
                friction, transform.getRotation()));
    }

    public static Body createDynamicRoundBody(World world, float radius,
                                              Vector2 position, float density,
                                              float restitution, float friction, float rotation) {
        return(createRoundBody(world, radius, position,
                BodyDef.BodyType.DynamicBody, density, restitution,
                friction, rotation));
    }

    public static Body createDynamicRoundBody(World world, float radius,
                                              Vector2 position, float density,
                                              float restitution, float friction) {
        return(createRoundBody(world, radius, position,
                BodyDef.BodyType.DynamicBody, density, restitution,
                friction, 0));
    }

    public static Body createDynamicRoundBody(World world, float radius,
                                              Vector2 position) {
        return(createRoundBody(world, radius, position,
                BodyDef.BodyType.DynamicBody, 0, 0,
                0, 0));
    }

    public static Body dynamicRoundBodyFor(io.github.isoteriktech.xgdx.Transform transform, World world,
                                           float density, float restitution, float friction) {
        Vector2 position = new Vector2(transform.position.x, transform.position.y);
        float radius = transform.size.x/2;
        return(createDynamicRoundBody(world, radius, position,
                density, restitution,
                friction, transform.getRotation()));
    }

    public static Body createWall(World world,
                                  Vector2 dimens, Vector2 pos, float friction) {
        return(createBoxBody(world, dimens,
                pos, 0, 0, friction));
    }

    public static Body createEdge(World world,
                                  Vector2 pos, Vector2 start,
                                  Vector2 end, float friction) {
        float x1 = start.x;
        float y1 = start.y;
        float x2 = end.x;
        float y2 = end.y;

        BodyDef bdef = new BodyDef();
        bdef.position.set(pos.x, pos.y);

        Body body = world.createBody(bdef);
        body.setType(BodyDef.BodyType.StaticBody);

        EdgeShape shape = new EdgeShape();
        shape.set(x1, y1, x2, y2);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = friction;
        body.createFixture(fdef);

        shape.dispose();
        return(body);
    }

    /**
     * Creates a boundary box around using given dimensions. {@link Body}(ies) within the boundary will bounce off it.
     * @param world the physics world
     * @param boundaryWidth the width of the boundary
     * @param boundaryHeight the height of the boundary
     * @param friction the friction of the bodies
     * @return world boundaries (array of generated bodies)
     */
    public static Body[] createBoundaryBox(World world, float boundaryWidth,
                                           float boundaryHeight, float friction) {
        Body[] walls = new Body[4];

        float size = 0.25f;
        Vector2 start = new Vector2(0, 0);
        Vector2 end   = new Vector2(boundaryWidth, 0);
        Vector2 pos = new Vector2(0, 0);

        walls[bottomBoundaryIndex] = createEdge(world, pos, start, end, friction);

        pos.set(0, boundaryHeight);
        walls[topBoundaryIndex] = createEdge(world, pos, start, end, friction);

        start.set(0, 0);
        end.set(0, boundaryHeight);
        pos.set(boundaryWidth, 0);

        walls[rightBoundaryIndex] = createEdge(world, pos, start, end, friction);

        pos.set(0, 0);
        walls[leftBoundaryIndex] = createEdge(world, pos, start, end, friction);

        return(walls);
    }

    /**
     * Removes one of the boundaries created using {@link #createBoundaryBox(World, float, float, float)}.
     * @param boundaries the boundaries
     * @param index the index of the boundary to remove. It should be one of {@link #topBoundaryIndex}, {@link #bottomBoundaryIndex}, {@link #leftBoundaryIndex}, {@link #bottomBoundaryIndex}
     * @return
     */
    public static Body[] removeWorldBoundary(Body[] boundaries, int index) {
        if(index >= boundaries.length ||
                index < 0)
            return(null);

        Body[] newBoundaries = new Body[boundaries.length-1];
        int added = 0;
        int max = newBoundaries.length;
        for(int i=0; i<boundaries.length; i++) {
            if(i != index) {
                if(added >= max)
                    break;

                newBoundaries[i] = boundaries[i];
                added += 1;
            }
        }

        return(newBoundaries);
    }
}