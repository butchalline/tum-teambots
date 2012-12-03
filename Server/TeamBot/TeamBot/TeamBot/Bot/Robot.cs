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

using TeamBot.Communication;

namespace TeamBot.Bot
{
    class Robot
    {

        public Robot()
        {
            Angle = 0;
            Position = new Vector2(500, 750); 
        }

        private Vector2 _Position;

        public Vector2 Position
        {
            get { return _Position; }
            set { _Position = value; }
        }

        public double Angle
        {
            get;
            set;
        }

        public Texture2D Texture
        {
            get;
            set;
        }

        public const double WheelDiameter = 17;
        public const double WheelPerimeter = Math.PI * WheelDiameter;
        public const double AxialDistance = 27.5;
        public const int maxRPM = 114;
        public const int hexRPM = 1023;


        private int? _DebugIndex = null;

        /// <summary>
        /// Velocity of the right Wheel
        /// </summary>
        private int _vRight;

        /// <summary>
        /// Velocity of the left Wheel
        /// </summary>
        private int _vLeft;

        internal void draw(ref SpriteBatch spriteBatch)
        {
            _DebugIndex = DebugLayer.addString("VLeft: " + this._vLeft.ToString() + "\nVRight: " + this._vRight.ToString(), _DebugIndex);

            spriteBatch.Draw(Texture, Position, null, Color.White, (float) Angle, new Vector2(Texture.Width / 2, Texture.Height / 2), .23f, SpriteEffects.None, 1);
        }
        internal void update(GameTime gameTime)
        {
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

            double dR = 0.65f * gameTime.ElapsedGameTime.TotalSeconds * WheelPerimeter * ((_vRight * (114f / 1023f)) / 60f);
            double dL = 0.65f * gameTime.ElapsedGameTime.TotalSeconds * WheelPerimeter * ((_vLeft * (114f / 1023f)) / 60f);
            double s = (dR + dL) / 2;
            double w = (dR - dL) / AxialDistance;

            Angle -= w;
            _Position.X += (float) (s * Math.Sin(Angle));
            _Position.Y -= (float) (s * Math.Cos(Angle));
        }

        internal void setVelocity(int velocityLeft, int veloctiyRight, WheelDirection wheelDirectionLeft, WheelDirection wheelDirectionRight)
        {
            _vLeft = velocityLeft * (wheelDirectionLeft.Equals(WheelDirection.Forwards) ? 1 : -1);
            _vRight = veloctiyRight * (wheelDirectionRight.Equals(WheelDirection.Forwards) ? 1 : -1);
        }
    }
}
