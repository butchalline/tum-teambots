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

namespace TeamBot.Bot
{
    class Sensor
    {
        public struct Line
        {
            public double m, b, angle;
            public Vector2 position;
            public Texture2D color;
        }

        Line leftSensorLine;
        Line rightSensorLine;
        Line middleSensorLine;

        public Sensor()
        {
            drawLeftLine = true;
            drawRightLine = false;
            drawMiddleLine = false;
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

        public Texture2D RedInfra
        {
            get;
            set;
        }

        Texture2D _GreenInfra;
        public Texture2D GreenInfra
        {
            get { return _GreenInfra; } 
            set
            {
                _GreenInfra = value;
                leftSensorLine.color = _GreenInfra;
                rightSensorLine.color = _GreenInfra;
                middleSensorLine.color = _GreenInfra;
            }
        }



        const int leftSensorLength = 20;
        const int rightSensorLength = 20;
        const int middleSensorLenth = 120;
        const double RadToGrad = 180.0 / Math.PI;




        public void calcLine(ref Line line, Vector2 position, double angle)
        {
            line.angle = angle;
            line.m = -1 / Math.Round(Math.Tan(angle), 5);
            line.b = position.Y  - position.X * line.m;
        }

        public float checkLeftSensor(Vector2 position, double angle)
        {
            leftSensorLine.position = new Vector2((float)(-60 * Math.Cos(angle + Math.PI / 2) + 60 * Math.Sin(angle + Math.PI / 2) + position.X), (float)(0 * Math.Sin(angle + Math.PI / 2) + 0 * Math.Cos(angle + Math.PI / 2) + position.Y));
            if (angle == Math.PI / 2)
            {
                //TODO: Vertical Line
            }
            calcLine(ref leftSensorLine, position, angle);
            
            if (angle < 0)
            {

            }
            else
            {

            }

            return 0.0f;
            
        }

        public float checkRightSensor(ref Map map, Vector2 position, float angle)
        {
            return 0.0f;

        }

        public float checkMiddleSensor()
        {
            return 0.0f;
        }

        internal void Draw(ref SpriteBatch spriteBatch)
        {
            if(drawLeftLine)
            {
                Vector2 drawLine = new Vector2(leftSensorLine.position.X, leftSensorLine.position.Y);
                for (int i = 0; i < leftSensorLength; i += 5)
                {
                    drawLine.X = leftSensorLine.angle < 0 ? drawLine.X - 5f : drawLine.X + 5f;
                    spriteBatch.Draw(leftSensorLine.color, new Vector2(drawLine.X, (float)(drawLine.X * leftSensorLine.m + leftSensorLine.b)), null, Color.White, 0.0f, new Vector2(leftSensorLine.color.Width / 2, leftSensorLine.color.Height / 2), (3.0f / leftSensorLength / 5) * (i / 5) + .5f, SpriteEffects.None, 0);
                }
            }

            if(drawRightLine)
            {
                for (int i = 0; i < rightSensorLength; i += 5)
                {
                }
            }

            if(drawMiddleLine)
            {
                for (int i = 0; i < middleSensorLenth; i += 5)
                {
                }
            }
        }
    }
}