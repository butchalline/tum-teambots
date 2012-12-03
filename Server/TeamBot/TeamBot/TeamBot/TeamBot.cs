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

using TeamBot.Bot;

namespace TeamBot
{
    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class TeamBot : Microsoft.Xna.Framework.Game
    {
        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;
        KeyboardState currentKState = Keyboard.GetState();
        KeyboardState previousKState = Keyboard.GetState();
        MouseState currentMState = Mouse.GetState();
        MouseState previousMState = Mouse.GetState();
        int? _debugIndex = null;

        Robot bot;
        Map map;
        Communication.DataHandler dataHandler;
        Communication.KeyboardHandyDummy HandyDummy = new Communication.KeyboardHandyDummy();


        public TeamBot()
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



        private static TeamBot _Reference;
        public static TeamBot getReference()
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
            bot = new Robot();
            dataHandler = new Communication.DataHandler(bot);
            if (!Map.DeserializeFromXML(out map, this.Content.RootDirectory))
            {
                map = new Map();
                map.initEmpty();
                Map.SerializeToXML(ref map, this.Content.RootDirectory);
            }
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
            bot.Texture = this.Content.Load<Texture2D>("TeamBot");
            DebugLayer.BasicFont = this.Content.Load<SpriteFont>("BasicFont");
            map.Wall = this.Content.Load<Texture2D>("Wall");
            map.effects = this.Content.Load<Effect>("effects");
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
            HandyDummy.update(dataHandler);
            bot.update(gameTime);

            base.Update(gameTime);
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.White);
            // TODO: Add your drawing code here
            spriteBatch.Begin();
            map.draw(ref spriteBatch);
            bot.draw(ref spriteBatch);
            DebugLayer.draw(ref spriteBatch);
            spriteBatch.End();
            base.Draw(gameTime);
        }
    }
}
