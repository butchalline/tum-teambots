using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;

using teambot.communication;

namespace teambot.Bot
{
    class Robot
    {
        public enum RobotStates
        {
            Idle,
            VelocityMode,
            PositionMode
        }


        #region Properties
        public const float WheelDiameter = 17.0f * Map.CmToPixel;
        public const float WheelPerimeter = ((float)Math.PI * WheelDiameter) * Map.CmToPixel;
        public const float AxialDistance = 27.5f * Map.CmToPixel;
        public const int maxRPM = 114;
        public const int hexRPM = 1023;
        public const float scaleFactor = .23f;
        public const float SensorToHalfSizeRelation = 0.7f; //(Texture.Size / 2) * Position relative to middle...
        private const float _Threshold = 0.05f;
        public readonly object _lockObject = new object();

        private RobotStates _RobotState = RobotStates.Idle;

        /// <summary>
        /// Debug Index for Debug Message Writer
        /// </summary>
        private int? _DebugIndex = null;

        /// <summary>
        /// Velocity of the right Wheel
        /// </summary>
        private int _vRight;

        /// <summary>
        /// Velocity of the left Wheel
        /// </summary>
        private int _vLeft;

        /// <summary>
        /// The current Position
        /// </summary>
        private Vector2 _Position;

        /// <summary>
        /// The target Position of the Robot in Position Mode
        /// </summary>
        private Vector2 _TargetPosition;

        /// <summary>
        /// The target Angle of the Robot in Position Mode
        /// </summary>
        private float _TargetAngle;

        /// <summary>
        /// Infrared Sensors of the Bot
        /// </summary>
        private InfraredSensor _InfraSensor;

        private bool _PositionForward = true;
        private bool _AngleClockwise = true;

        private Texture2D _BotTexture;
        private Map _Map;
        private float _CurrentRightSensorDistance;
        private float _CurrentMiddleSensorDistance;
        private float _CurrentLeftSensorDistance;
        #endregion

        public Robot(Map map)
        {
            this._Map = map;
            Angle = 0;
            Position = new Vector2(400, 450);
            _TargetPosition = new Vector2(400, 450);
            PositionReached = false;
            PositionRequested = false;
        }

        public Vector2 Position
        {
            get { return _Position; }
            set { _Position = value; }
        }

        public float Angle
        {
            get;
            set;
        }

        public bool PositionReached
        {
            get;
            set;
        }

        public bool PositionRequested
        {
            get;
            set;
        }

        public bool DrawBot { get; set; }

        internal void draw(ref SpriteBatch spriteBatch)
        {
            if (DrawBot)
                spriteBatch.Draw(_BotTexture, Position, null, Color.White, (float)Angle, new Vector2(_BotTexture.Width / 2, _BotTexture.Height / 2), scaleFactor, SpriteEffects.None, 1);
            _InfraSensor.Draw(ref spriteBatch);
        }
        internal void update(GameTime gameTime)
        {
            lock (_lockObject)
            {
                if (_RobotState == RobotStates.PositionMode)
                {
                    Vector2 t;
                    float velocity = 400;
                    if (!targetPositionReached(out t))
                    {
                        PositionReached = false;
                        if (t.Length() <= 4)
                            velocity = 400 - ((4 - t.Length()) * 100);
                        if (velocity < 1)
                            velocity = 1;
                        if (_PositionForward)
                        {
                            _vLeft = (int)velocity;
                            _vRight = (int)velocity;
                        }
                        else
                        {
                            _vLeft = (int)-velocity;
                            _vRight = (int)-velocity;
                        }
                    }
                    else if (!targetAngleReached())
                    {
                        PositionReached = false;
                        float a = _TargetAngle - Angle;

                        a = a > (float)Math.PI ? a - 2f * (float)Math.PI : a;
                        a = a < -(float)Math.PI ? a + 2f * (float)Math.PI : a;

                        if (Math.Abs(a) <= MathHelper.ToRadians(4))
                            velocity = 400 - ((4 - Math.Abs(MathHelper.ToDegrees(a))) * 100);
                        if (velocity < 1)
                            velocity = 1;
                        if (_AngleClockwise)
                        {
                            _vLeft = (int)velocity;
                            _vRight = (int)-velocity;
                        }
                        else
                        {
                            _vLeft = (int)-velocity;
                            _vRight = (int)velocity;
                        }
                    }
                    else
                    {
                        _vLeft = 0;
                        _vRight = 0;
                        if (PositionRequested == true)
                        {
                            PositionRequested = false;
                            PositionReached = true;
                        }
                    }
                }


                /**
                 * Strecke
                 * (dR + dL) / 2
                 * Winkel
                 * (dR - dL) / Achsabstand
                 * 
                 * x = x + strecke * cos(winkel)
                 * y = y + strecke * sin(winkel)
                 * winkel = winkel + winkel
                 */

                //Page 18 dynamixel documentation... 0x3ff speed = 114 RPM ( devided by 60 = rounds per second)
                double dR = 0.5f * gameTime.ElapsedGameTime.TotalSeconds * WheelPerimeter * ((_vRight * (114f / 1023f)) / 60f);
                double dL = 0.5f * gameTime.ElapsedGameTime.TotalSeconds * WheelPerimeter * ((_vLeft * (114f / 1023f)) / 60f);
                double s = (dR + dL) / 2;
                double w = (dR - dL) / AxialDistance;

                Angle -= (float)w;
                if (Angle < -Math.PI)
                    Angle += (float)(2 * Math.PI);
                else if (Angle > Math.PI)
                    Angle -= (float)(2 * Math.PI);

                _Position.X += (float)(s * Math.Sin(Angle));
                _Position.Y -= (float)(s * Math.Cos(Angle));


                _CurrentLeftSensorDistance = _InfraSensor.checkLeftSensor(Position, (float)Angle);
                _CurrentMiddleSensorDistance = _InfraSensor.checkMiddleSensor(Position, (float)Angle);
                _CurrentRightSensorDistance = _InfraSensor.checkRightSensor(Position, (float)Angle);

                _DebugIndex = DebugLayer.addString("VLeft: " + this._vLeft.ToString() + "\nVRight: " + this._vRight.ToString() + "\nAngle: " + Math.Round(MathHelper.ToDegrees((float)(this.Angle)), 2).ToString() + "\nRobotPos: \nX: " + Math.Round(this.Position.X * Map.PixelToCm, 2).ToString() + " Y: " + Math.Round(this.Position.Y * Map.PixelToCm, 2).ToString()
                    + "\nSensoren L|M|R:\n| " + _CurrentLeftSensorDistance.ToString() + " | " + _CurrentMiddleSensorDistance + " | " + _CurrentRightSensorDistance.ToString() + " | ", _DebugIndex);
            }
        }

