package io.wasabi.urg.elements.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.GameObject;
import io.wasabi.urg.elements.betting.Bet;
import io.wasabi.urg.elements.betting.WinBreakdown;
import io.wasabi.urg.managers.BallFrictionCalculator;
import io.wasabi.urg.managers.BallWeightCalculator;
import io.wasabi.urg.managers.RendererManager;
import io.wasabi.urg.managers.SoundManager;
import io.wasabi.urg.screens.GameScreen;
import io.wasabi.urg.state.RunState;
import io.wasabi.urg.util.tweens.Tween;

public class Ball extends GameObject {

    public enum State {
        SPINNING, // on outer track, no inward movement
        DROPPING, // still spinning moving inwards
        BOUNCING, // free physics against frets on inner wheel
        SETTLING, // almost stopped, easing into the centre of its pocket
        STOPPED // fully stopped in pocket, riding along with the wheel
    }

    private static final RendererManager RENDERER_MANAGER = RendererManager.getInstance();
    private static final ShapeRenderer SHAPE_RENDERER = RENDERER_MANAGER.getShapeRenderer();

    private static final SoundManager SOUND_MANAGER = SoundManager.getInstance();
    private static final String ROLL_SOUND = "spin1";
    private static final String[] BOUNCE_SOUNDS = {"bounce1", "bounce2", "bounce3", "bounce4"};

    private final World world;
    private final Body ball;
    private final float radius;
    private final Wheel wheel;

    // Used to track when to switch states
    private final Vector2 wheelCenter;
    private float innerWheelRadius;
    private float outerTrackRadius;

    // Used for fixed spinning in SPINNING AND DROPPING states
    private float currentAngleRad = 0f;
    private float currentRadius = 0f;

    // Used during Spin state
    private State state = State.STOPPED;
    private float launchSpeed = 1f;
    private float tangentialSpeed = 0f;
    private boolean freeSpin = false;
    private float frictionMultiplier = 1f;
    private float weightMultiplier = 1f;

    private Tween dropTween;
    private float settleTimer = 0f;
    private float lowSpeedTimer = 0f;
    private float bounceTimer = 0f;
    private float relativeSpeed = 0f;

    // The pocket the ball is settling into / stopped in, stored relative to the wheel's
    // rotation so the ball rides along with the wheel as it slows down
    private Tile pocket;
    private float pocketAngle = 0f;
    private float pocketRadius = 0f;
    // Bets are resolved once the wheel has come to rest under the settled ball
    private boolean resultPending = false;
    // Longest the wheel keeps turning after the ball has settled
    private static final float WHEEL_BRAKE_TIME = 1.2f;

    // Inward pull of the sloped bowl towards the pockets
    private static final float BOWL_PULL = 700f;
    // How much radial speed is kept when the ball hits the outer edge of the bowl
    private static final float BOWL_WALL_RESTITUTION = 0.3f;
    // Longest the ball may bounce before it is eased into whichever pocket it is over
    private static final float MAX_BOUNCE_TIME = 8f;

    // Sounds
    private long rollSoundId = -1;
    private float rollVolume = 0f;
    private float rollPitch = 1f;
    private float pendingImpactSpeed = 0f;
    private float bounceSoundCooldown = 0f;

    private static final float ROLL_VOLUME = 0.6f;
    private static final float ROLL_SMOOTHING = 10f;
    private static final float BOUNCE_VOLUME = 0.6f;
    private static final float MIN_IMPACT_SPEED = 25f; // quieter contacts are just rolling
    private static final float FULL_IMPACT_SPEED = 400f;
    private static final float BOUNCE_SOUND_COOLDOWN = 0.05f;

    // Visibility
    private boolean visible = false;

    // Used to cap the frame time between frames so a huge lag spike doesn't kill
    // the physics
    private static final float MAX_DELTA = 1f / 20f;

    // Framed timeStep for physics steps
    private static final float FIXED_TIMESTEP = 1f / 60f;
    private static final int MAX_STEPS_PER_FRAME = 8; // avoid spiral of death on big lag spikes

    // Keeps track of real time that has passed that hasn't been simulated yet
    private float physicsAccumulator = 0f;

