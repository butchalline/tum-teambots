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

namespace teambot.Bot
{
    class InfraredSensor
    {
        public struct Line
        {
            public Vector2 start, stop;
        }

        public bool drawLeftLine
        {
            get;
            set;
        }

        public bool drawRightLine
        {
            get;
            set;
        }


        public bool drawMiddleLine
        {
            get;
            set;
        }

        Line _LeftSensorLine;
        Line _RightSensorLine;
        Line _MiddleSensorLine;

        Texture2D _RedInfra;
        Texture2D _GreenInfra;

        const int LeftSensorLength = 25;
        const int RightSensorLength = 25;
        const int MiddleSensorLength = 125;
        readonly Vector2 LeftSensorPosition;
        readonly Vector2 RightSensorPosition;
        readonly Vector2 MiddleSensorPosition;
        private Map _Map;


        public InfraredSensor(Map map, Vector2 leftPosition, Vector2 rightPosition, Vector2 middlePosition)
        {

            drawLeftLine = true;
            drawRightLine = true;
            drawMiddleLine = true;

            this.LeftSensorPosition = leftPosition;
            this.RightSensorPosition = rightPosition;
            this.MiddleSensorPosition = middlePosition;

            this._Map = map;
        }

        public float checkLeftSensor(Vector2 position, float angle)
        {
            /**
             * Rotation Matrix
             * | sin(a) | -cos(a) |
             * | cos(a) |  sin(a) |
             */
            double cAngle = angle + Math.PI;
            _LeftSensorLine.start.X = (float)(position.X + LeftSensorPosition.X * Math.Cos(cAngle) - LeftSensorPosition.Y * Math.Sin(cAngle));
            _LeftSensorLine.start.Y = (float)(position.Y + LeftSensorPosition.X * Math.Sin(cAngle) + LeftSensorPosition.Y * Math.Cos(cAngle));
            Vector2 posEnd = new Vector2(LeftSensorPosition.X, LeftSensorPosition.Y + LeftSensorLength);
            _LeftSensorLine.stop.X = (float)(position.X + posEnd.X * Math.Cos(cAngle) - posEnd.Y * Math.Sin(cAngle));
            _LeftSensorLine.stop.Y = (float)(position.Y + posEnd.X * Math.Sin(cAngle) + posEnd.Y * Math.Cos(cAngle));

            for (int i = 0; i < LeftSensorLength / 5; i++)
            {
                Vector2 pos = Vector2.Lerp(_LeftSensorLine.start, _LeftSensorLine.stop, i * (1.0f / (LeftSensorLength / 5.0f)));
                if (_Map.isWallAt((int)pos.X, (int)pos.Y))
                    return Math.Max(0, (float)Math.Round((Vector2.Distance(_LeftSensorLine.start, pos) - 5), 2));
            }
            return LeftSensorLength - 5;
        }

        public float checkRightSensor(Vector2 position, float angle)
        {
            /**
             * Rotation Matrix
             * | sin(a) | -cos(a) |
             * | cos(a) |  sin(a) |
             */
            double cAngle = angle + Math.PI;
            _RightSensorLine.start.X = (float)(position.X + RightSensorPosition.X * Math.Cos(cAngle) - RightSensorPosition.Y * Math.Sin(cAngle));
            _RightSensorLine.start.Y = (float)(position.Y + RightSensorPosition.X * Math.Sin(cAngle) + RightSensorPosition.Y * Math.Cos(cAngle));
            Vector2 posEnd = new Vector2(RightSensorPosition.X, RightSensorPosition.Y + RightSensorLength);
            _RightSensorLine.stop.X = (float)(position.X + posEnd.X * Math.Cos(cAngle) - posEnd.Y * Math.Sin(cAngle));
            _RightSensorLine.stop.Y = (float)(position.Y + posEnd.X * Math.Sin(cAngle) + posEnd.Y * Math.Cos(cAngle));

            for (int i = 0; i < RightSensorLength / 5; i++)
            {
                Vector2 pos = Vector2.Lerp(_RightSensorLine.start, _RightSensorLine.stop, i * (1.0f / (RightSensorLength / 5.0f)));
                if (_Map.isWallAt((int)pos.X, (int)pos.Y))
                    return Math.Max(0, (float)Math.Round((Vector2.Distance(_RightSensorLine.start, pos) - 5), 2));
            }
            return RightSensorLength - 5;
        }