        internal void setVelocity(int velocityLeft, int veloctiyRight, WheelDirection wheelDirectionLeft, WheelDirection wheelDirectionRight)
        {
            lock (_lockObject)
            {
                if (this._RobotState != RobotStates.VelocityMode)
                    return;
                _vLeft = velocityLeft * (wheelDirectionLeft.Equals(WheelDirection.Forwards) ? 1 : -1);
                _vRight = veloctiyRight * (wheelDirectionRight.Equals(WheelDirection.Forwards) ? 1 : -1);
            }
        }

        public void LoadContent(ContentManager manager)
        {
            this._BotTexture = manager.Load<Texture2D>("TeamBot");
            this._InfraSensor = new InfraredSensor(_Map,
                new Vector2(((_BotTexture.Width / 2) * SensorToHalfSizeRelation) * scaleFactor, ((_BotTexture.Height / 2) - 2) * scaleFactor),
                new Vector2(-((_BotTexture.Width / 2) * SensorToHalfSizeRelation) * scaleFactor, ((_BotTexture.Height / 2) - 2) * scaleFactor),
                new Vector2(0, ((_BotTexture.Height / 2) - 2) * scaleFactor));
            this._InfraSensor.LoadContent(manager);
        }

        internal byte getLeftSensorDistance()
        {
            return (byte)_CurrentLeftSensorDistance;
        }

        internal byte getMiddleSensorDistance()
        {
            return (byte)_CurrentMiddleSensorDistance;
        }

        internal byte getRightSensorDistance()
        {
            return (byte)_CurrentRightSensorDistance;
        }


        internal void changeState(RobotStates targetState)
        {
            this._RobotState = targetState;
        }

        internal bool targetPositionReached(out Vector2 distance)
        {
            Vector2 dist = Vector2.Subtract(_TargetPosition, _Position);
            distance = dist;
            if (distance.Length() <= _Threshold)
                return true;
            return false;
        }

        internal bool targetAngleReached()
        {
            float distance = _TargetAngle - Angle;
            if (distance <= MathHelper.ToRadians(_Threshold) && distance >= -MathHelper.ToRadians(_Threshold))
                return true;
            return false;
        }

        internal void setPosition(int p)
        {
            lock (_lockObject)
            {
                if (this._RobotState != RobotStates.PositionMode)
                    return;
                _PositionForward = true;
                if (p < 0)
                    _PositionForward = false;

                double cAngle = Angle + Math.PI;
                float p2 = p * 0.1f * Map.CmToPixel;
                _TargetPosition.X = (float)(_Position.X - p2 * Math.Sin(cAngle));
                _TargetPosition.Y = (float)(_Position.Y + p2 * Math.Cos(cAngle));
            }
        }

        internal void setAngle(int p)
        {
            if (this._RobotState != RobotStates.PositionMode)
                return;
            float p2 = Angle + MathHelper.ToRadians(p / 100f);
            if (p2 < -Math.PI)
                p2 += (float)(2 * Math.PI);
            else if (p2 > Math.PI)
                p2 -= (float)(2 * Math.PI);
            _AngleClockwise = true;
            if (p < 0)
                _AngleClockwise = false;
            _TargetAngle = p2;
        }
    }
}
