﻿
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using teambot.Bot;



namespace teambot.communication
{
    enum WheelDirection
    {
        Forwards,
        Backwards
    }


    class DataHandler : IDataDisp_
    {
        private Robot _Bot;
        private int _Time;
        private IDataPrx _Proxy;
        private Ice.Communicator _Communicator;

        public DataHandler(Robot robot)
        {
            _Bot = robot;
            Ice.Properties props = Ice.Util.createProperties();
            props.setProperty("Ice.Warn.Connections", "2");
            props.setProperty("Ice.Trace.Protocol", "2");
            Ice.InitializationData initData = new Ice.InitializationData();
            initData.properties = props;
            _Communicator = Ice.Util.initialize(initData);
        }

        public void sendInfraredFrame(byte left, byte middle, byte right)
        {
            TBInfraredData infraredData = new TBInfraredData();
            infraredData.Id = Constants.TB_DATA_ID;
            infraredData.SubId = Constants.TB_DATA_INFRARED;
            infraredData.TimeStamp = 0;
            infraredData.leftDistance = left;
            infraredData.middleDistance = middle;
            infraredData.rightDistance = right;
            sendData(infraredData);
        }

        internal void update(GameTime gameTime)
        {
            _Time += gameTime.ElapsedGameTime.Milliseconds;

            if (_Time > 100)
            {
                _Time = 0;
                sendInfraredFrame(_Bot.getLeftSensorDistance(), _Bot.getMiddleSensorDistance(), _Bot.getRightSensorDistance());
            }
        }

        public override void receive(TBFrame frame, Ice.Current current__)
        {
            switch (frame.Id)
            {
                case Constants.TB_COMMAND_ID:
                    break;
                case Constants.TB_VELOCITY_ID:
                    if (!(frame is TBVelocity))
                        throw new Exception("Frame is not a Velocity Frame");
                    TBVelocity velocityData = (TBVelocity)frame;

                    if (velocityData.SubId == Constants.TB_VELOCITY_FORWARD)
                    {
                        _Bot.setVelocity(velocityData.speedLeft * 4, velocityData.speedRight * 4, WheelDirection.Forwards, WheelDirection.Forwards);
                    }
                    else if (velocityData.SubId == Constants.TB_VELOCITY_BACKWARD)
                    {
                        _Bot.setVelocity(velocityData.speedLeft * 4, velocityData.speedRight * 4, WheelDirection.Backwards, WheelDirection.Backwards);
                    }
                    else if (velocityData.SubId == Constants.TB_VELOCITY_TURN_LEFT)
                    {
                        _Bot.setVelocity(velocityData.speedLeft * 4, velocityData.speedRight * 4, WheelDirection.Backwards, WheelDirection.Forwards);
                    }
                    else if (velocityData.SubId == Constants.TB_VELOCITY_TURN_RIGHT)
                    {
                        _Bot.setVelocity(velocityData.speedLeft * 4, velocityData.speedRight * 4, WheelDirection.Forwards, WheelDirection.Backwards);
                    }
                    break;
                case Constants.TB_POSITION_ID:
                    if (!(frame is TBPosition))
                        throw new Exception("Frame is not a Position Frame");
                    TBPosition positionData = (TBPosition)frame;
                    if (positionData.SubId == Constants.TB_POSITION_FORWARD)
                    {
                        _Bot.setPosition(positionData.distance);
                    }
                    else if (positionData.SubId == Constants.TB_POSITION_BACKWARD)
                    {
                        _Bot.setPosition(-positionData.distance);
                    }
                    else if (positionData.SubId == Constants.TB_POSITION_TURN_LEFT)
                    {
                        _Bot.setAngle(-positionData.distance);
                    }
                    else if (positionData.SubId == Constants.TB_POSITION_TURN_RIGHT)
                    {
                        _Bot.setAngle(positionData.distance);
                    }
                    break;
                case Constants.TB_ERROR_ID:
                    break;
            }
        }

        public void sendData(TBFrame data)
        {
            if (_Communicator.isShutdown())
            {
                Ice.Properties props = Ice.Util.createProperties();
                props.setProperty("Ice.Warn.Connections", "2");
                props.setProperty("Ice.Trace.Protocol", "2");
                Ice.InitializationData initData = new Ice.InitializationData();
                initData.properties = props;
                _Communicator = Ice.Util.initialize(initData);
                _Proxy = null;
            }
            if (_Proxy == null)
            {
                _Proxy = IDataPrxHelper.uncheckedCast(_Communicator.stringToProxy("hello:tcp -p 10000").ice_oneway());
            }
            try
            {
            //    _Proxy.receive(data);
            }
            catch(Exception e)
            {
            //   Console.WriteLine("Error DataHandler: " + e.Message);
            }
        }
    }
}