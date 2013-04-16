using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace teambot.communication
{
    class KeyboardHandyDummy
    {

        const int maxVelocity = 0x3FF / 4;

        int velocityLeft = 0;
        int velocityRight = 0;

        internal void update(IDataServer toUpdate)
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

            //toUpdate.receive(frame);
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
    }
}
