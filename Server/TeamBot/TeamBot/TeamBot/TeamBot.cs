using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;

using teambot.Bot;

namespace teambot
{
    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class Simulator : Microsoft.Xna.Framework.Game
    {
        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;
        KeyboardState currentKState = Keyboard.GetState();
        KeyboardState previousKState = Keyboard.GetState();
        MouseState currentMState = Mouse.GetState();
        MouseState previousMState = Mouse.GetState();
        int? _debugIndex = null;

        Robot _Bot;
        Map _Map;
        communication.DataHandler _DataHandler;

        //ICE
        private Ice.Communicator _Communicator;
        private Ice.ObjectAdapter _Adapter;

        public Simulator()
        {
            graphics = new GraphicsDeviceManager(this);
        }

        private object locker = new Object();
        public GraphicsDevice getGraphicsDevice()
        {
            lock (locker)
            {
                return graphics.GraphicsDevice;
            }
        }



        private static Simulator _Reference;
        public static Simulator getReference()
        {
            return _Reference;
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            // TODO: Add your initialization logic here
            _Reference = this;
            this.IsMouseVisible = true;
            Mouse.WindowHandle = Window.Handle;
            graphics.PreferredBackBufferHeight = 800;
            graphics.PreferredBackBufferWidth = 800;

            Content.RootDirectory = "Content";
            if (!Map.DeserializeFromXML(out _Map, this.Content.RootDirectory))
            {
                _Map = new Map();
                _Map.initEmpty();
                Map.SerializeToXML(ref _Map, this.Content.RootDirectory);
            }
            _Bot = new Robot(_Map);
            _Bot.changeState(Robot.RobotStates.PositionMode);

            Ice.Properties properties = Ice.Util.createProperties();
            Ice.InitializationData initData = new Ice.InitializationData();
            initData.properties = properties;

            _Communicator = Ice.Util.initialize(initData);
            _Adapter = _Communicator.createObjectAdapterWithEndpoints("Simulator", "tcp -h localhost -p 55001");
            _DataHandler = new communication.DataHandler(_Bot);
            _Adapter.add(_DataHandler, _Communicator.stringToIdentity("Simulator"));
            _Adapter.activate();

            graphics.ApplyChanges();
            base.Initialize();
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);
            // TODO: use this.Content to load your game content here
            _Bot.LoadContent(this.Content);
            _Map.LoadContent(this.Content);
            DebugLayer.LoadContent(this.Content);
            DebugLayer.setDebugColors(new Color(0, 255, 0, 50), //green
            new Color(255, 0, 0, 50), //red
            new Color(255, 170, 0, 50)); //orange

            //Test debugLayeMap
        /*    communication.DebugGridPoint[] points = new communication.DebugGridPoint[100*100];
            Random random = new Random();
            for(int x = 0; x < 100; x++)
            {
                for(int y = 0; y < 100; y++)
                {
                    int i = random.Next(0,3);
                    switch (i)
                    {
                        case 0:
                            points[y * 100 + x] = new communication.DebugGridPoint(x + 50, y + 50, communication.DebugGridPointStatus.Wall);
                            break;
                        case 1:
                            points[y * 100 + x] = new communication.DebugGridPoint(x + 50, y + 50, communication.DebugGridPointStatus.Valid);
                            break;
                        case 2:
                            points[y * 100 + x] = new communication.DebugGridPoint(x + 50, y + 50, communication.DebugGridPointStatus.Invalid);
                            break;
                    }

                }
            }
            DebugLayer.setDebugMap(points, 50);    */
        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// all content.
        /// </summary>
        protected override void UnloadContent()
        {
            this.Content.Unload();
            // TODO: Unload any non ContentManager content here
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        
        
            
        protected override void Update(GameTime gameTime)
        {
            // Allows the game to exit
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back == ButtonState.Pressed)
                this.Exit();


            //Sensor s = new Sensor();
            //s.checkLeftSensor(ref map, Vector2.Zero,(float) Math.PI / 2.0f);
            //this.Exit();
            previousKState = currentKState;
            currentKState = Keyboard.GetState();
            previousMState = currentMState;
            currentMState = Mouse.GetState();

            _debugIndex = DebugLayer.addString("Mouse X: " + currentMState.X + " Y: " + currentMState.Y, _debugIndex);

            if (currentKState.IsKeyDown(Keys.LeftControl) && currentKState.IsKeyDown(Keys.D) && previousKState.IsKeyUp(Keys.D))
                DebugLayer.DebugActivated = !DebugLayer.DebugActivated;

            if (currentKState.IsKeyDown(Keys.LeftControl) && currentKState.IsKeyDown(Keys.G) && previousKState.IsKeyUp(Keys.G))
                _Map.drawGrid = !_Map.drawGrid;
            if (currentKState.IsKeyDown(Keys.LeftControl) && currentKState.IsKeyDown(Keys.M) && previousKState.IsKeyUp(Keys.M))
            {
                _Map.editMode = !_Map.editMode;
                if (!_Map.editMode)
                    Map.SerializeToXML(ref _Map, Content.RootDirectory);
            }

            if (_Map.editMode)
            {
                if (currentMState.LeftButton == ButtonState.Pressed && previousMState.LeftButton == ButtonState.Released)
                {
                    _Map.click(currentMState.X, currentMState.Y);
                }
            }


            // TODO: Add your update logic here
            _Bot.update(gameTime);
            _DataHandler.update(gameTime);

            base.Update(gameTime);
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.White);
            spriteBatch.Begin();
            _Map.draw(ref spriteBatch); //Grid should not be drawn inside of SpriteBatch.begin
            DebugLayer.draw(ref spriteBatch);
            _Bot.draw(ref spriteBatch);
            spriteBatch.End();
            base.Draw(gameTime);
        }
    }
}
