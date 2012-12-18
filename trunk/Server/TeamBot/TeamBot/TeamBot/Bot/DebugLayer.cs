using System;
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

namespace TeamBot.Bot
{
    static class DebugLayer
    {
        static List<String> _MessageList = new List<string>();

        static Object locker = new object();

        const float leftOffset = 10.0f;
        const float spacing = .75f;

        public static SpriteFont BasicFont
        {
            get;
            set;
        }

        static bool _draw = true;
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
            lock (locker)
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
            lock (locker)
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
        }
    }
}