        public float checkMiddleSensor(Vector2 position, float angle)
        {
            /**
             * Rotation Matrix
             * | sin(a) | -cos(a) |
             * | cos(a) |  sin(a) |
             */
            double cAngle = angle + Math.PI;
            _MiddleSensorLine.start.X = (float)(position.X + MiddleSensorPosition.X * Math.Cos(cAngle) - MiddleSensorPosition.Y * Math.Sin(cAngle));
            _MiddleSensorLine.start.Y = (float)(position.Y + MiddleSensorPosition.X * Math.Sin(cAngle) + MiddleSensorPosition.Y * Math.Cos(cAngle));
            Vector2 posEnd = new Vector2(MiddleSensorPosition.X, MiddleSensorPosition.Y + MiddleSensorLength);
            _MiddleSensorLine.stop.X = (float)(position.X + posEnd.X * Math.Cos(cAngle) - posEnd.Y * Math.Sin(cAngle));
            _MiddleSensorLine.stop.Y = (float)(position.Y + posEnd.X * Math.Sin(cAngle) + posEnd.Y * Math.Cos(cAngle));

            for (int i = 0; i < MiddleSensorLength / 5; i++)
            {
                Vector2 pos = Vector2.Lerp(_MiddleSensorLine.start, _MiddleSensorLine.stop, i * (1.0f / (MiddleSensorLength / 5.0f)));
                if (_Map.isWallAt((int)pos.X, (int)pos.Y))
                    return Math.Max(0, (float)Math.Round((Vector2.Distance(_MiddleSensorLine.start, pos) - 5), 2));
            }
            return MiddleSensorLength - 5;
        }

        internal void Draw(ref SpriteBatch spriteBatch)
        {
            if (drawLeftLine)
            {
                for (int i = 0; i < LeftSensorLength / 5; i++)
                {
                    Vector2 pos = Vector2.Lerp(_LeftSensorLine.start, _LeftSensorLine.stop, i * (1.0f / (LeftSensorLength / 5.0f)));
                    float scale = 0.25f + (1.0f / (LeftSensorLength / 5.0f)) * 4f * i * 0.25f;

                    if (_Map.isWallAt((int)pos.X, (int)pos.Y))
                    {
                        spriteBatch.Draw(_RedInfra, pos, null, Color.White, 0.0f, new Vector2(5, 5), scale, SpriteEffects.None, 0);
                        break;
                    }
                    else
                        spriteBatch.Draw(_GreenInfra, pos, null, Color.White, 0.0f, new Vector2(5, 5), scale, SpriteEffects.None, 0);
                }
            }

            if (drawRightLine)
            {
                for (int i = 0; i < RightSensorLength / 5; i++)
                {
                    Vector2 pos = Vector2.Lerp(_RightSensorLine.start, _RightSensorLine.stop, i * (1.0f / (RightSensorLength / 5.0f)));
                    float scale = 0.25f + (1.0f / (RightSensorLength / 5.0f)) * 4f * i * 0.25f;
                    if (_Map.isWallAt((int)pos.X, (int)pos.Y))
                    {
                        spriteBatch.Draw(_RedInfra, pos, null, Color.White, 0.0f, new Vector2(5, 5), scale, SpriteEffects.None, 0);
                        break;
                    }
                    else
                        spriteBatch.Draw(_GreenInfra, pos, null, Color.White, 0.0f, new Vector2(5, 5), scale, SpriteEffects.None, 0);
                }
            }

            if (drawMiddleLine)
            {
                for (int i = 0; i < MiddleSensorLength / 5; i++)
                {
                    Vector2 pos = Vector2.Lerp(_MiddleSensorLine.start, _MiddleSensorLine.stop, i * (1.0f / (MiddleSensorLength / 5.0f)));
                    float scale = 0.25f + (1.0f / (MiddleSensorLength / 5.0f)) * 4f * i * 0.25f;
                    if (_Map.isWallAt((int)pos.X, (int)pos.Y))
                    {
                        spriteBatch.Draw(_RedInfra, pos, null, Color.White, 0.0f, new Vector2(5, 5), scale, SpriteEffects.None, 0);
                        break;
                    }
                    else
                        spriteBatch.Draw(_GreenInfra, pos, null, Color.White, 0.0f, new Vector2(5, 5), scale, SpriteEffects.None, 0);
                }
            }
        }

        internal void LoadContent(ContentManager ContentManager)
        {
            this._GreenInfra = ContentManager.Load<Texture2D>("greenI");
            this._RedInfra = ContentManager.Load<Texture2D>("redI");
        }
    }
}