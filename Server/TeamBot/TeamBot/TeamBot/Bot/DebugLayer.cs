﻿using System;
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

namespace teambot.Bot
{
    static class DebugLayer
    {
        static List<String> _MessageList = new List<string>();

        static Object _Locker = new object();
        static Object _DebugMapLocker = new object();

        private static short _DebugGridWidth;
        private static communication.DebugGridPoint[] _DebugMap = new communication.DebugGridPoint[0];

        const float leftOffset = 10.0f;
        const float spacing = .75f;

        public static SpriteFont BasicFont { get; set; }
        public static Texture2D DebugGridTexture { get; set; }

        static bool _draw = true;
        private static Color _WallColor;
        private static Color _InValidColor;
        private static Color _ValidColor;

        public static bool DebugActivated
        {
            get { return _draw; }
            set { _draw = value; }
        }

        /// <summary>
        /// Event handler would be cooler ;D 
        /// </summary>
        /// <param name="s"></param>
        /// <returns></returns>
        public static int addString(String s, int? index)
        {
            lock (_Locker)
            {
                if (index.HasValue)
                {
                    _MessageList[index.Value] = s;
                    return index.Value;
                }
                _MessageList.Add(s);
                return _MessageList.Count - 1;
            }
        }


        internal static void draw(ref SpriteBatch spriteBatch)
        {
            lock (_Locker)
            {
                if (_draw)
                {
                    float currentY = 0;
                    foreach (String s in _MessageList)
                    {
                        spriteBatch.DrawString(BasicFont, s, new Vector2(leftOffset, currentY), Color.OrangeRed);
                        currentY += spacing + BasicFont.MeasureString(s).Y;
                    }
                }
            }
            lock (_DebugMapLocker)
            {
                if (_draw)
                {
                    //float textureScale = 1 / (100 / _DebugGridWidth);
                    int gridScale = (int)((_DebugGridWidth / 10.0f) * Map.CmToPixel);
                    foreach (var point in _DebugMap)
                    {
                        int x = (int)((point.x / 10.0f) * Map.CmToPixel);
                        int y = (int)((point.y / 10.0f) * Map.CmToPixel);
                        Rectangle targetPosition = new Rectangle(x, y, gridScale, gridScale);
                        switch (point.status)
                        {
                            case communication.DebugGridPointStatus.Invalid:
                                _InValidColor.A = (byte)(point.alpha + 50);
                                spriteBatch.Draw(DebugGridTexture, targetPosition, _InValidColor);
                                break;
                            case communication.DebugGridPointStatus.Valid:
                                _ValidColor.A = (byte)(point.alpha + 50);
                                spriteBatch.Draw(DebugGridTexture, targetPosition, _ValidColor);
                                break;
                            case communication.DebugGridPointStatus.Wall:
                                _WallColor.A = (byte)(point.alpha + 50);
                                spriteBatch.Draw(DebugGridTexture, targetPosition, _WallColor);
                                break;
                            case communication.DebugGridPointStatus.Cyan:
                                _WallColor.A = (byte)(point.alpha + 50);
                                spriteBatch.Draw(DebugGridTexture, targetPosition, Color.DarkCyan);
                                break;
                        }

                    }
                }
            }
        }


        internal static void setDebugMap(communication.DebugGridPoint[] _Map, short _GridWidth)
        {
            _DebugMap = _Map;
            _DebugGridWidth = _GridWidth;
        }

        internal static void LoadContent(ContentManager contentManager)
        {
            BasicFont = contentManager.Load<SpriteFont>("BasicFont");
            DebugGridTexture = contentManager.Load<Texture2D>("Block10");
        }

        internal static void setDebugColors(Color ValidColor, Color InValidColor, Color WallColor)
        {
            _ValidColor = ValidColor;
            _InValidColor = InValidColor;
            _WallColor = WallColor;
        }
    }
}
