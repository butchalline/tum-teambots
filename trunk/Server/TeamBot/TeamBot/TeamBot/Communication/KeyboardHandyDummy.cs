using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework.Input;

namespace teambot.communication
{
    class KeyboardHandyDummy
    {

        const int maxVelocity = 0x3FF / 4;

        int velocityLeft = 0;
        int velocityRight = 0;

        internal void update(DataHandler toUpdate)
        {
            TBVelocity frame = new TBVelocity();
            frame.Id = Constants.TB_VELOCITY_ID;

            this.check();

            if (velocityLeft > 0 && velocityRight > 0)
                frame.SubId = Constants.TB_VELOCITY_FORWARD;
            else if (velocityLeft > 0 && velocityRight < 0)
                frame.SubId = Constants.TB_VELOCITY_TURN_RIGHT;
            else if (velocityLeft < 0 && velocityRight < 0)
                frame.SubId = Constants.TB_VELOCITY_BACKWARD;
            else if (velocityLeft < 0 && velocityRight > 0)
                frame.SubId = Constants.TB_VELOCITY_TURN_LEFT;

            frame.TimeStamp = 0;

            frame.speedLeft = (byte) Math.Abs(velocityLeft);
            frame.speedRight = (byte) Math.Abs(velocityRight);

            toUpdate.update(frame);
        }


        private void check()
        {
            if (velocityLeft > maxVelocity)
                velocityLeft = maxVelocity;
            else if (velocityLeft < -maxVelocity)
                velocityLeft = -maxVelocity;

            if (velocityRight > maxVelocity)
                velocityRight = maxVelocity;
            else if (velocityRight < -maxVelocity)
                velocityRight = -maxVelocity;
        }

        public void incForward()
        {
            velocityLeft += 10;
            velocityRight += 10;
        }

        public void incBackward()
        {
            velocityLeft -= 10;
            velocityRight -= 10;
        }

        public void incLeft()
        {
            velocityLeft -= 10;
            velocityRight += 10;
        }

        public void incRight()
        {
            velocityLeft += 10;
            velocityRight -= 10;
        }

        public void reset()
        {
            velocityRight = 0;
            velocityLeft = 0;
        }

        internal void updateInput(Microsoft.Xna.Framework.Input.KeyboardState currentKState, Microsoft.Xna.Framework.Input.KeyboardState previousKState)
        {
            if (currentKState.IsKeyDown(Keys.Up) && previousKState.IsKeyUp(Keys.Up))
                incForward();
            if (currentKState.IsKeyDown(Keys.Down) && previousKState.IsKeyUp(Keys.Down))
                incBackward();
            if (currentKState.IsKeyDown(Keys.Left) && previousKState.IsKeyUp(Keys.Left))
                incLeft();
            if (currentKState.IsKeyDown(Keys.Right) && previousKState.IsKeyUp(Keys.Right))
                incRight();

            if (currentKState.IsKeyDown(Keys.Space) && previousKState.IsKeyUp(Keys.Space))
                reset();
        }
    }
}
