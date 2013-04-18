
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using System.Threading;
using teambot.Bot;



namespace teambot.communication
{
    enum WheelDirection
    {
        Forwards,
        Backwards
    }


    class DataHandler : IDataServerDisp_
    {
        private Robot _Bot;
        private List<IDataClientPrx> _Clients;
        private double lastAngle = 0;

        class Receiver
        {
            private TBFrame _Frame;
            private Robot _Bot;
            private AMD_IDataServer_update _CallBack;
            public Receiver(ref TBFrame frame, ref Robot bot, ref AMD_IDataServer_update cb)
            {
                _Frame = frame;
                _Bot = bot;
                _CallBack = cb;
            }
            public void ThreadPoolCallback(Object threadContext)
            {
                _receiveMessage();
                _CallBack.ice_response();
            }
            private void _receiveMessage()
            {
                switch (_Frame.Id)
                {
                    case Constants.TB_COMMAND_ID:
                        break;
                    case Constants.TB_VELOCITY_ID:
                        if (!(_Frame is TBVelocity))
                            throw new Exception("Frame is not a Velocity Frame");
                        TBVelocity velocityData = (TBVelocity)_Frame;

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
                        if (!(_Frame is TBPosition))
                            throw new Exception("Frame is not a Position Frame");
                        TBPosition positionData = (TBPosition)_Frame;
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
        }

        class DebugReceiver
        {
            private DebugGridPoint[] _Map;
            private short _GridWidth;
            private AMD_IDataServer_debugMap _CallBack;
            public DebugReceiver(ref DebugGridPoint[] map, ref short gridwidth, ref AMD_IDataServer_debugMap cb)
            {
                _Map = map;
                _GridWidth = gridwidth;
            }
            public void ThreadPoolCallback(Object threadContext)
            {
                DebugLayer.setDebugMap(_Map, _GridWidth);
                _CallBack.ice_response();
            }
        }

        public DataHandler(Robot robot)
        {
            _Bot = robot;
            _Clients = new List<IDataClientPrx>();
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
            updateData(positionData);
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
            updateData(positionReachedData);
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
            updateData(infraredData);
        }

        internal void update(GameTime gameTime)
        {
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
        }

        public void updateData(TBFrame data)
        {
            List<IDataClientPrx> clients;
            lock (this)
            {
                clients = new List<IDataClientPrx>(_Clients);
            }
            foreach (var client in clients)
            {
                try
                {
                    client.begin_update(data);
                }
                catch (Ice.TimeoutException te)
                {
                    //DO Nothing! :D
                }
                catch (Ice.LocalException le)
                {
                    Console.Error.WriteLine("Remove Client because of: " + le.ToString());
                    lock (this)
                    {
                        _Clients.Remove(client);
                    }
                }
            }
           
        }


        public override void update_async(AMD_IDataServer_update cb__, TBFrame data, Ice.Current current__)
        {
            Receiver r = new Receiver(ref data, ref _Bot, ref cb__);
            ThreadPool.QueueUserWorkItem(r.ThreadPoolCallback);
        }

        public override void addClient(Ice.Identity ident, Ice.Current current__)
        {
            lock (this)
            {
                Ice.ObjectPrx @base = current__.con.createProxy(ident);
                IDataClientPrx client = IDataClientPrxHelper.uncheckedCast(@base);
                _Clients.Add(client);
            }
        }

        public override void debugMap_async(AMD_IDataServer_debugMap cb__, DebugGridPoint[] map, short gridWidth, Ice.Current current__)
        {
            DebugReceiver r = new DebugReceiver(ref map, ref gridWidth, ref cb__);
            ThreadPool.QueueUserWorkItem(r.ThreadPoolCallback);
        }
    }
}