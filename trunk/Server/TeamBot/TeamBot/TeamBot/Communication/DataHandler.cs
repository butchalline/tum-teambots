
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using TeamBot.Bot;



namespace TeamBot.Communication
{
    enum WheelDirection
    {
        Forwards,
        Backwards
    }


    class DataHandler : IData
    {
        private Robot bot;

        public DataHandler(Robot robot)
        {
            bot = robot;
        }

        public void receive(TBFrame frame)
        {
            switch (frame.Id)
            {
                case Constants.TB_COMMAND_ID:
                    break;
                case Constants.TB_VELOCITY_ID:
                    if (!(frame is TBVelocity))
                        throw new Exception("Frame is not a Velocity Frame");
                    TBVelocity data = (TBVelocity) frame;
                    
                    if (data.SubId == Constants.TB_VELOCITY_FORWARD)
                    {
                        bot.setVelocity(data.speedLeft * 4, data.speedRight * 4, WheelDirection.Forwards, WheelDirection.Forwards);
                    }
                    else if (data.SubId == Constants.TB_VELOCITY_BACKWARD)
                    {
                        bot.setVelocity(data.speedLeft * 4, data.speedRight * 4, WheelDirection.Backwards, WheelDirection.Backwards);
                    }
                    else if (data.SubId == Constants.TB_VELOCITY_TURN_LEFT)
                    {
                        bot.setVelocity(data.speedLeft * 4, data.speedRight * 4, WheelDirection.Backwards, WheelDirection.Forwards);
                    }
                    else if (data.SubId == Constants.TB_VELOCITY_TURN_RIGHT)
                    {
                        bot.setVelocity(data.speedLeft * 4, data.speedRight * 4, WheelDirection.Forwards, WheelDirection.Backwards);
                    }
                    
                    break;
                case Constants.TB_POSITION_ID:
                    break;
                case Constants.TB_ERROR_ID:
                    break;                
            }
        }
    }
}
