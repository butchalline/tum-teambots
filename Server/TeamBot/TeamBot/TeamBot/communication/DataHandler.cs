
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
        private double _Time;
        private IDataPrx _Proxy;
        private Ice.Communicator _Communicator;
        private Ice.ObjectAdapter _Adapter = null;

        private double lastAngle = 0;

        public DataHandler(Robot robot)
        {
            _Bot = robot;
            Ice.Properties props = Ice.Util.createProperties();
            //props.setProperty("Ice.Warn.Connections", "2");
            //props.setProperty("Ice.Trace.Protocol", "2");
            Ice.InitializationData initData = new Ice.InitializationData();
            initData.properties = props;
            _Communicator = Ice.Util.initialize(initData);
        }

        public void sendPositionFrame(short x, short y, short angle, GameTime gameTime)
        {
            TBPositionData positionData = new TBPositionData();
            positionData.Id = Constants.TB_DATA_ID;
            positionData.SubId = Constants.TB_DATA_POSITION;
            positionData.TimeStamp = (short)((long)gameTime.TotalGameTime.TotalMilliseconds & 0xFFFF);
            positionData.x = x;
            positionData.y = y;
            positionData.angle = angle;
            sendData(positionData);
        }

        public void sendPositionReachedFrame(short x, short y, short angle, GameTime gameTime)
        {
            TBPositionReached positionReachedData = new TBPositionReached();
            positionReachedData.Id = Constants.TB_DATA_ID;
            positionReachedData.SubId = Constants.TB_DATA_POSITION;
            positionReachedData.TimeStamp = (short)((long)gameTime.TotalGameTime.TotalMilliseconds & 0xFFFF);
            positionReachedData.x = x;
            positionReachedData.y = y;
            positionReachedData.angle = angle;
            sendData(positionReachedData);
        }

        public void sendInfraredFrame(byte left, byte middle, byte right, GameTime gameTime)
        {
            TBInfraredData infraredData = new TBInfraredData();
            infraredData.Id = Constants.TB_DATA_ID;
            infraredData.SubId = Constants.TB_DATA_INFRARED;
            infraredData.TimeStamp = (short)((long)gameTime.TotalGameTime.TotalMilliseconds & 0xFFFF);
            infraredData.leftDistance = left;
            infraredData.middleDistance = middle;
            infraredData.rightDistance = right;
            sendData(infraredData);
        }

        internal void update(GameTime gameTime)
        {
            //if(_Time <= gameTime.TotalGameTime.TotalMilliseconds) {
            sendInfraredFrame(_Bot.getLeftSensorDistance(), _Bot.getMiddleSensorDistance(), _Bot.getRightSensorDistance(), gameTime);

            if (_Bot.PositionReached)
            {
                if (_Bot.Angle > Math.PI || _Bot.Angle < -Math.PI)
                    sendPositionReachedFrame((short)(_Bot.Position.X * Map.PixelToCm * 10f), (short)(_Bot.Position.Y * Map.PixelToCm * 10f), (short)(MathHelper.ToDegrees(_Bot.Angle * 100)), gameTime);
                else
                    sendPositionReachedFrame((short)(_Bot.Position.X * Map.PixelToCm * 10f), (short)(_Bot.Position.Y * Map.PixelToCm * 10f), (short)(MathHelper.ToDegrees(_Bot.Angle * 100)), gameTime);
                
                _Bot.PositionReached = false;
            }
            else
            {
                if (_Bot.Angle > Math.PI || _Bot.Angle < -Math.PI)
                    sendPositionFrame((short)(_Bot.Position.X * Map.PixelToCm * 10f), (short)(_Bot.Position.Y * Map.PixelToCm * 10f), (short)(MathHelper.ToDegrees(_Bot.Angle * 100)), gameTime);
                else
                    sendPositionFrame((short)(_Bot.Position.X * Map.PixelToCm * 10f), (short)(_Bot.Position.Y * Map.PixelToCm * 10f), (short)(MathHelper.ToDegrees(_Bot.Angle * 100)), gameTime);
            }
            //    _Time = gameTime.TotalGameTime.TotalMilliseconds + 100.0;
            //}
        }

        public override void receive(TBFrame frame, Ice.Current current__)
        {
            System.Console.WriteLine("Received package");
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
                    _Bot.PositionRequested = true;
                    if (positionData.SubId == Constants.TB_POSITION_FORWARD)
                    {
                        _Bot.setPosition((ushort)positionData.distance);
                    }
                    else if (positionData.SubId == Constants.TB_POSITION_BACKWARD)
                    {
                        _Bot.setPosition(-(ushort)positionData.distance);
                    }
                    else if (positionData.SubId == Constants.TB_POSITION_TURN_LEFT)
                    {
                        _Bot.setAngle(-(ushort)positionData.distance);
                    }
                    else if (positionData.SubId == Constants.TB_POSITION_TURN_RIGHT)
                    {
                        _Bot.setAngle((ushort)positionData.distance);
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
                //props.setProperty("Ice.Warn.Connections", "2");
                //props.setProperty("Ice.Trace.Protocol", "2");
                Ice.InitializationData initData = new Ice.InitializationData();
                initData.properties = props;
                _Communicator = Ice.Util.initialize(initData);
                _Proxy = null;
            }
            if (_Adapter == null)
            {
                _Adapter = _Communicator.createObjectAdapterWithEndpoints("Simulator", "tcp -p 55001");
                _Adapter.add(this, _Communicator.stringToIdentity("simUsb")).ice_oneway();
                _Adapter.activate();
            }
            if (_Proxy == null)
            {
                _Proxy = IDataPrxHelper.uncheckedCast(_Communicator.stringToProxy("usb:tcp -h localhost -p 55000").ice_oneway());
            }
            try
            {
                _Proxy.receive(data);
            }
            catch (Exception e)
            {
                Console.WriteLine("Error DataHandler: " + e.Message);
            }
        }
    }
}
