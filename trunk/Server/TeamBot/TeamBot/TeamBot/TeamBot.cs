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
        Map map;
        communication.DataHandler _DataHandler;
        communication.KeyboardHandyDummy HandyDummy = new communication.KeyboardHandyDummy();


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
            if (!Map.DeserializeFromXML(out map, this.Content.RootDirectory))
            {
                map = new Map();
                map.initEmpty();
                Map.SerializeToXML(ref map, this.Content.RootDirectory);
            }
            _Bot = new Robot(map);
            _Bot.changeState(Robot.RobotStates.VelocityMode);
            _DataHandler = new communication.DataHandler(_Bot);
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
            map.LoadContent(this.Content);
            DebugLayer.BasicFont = this.Content.Load<SpriteFont>("BasicFont");
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

            if (previousKState.IsKeyUp(Keys.Up) && currentKState.IsKeyDown(Keys.Up))
                HandyDummy.incForward();
            if (previousKState.IsKeyUp(Keys.Down) && currentKState.IsKeyDown(Keys.Down))
                HandyDummy.incBackward();
            if (previousKState.IsKeyUp(Keys.Left) && currentKState.IsKeyDown(Keys.Left))
                HandyDummy.incLeft();
            if (previousKState.IsKeyUp(Keys.Right) && currentKState.IsKeyDown(Keys.Right))
                HandyDummy.incRight();
            if (currentKState.IsKeyDown(Keys.Space))
                HandyDummy.reset();
            if (currentKState.IsKeyDown(Keys.LeftControl) && currentKState.IsKeyDown(Keys.D) && previousKState.IsKeyUp(Keys.D))
                DebugLayer.DebugActivated = !DebugLayer.DebugActivated;

            if (currentKState.IsKeyDown(Keys.LeftControl) && currentKState.IsKeyDown(Keys.G) && previousKState.IsKeyUp(Keys.G))
                map.drawGrid = !map.drawGrid;
            if (currentKState.IsKeyDown(Keys.LeftControl) && currentKState.IsKeyDown(Keys.M) && previousKState.IsKeyUp(Keys.M))
            {
                map.editMode = !map.editMode;
                if (!map.editMode)
                    Map.SerializeToXML(ref map, Content.RootDirectory);
            }

            if (map.editMode)
            {
                if (currentMState.LeftButton == ButtonState.Pressed && previousMState.LeftButton == ButtonState.Released)
                {
                    map.click(currentMState.X, currentMState.Y);
                }
            }


            // TODO: Add your update logic here
            HandyDummy.update(_DataHandler);
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
            GraphicsDevice.Clear(Color.LightGray);
            spriteBatch.Begin();
            map.draw(ref spriteBatch); //Grid should not be drawn inside of SpriteBatch.begin
            _Bot.draw(ref spriteBatch);
            DebugLayer.draw(ref spriteBatch);
            spriteBatch.End();
            base.Draw(gameTime);
        }
    }
}