    public Ball(World world, float ballRadius, Wheel wheel) {
        this.world = world;
        this.radius = ballRadius;
        this.wheel = wheel;
        this.wheelCenter = wheel.getPosition();

        BodyDef ballDef = new BodyDef();
        ballDef.type = BodyType.DynamicBody;
        ballDef.linearDamping = 0.05f;
        ballDef.angularDamping = 0.1f;
        ballDef.bullet = true;
        this.ball = world.createBody(ballDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(ballRadius);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 0.1f;
        fd.friction = 0.6f;
        // Box2D uses the bouncier of the two fixtures, so keep this low enough that
        // the pocket floor absorbs the ball while the frets still kick it around
        fd.restitution = 0.4f;
        ball.createFixture(fd);

        shape.dispose();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                recordImpact(contact);
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }

    /**
     * Launches the ball into a spin on the outer track.
     *
     * @param startAngleRad    starting angle around wheelCenter, radians
     * @param initialSpeed     initial tangential (linear) speed, units/sec — note
     *                         this is
     *                         NOT radians/sec, it's divided by radius internally to
     *                         get
     *                         angular velocity
     * @param outerTrackRadius radius of the outer track the ball starts on
     * @param innerWheelRadius radius of the inner wheel surface it will drop onto
     */
    public void launch(float startAngleRad, float initialSpeed, float outerTrackRadius, float innerWheelRadius) {
        freeSpin = false;
        launchBall(startAngleRad, initialSpeed, outerTrackRadius, innerWheelRadius);
    }

    public void launchFree(float startAngleRad, float initialSpeed,
                           float outerTrackRadius, float innerWheelRadius) {
        freeSpin = true;
        launchBall(startAngleRad, initialSpeed, outerTrackRadius, innerWheelRadius);
    }

    private void launchBall(float startAngleRad, float initialSpeed,
                            float outerTrackRadius, float innerWheelRadius) {
        this.outerTrackRadius = outerTrackRadius;
        this.innerWheelRadius = innerWheelRadius;
        this.launchSpeed = initialSpeed;
        this.tangentialSpeed = initialSpeed;
        this.settleTimer = 0f;
        this.lowSpeedTimer = 0f;
        this.bounceTimer = 0f;
        this.currentAngleRad = startAngleRad;
        this.currentRadius = outerTrackRadius;
        this.physicsAccumulator = 0f;
        this.pocket = null;
        this.resultPending = false;

        Vector2 startPos = new Vector2(
            wheelCenter.x + outerTrackRadius * (float) Math.cos(startAngleRad),
            wheelCenter.y + outerTrackRadius * (float) Math.sin(startAngleRad));
        ball.setActive(true);
        ball.setTransform(startPos, 0f);
        ball.setLinearVelocity(0f, 0f);
        ball.setAngularVelocity(0f);

        state = State.SPINNING;
        startRollSound();
    }

    /**
     * Updates the ball using different behaviour depending on the state it is in
     * @param delta The time in seconds since the last update
     */
    @Override
    public void update(float delta) {
        // Clamp so a lag spike doesn't destroy the simulation
        float dt = Math.min(delta, MAX_DELTA);

        switch (state) {
            case SPINNING:
                updateSpinning(dt);
                world.step(dt, 6, 2);
                break;
            case DROPPING:
                updateDropping(dt);
                world.step(dt, 6, 2);
                break;
            case BOUNCING:
            case SETTLING:
            case STOPPED:
                stepPhysicsFixed(dt);
                break;
        }

        if (state == State.SETTLING) {
            updateSettling(dt);
        }
        if (pocket != null) {
            placeInPocket();
        }
        if (resultPending && !wheel.isSpinning()) {
            resultPending = false;
            resolveResult();
        }

        updateSounds(dt);
    }

    /**
     * Advances post-drop physics in fixed-size chunks, accumulating
     * leftover real time between calls.
     * @param dt The time in seconds since the last update
     */
    private void stepPhysicsFixed(float dt) {
        physicsAccumulator += dt;

        int steps = 0;
        while (physicsAccumulator >= FIXED_TIMESTEP && steps < MAX_STEPS_PER_FRAME) {
            if (state == State.BOUNCING) {
                updateBouncing();
            }
            world.step(FIXED_TIMESTEP, 6, 2);
            physicsAccumulator -= FIXED_TIMESTEP;
            steps++;
        }
    }

    /**
     * Used to set position of the ball from the wheel center using angle in radian
     * and distance
     * @param angleRad The angle in radians from the wheel center
     * @param radialDist The distance from the wheel center
     */
    private void setPositionFromPolar(float angleRad, float radialDist) {
        Vector2 pos = new Vector2(
            wheelCenter.x + radialDist * (float) Math.cos(angleRad),
            wheelCenter.y + radialDist * (float) Math.sin(angleRad));
        ball.setTransform(pos, 0f);
    }

    /**
     * The velocity of the wheel's surface at a point, i.e. how fast a ball resting there would be moving.
     * @param point The point in world space
     */
    private Vector2 getWheelSurfaceVelocity(Vector2 point) {
        float omega = wheel.getBody().getAngularVelocity();
        return new Vector2(-omega * (point.y - wheelCenter.y), omega * (point.x - wheelCenter.x));
    }

    /**
     * Damps the ball's velocity relative to the wheel, which is what friction with the spinning
     * wheel does: the ball is dragged towards moving with the wheel rather than towards standing still.
     * @param dampingPerSecond Fraction of relative speed lost per second
     * @param surfaceVel The wheel's surface velocity under the ball
     */
    private void applyRelativeDamping(float dampingPerSecond, Vector2 surfaceVel) {
        float dampingThisFrame = 1f - (float) Math.pow(1f - dampingPerSecond, FIXED_TIMESTEP);
        Vector2 relVel = new Vector2(ball.getLinearVelocity()).sub(surfaceVel).scl(1f - dampingThisFrame);
        ball.setLinearVelocity(relVel.add(surfaceVel));
    }

    private void applyTangentialDamping(Vector2 surfaceVel) {
        Vector2 radialDir = new Vector2(wheelCenter).sub(ball.getPosition()).nor();
        Vector2 tangentDir = new Vector2(-radialDir.y, radialDir.x);

        Vector2 relVel = new Vector2(ball.getLinearVelocity()).sub(surfaceVel);
        float radialComp = relVel.dot(radialDir);
        float tangentComp = relVel.dot(tangentDir);

        float tangentialDampingPerSecond = BallWeightCalculator.adjustDampingPerSecond(0.6f * frictionMultiplier, weightMultiplier);
        float dampingThisFrame = 1f - (float) Math.pow(1f - tangentialDampingPerSecond, FIXED_TIMESTEP);
        tangentComp *= (1f - dampingThisFrame);

        Vector2 newVel = radialDir.scl(radialComp).add(tangentDir.scl(tangentComp)).add(surfaceVel);
        ball.setLinearVelocity(newVel);
    }

    /**
     * Stops the ball leaving the bowl: nothing else stops a fast ball flying outwards
     * past the track it dropped from, since the only walls are the frets and inner ring.
     * @param distance The ball's distance from the wheel center
     * @param radialInward Unit vector from the ball towards the wheel center
     */
    private void keepInsideBowl(float distance, Vector2 radialInward) {
        float limit = innerWheelRadius - radius;
        if (distance <= limit) {
            return;
        }

        Vector2 vel = new Vector2(ball.getLinearVelocity());
        float inwardSpeed = vel.dot(radialInward);
        if (inwardSpeed < 0f) {
            vel.sub(new Vector2(radialInward).scl(inwardSpeed * (1f + BOWL_WALL_RESTITUTION)));
            ball.setLinearVelocity(vel);
        }
        ball.setTransform(
            wheelCenter.x - radialInward.x * limit,
            wheelCenter.y - radialInward.y * limit,
            0f);
    }

    /**
     * Updates the ball's position and speed while it is in the SPINNING state.
     * @param delta The time in seconds since the last update
     */
    private void updateSpinning(float delta) {
        float angularVelocity = tangentialSpeed / currentRadius;
        currentAngleRad += angularVelocity * delta;

        setPositionFromPolar(currentAngleRad, currentRadius);

        float adjustedDecelerationFactor = BallFrictionCalculator.adjustDecelerationFactor(0.5f, frictionMultiplier);
        float decelerationFactor = BallWeightCalculator.adjustDecelerationFactor(adjustedDecelerationFactor, weightMultiplier);
        tangentialSpeed *= (float) Math.pow(decelerationFactor, delta);

        // Speed to enter DROPPING state
        float dropSpeedThreshold = BallWeightCalculator.adjustSpeedThreshold(400f, weightMultiplier);
        if (tangentialSpeed <= dropSpeedThreshold) {
            float targetRadius = innerWheelRadius - (2 * radius);
            // tune this — how long the drop takes
            float dropDuration = 0.6f;
            dropTween = new Tween(dropDuration, currentRadius, targetRadius,
                Tween.TweenStyle.QUAD, Tween.TweenDirection.IN);
            state = State.DROPPING;
        }
    }

    /**
     * Updates the ball's position and speed while it is in the DROPPING state.
     * @param delta The time in seconds since the last update
     */
    private void updateDropping(float delta) {
        float angularVelocity = tangentialSpeed / currentRadius;
        currentAngleRad += angularVelocity * delta;

        float previousRadius = currentRadius;
        currentRadius = dropTween.update(delta);
        // derived each frame, used for exit velocity
        float dropRadialSpeed = (previousRadius - currentRadius) / delta; // inward = positive

        setPositionFromPolar(currentAngleRad, currentRadius);

        float adjustedDropDecelerationFactor = BallFrictionCalculator.adjustDecelerationFactor(0.2f, frictionMultiplier);
        float dropDecelerationFactor = BallWeightCalculator.adjustDecelerationFactor(adjustedDropDecelerationFactor, weightMultiplier);
        tangentialSpeed *= (float) Math.pow(dropDecelerationFactor, delta);

        if (dropTween.isComplete() || currentRadius <= innerWheelRadius - (2 * radius)) {
            Vector2 tangentDir = new Vector2(
                -(float) Math.sin(currentAngleRad),
                (float) Math.cos(currentAngleRad));
            Vector2 radialOutDir = new Vector2(
                (float) Math.cos(currentAngleRad),
                (float) Math.sin(currentAngleRad));
            Vector2 exitVelocity = tangentDir.scl(tangentialSpeed)
                .add(radialOutDir.scl(-dropRadialSpeed));
            ball.setLinearVelocity(exitVelocity);

            // Bounce sounds now come from actual contacts with the frets
            state = State.BOUNCING;
        }
    }

    /**
     * Updates the ball's position and speed while it is in the BOUNCING state.
     */
    private void updateBouncing() {
        Vector2 position = ball.getPosition();
        Vector2 toCenter = new Vector2(wheelCenter).sub(position);
        float distance = toCenter.len();
        Vector2 radialInward = toCenter.nor();

        float bowlPullMag = ball.getMass() * BOWL_PULL;
        ball.applyForceToCenter(new Vector2(radialInward).scl(bowlPullMag), true);

        keepInsideBowl(distance, radialInward);


        float frictionAdjustedDamping = BallFrictionCalculator.adjustDampingPerSecond(0.25f, frictionMultiplier);
        Vector2 surfaceVel = getWheelSurfaceVelocity(ball.getPosition());
        applyRelativeDamping(BallWeightCalculator.adjustDampingPerSecond(frictionAdjustedDamping, weightMultiplier), surfaceVel);
        applyTangentialDamping(surfaceVel);

        relativeSpeed = new Vector2(ball.getLinearVelocity()).sub(surfaceVel).len();
        bounceTimer += FIXED_TIMESTEP;

        // Only a ball down among the frets can settle, not one still rolling in from the rim
        boolean inPocket = distance <= wheel.getRadius() + wheel.getTileSize();

        // Speed (relative to the wheel) to enter SETTLE state
        float frictionAdjustedThreshold = BallFrictionCalculator.adjustSpeedThreshold(50f, frictionMultiplier);
        float settleSpeedThreshold = BallWeightCalculator.adjustSpeedThreshold(frictionAdjustedThreshold, weightMultiplier);
        if (relativeSpeed <= settleSpeedThreshold && inPocket) {
            lowSpeedTimer += FIXED_TIMESTEP;

            // must stay slow this long before settling
            float frictionAdjustedTime = BallFrictionCalculator.adjustTimeRequired(0.5f, frictionMultiplier);
            float lowSpeedTimeRequired = BallWeightCalculator.adjustTimeRequired(frictionAdjustedTime, weightMultiplier);
            if (lowSpeedTimer >= lowSpeedTimeRequired) {
                startSettling();
            }
        } else {
            lowSpeedTimer = 0f;
        }

        if (state == State.BOUNCING && bounceTimer >= MAX_BOUNCE_TIME) {
            startSettling();
        }
    }

    /**
     * Picks the pocket under the ball and hands the ball over from Box2D to riding along with the wheel.
     */
    private void startSettling() {
        Vector2 offset = new Vector2(ball.getPosition()).sub(wheelCenter);
        float worldAngle = MathUtils.atan2(offset.y, offset.x);

        Tile tile = wheel.getTileAtAngle(worldAngle);
        if (tile == null) {
            return;
        }

        pocket = tile;
        pocketAngle = worldAngle - wheel.getRotation();
        pocketRadius = offset.len();

        // The ball's position is driven by the wheel from here on, so take it out of the simulation
        ball.setLinearVelocity(0f, 0f);
        ball.setAngularVelocity(0f);
        ball.setActive(false);

        settleTimer = 0f;
        state = State.SETTLING;
    }

    /**
     * Updates the ball's position while it is in the SETTLING state, easing it into the centre of its pocket.
     * @param delta The time in seconds since the last update
     */
    private void updateSettling(float delta) {
        float settleTimeRequired = BallWeightCalculator.adjustTimeRequired(0.5f / frictionMultiplier, weightMultiplier);
        settleTimer += delta;

        float targetAngle = wheel.getTileCenterOffset(pocket);
        float seatRadius = wheel.getRadius() + radius + 0.5f;

        // Rate chosen so the ball is ~99% of the way there when the settle time runs out
        float ease = 1f - (float) Math.exp(-5f / settleTimeRequired * delta);
        pocketAngle += wrapAngle(targetAngle - pocketAngle) * ease;
        pocketRadius += (seatRadius - pocketRadius) * ease;

        if (settleTimer >= settleTimeRequired) {
            pocketAngle = targetAngle;
            pocketRadius = seatRadius;
            finalizeStop();
        }
    }

    /**
     * Stops the ball in its pocket and has the wheel brake, so the result is revealed as the wheel comes to rest.
     */
    private void finalizeStop() {
        state = State.STOPPED;
        resultPending = true;
        wheel.brake(WHEEL_BRAKE_TIME);
    }

    /**
     * Places the ball in its pocket using the wheel's current rotation, so it turns with the wheel.
     */
    private void placeInPocket() {
        setPositionFromPolar(wheel.getRotation() + pocketAngle, pocketRadius);
    }

    /**
     * Wraps an angle to the range [-PI, PI].
     */
    private static float wrapAngle(float angle) {
        angle %= MathUtils.PI2;
        if (angle > MathUtils.PI) {
            angle -= MathUtils.PI2;
        } else if (angle < -MathUtils.PI) {
            angle += MathUtils.PI2;
        }
        return angle;
    }

    /**
     * Resolves bets for the pocket the ball stopped in and triggers any necessary game state changes.
     */
    private void resolveResult() {
        List<Bet> savedBets = new ArrayList<>(Roulette.getInstance().getRunState().getActiveBets());

        Tile tile = pocket;

        Roulette.getInstance().getRunState().setLastTile(tile);

        WinBreakdown result = Roulette.getInstance().getRunState().resolveActiveBetsDetailed();
        GameScreen gameScreen = Roulette.getInstance().getGameScreen();
        RunState runState = Roulette.getInstance().getRunState();
        Vector2 topCenterAnchor = new Vector2(0f, Roulette.getInstance().getWorldHeight()/2 - 60f);
        Vector2 impactPosition = new Vector2(-500f, 200f);
        gameScreen.getWinAnimation().start(
            result,
            topCenterAnchor,
            impactPosition,
            () ->  {
                // What to run after the win animation finishes
                runState.clearActiveBets();
                runState.applyWinBreakdown(result);

                Roulette.getInstance().getRoundManager().recordSpin(freeSpin);

                boolean freeSpinRequested = Roulette.getInstance().getRunState().consumeFreeSpinRequest();


                if (Roulette.getInstance().getRunState().getChips() >= Roulette.getInstance().getRoundManager()
                    .getCurrentConfig().getQuota()) {
                    return;
                }

                if (freeSpinRequested) {
                    Roulette.getInstance().getRunState().getActiveBets().addAll(savedBets);
                    Roulette.getInstance().getGameScreen().freeSpin();
                }
            }
        );
    }

    /**
     * Records how hard the ball hit something, so a bounce sound matching the impact can be played.
     */
    private void recordImpact(Contact contact) {
        if (state != State.BOUNCING) {
            return;
        }

        Body other;
        if (contact.getFixtureA().getBody() == ball) {
            other = contact.getFixtureB().getBody();
        } else if (contact.getFixtureB().getBody() == ball) {
            other = contact.getFixtureA().getBody();
        } else {
            return;
        }

        WorldManifold manifold = contact.getWorldManifold();
        if (manifold.getNumberOfContactPoints() == 0) {
            return;
        }

        Vector2 point = manifold.getPoints()[0];
        Vector2 relVel = new Vector2(ball.getLinearVelocityFromWorldPoint(point))
            .sub(other.getLinearVelocityFromWorldPoint(point));
        float impactSpeed = Math.abs(relVel.dot(manifold.getNormal()));
        pendingImpactSpeed = Math.max(pendingImpactSpeed, impactSpeed);
    }

    private void startRollSound() {
        stopRollSound();
        rollVolume = 1f;
        rollPitch = getRollPitch(1f);
        rollSoundId = SOUND_MANAGER.loopSound(ROLL_SOUND, rollVolume * ROLL_VOLUME, rollPitch);
    }

    private void stopRollSound() {
        SOUND_MANAGER.stopSound(ROLL_SOUND, rollSoundId);
        rollSoundId = -1;
    }

    private static float getRollPitch(float speedRatio) {
        return 0.8f + 0.4f * speedRatio;
    }

    /**
     * Keeps the rolling sound in step with how fast the ball is actually rolling, and plays
     * bounce sounds for the contacts recorded during this frame's physics steps.
     * @param delta The time in seconds since the last update
     */
    private void updateSounds(float delta) {
        bounceSoundCooldown -= delta;
        if (pendingImpactSpeed >= MIN_IMPACT_SPEED && bounceSoundCooldown <= 0f) {
            float strength = MathUtils.clamp(pendingImpactSpeed / FULL_IMPACT_SPEED, 0f, 1f);
            SOUND_MANAGER.playSound(
                BOUNCE_SOUNDS[MathUtils.random(BOUNCE_SOUNDS.length - 1)],
                BOUNCE_VOLUME * (0.2f + 0.8f * strength),
                MathUtils.random(0.9f, 1.1f));
            bounceSoundCooldown = BOUNCE_SOUND_COOLDOWN;
        }
        pendingImpactSpeed = 0f;

        if (rollSoundId == -1) {
            return;
        }

        float speedRatio = MathUtils.clamp(tangentialSpeed / launchSpeed, 0f, 1f);
        float targetVolume;
        float targetPitch;
        switch (state) {
            case SPINNING:
                targetVolume = 0.35f + 0.65f * speedRatio;
                targetPitch = getRollPitch(speedRatio);
                break;
            case DROPPING:
                // Fades as the ball leaves the smooth track for the frets
                float dropProgress = MathUtils.clamp(dropTween.getAlpha(), 0f, 1f);
                targetVolume = (0.35f + 0.65f * speedRatio) * (1f - 0.6f * dropProgress);
                targetPitch = getRollPitch(speedRatio);
                break;
            case BOUNCING:
                // Rolling across the wheel, relative to the wheel's own movement
                float rollRatio = MathUtils.clamp(relativeSpeed / 500f, 0f, 1f);
                targetVolume = 0.4f * rollRatio;
                targetPitch = 0.7f + 0.3f * rollRatio;
                break;
            default:
                targetVolume = 0f;
                targetPitch = rollPitch;
                break;
        }

        float smoothing = 1f - (float) Math.exp(-ROLL_SMOOTHING * delta);
        rollVolume += (targetVolume - rollVolume) * smoothing;
        rollPitch += (targetPitch - rollPitch) * smoothing;

        if ((state == State.SETTLING || state == State.STOPPED) && rollVolume < 0.01f) {
            stopRollSound();
            return;
        }
        SOUND_MANAGER.updateSound(ROLL_SOUND, rollSoundId, rollVolume * ROLL_VOLUME, rollPitch);
    }

    public State getState() {
        return state;
    }

    public World getWorld() {
        return world;
    }

    public Body getBody() {
        return ball;
    }

    public float getFrictionMultiplier() { return frictionMultiplier; }

    public void setFrictionMultiplier(float frictionMultiplier) {
        this.frictionMultiplier = frictionMultiplier;
    }

    public float getWeightMultiplier() {
        return weightMultiplier;
    }

    /**
     * Sets a multiplier applied to the ball's effective weight, used by cards
     * such as OverweightSticker to make it decelerate faster and drop onto
     * the wheel sooner. 1f is the default weight.
     */
    public void setWeightMultiplier(float weightMultiplier) {
        if (weightMultiplier <= 0f) {
            throw new IllegalArgumentException("weightMultiplier must be greater than 0");
        }
        this.weightMultiplier = weightMultiplier;
    }

    @Override
    public void render() {
        if (visible) {
            SHAPE_RENDERER.begin(ShapeType.Filled);
            SHAPE_RENDERER.circle(
                ball.getPosition().x,
                ball.getPosition().y,
                radius);
            SHAPE_RENDERER.end();
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void dispose() {
        stopRollSound();
        world.destroyBody(ball);
    }}
