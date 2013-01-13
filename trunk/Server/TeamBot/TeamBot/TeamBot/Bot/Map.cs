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
using System.Xml.Serialization;

using teambot.communication;
using System.IO;

namespace teambot.Bot
{
    public class Map
    {
        [XmlIgnore]
        private Texture2D _WallTexture;

        [XmlIgnore]
        private Effect _Effects;

        [XmlIgnore]
        public bool drawGrid
        {
            get;
            set;
        }

        [XmlIgnore]
        public bool editMode { get; set; }

        [XmlIgnore]
        public const float PixelToCm = 1000 / 800;

        public class Field
        {
            [XmlAttribute]
            public int x 
            {
                get; set;
            }
            [XmlAttribute]
            public int y
            {
                get;
                set;
            }
            [XmlAttribute]
            public int value
            {
                get;
                set;
            }
        }

        /// <summary>
        /// The description of the Walls in the Map
        /// </summary>
        [XmlArray("Map")]
        public Field[] _map;

        /// <summary>
        /// Line Grid
        /// </summary>
        private VertexPositionColor[] lineGrid;
        private Matrix viewMatrix;
        private Matrix projectionMatrix;
        


        public Map()
        {
            drawGrid = false;
            editMode = false;
            lineGrid = new VertexPositionColor[78*2];
            viewMatrix = Matrix.CreateLookAt(new Vector3(0.0f, 0.0f, 1.0f), Vector3.Zero, Vector3.Up);
            projectionMatrix = Matrix.CreateOrthographicOffCenter(0, 1000, 0, 1000, 1, 1000.0f);

            _map = new Field[40 * 40];
            for (int x = 1; x < 40; x++)
            {
                //vertical lines
                lineGrid[((x - 1) * 2)] =     new VertexPositionColor(new Vector3(x * 0.05f - 1,   1.0f, 0), Color.YellowGreen);
                lineGrid[((x - 1) * 2) + 1] = new VertexPositionColor(new Vector3(x * 0.05f - 1 , -1.0f, 0), Color.YellowGreen);
            }
            for (int y = 1; y < 40; y++)
            {               
                //horizontal lines
                lineGrid[((y + 38) * 2)] =     new VertexPositionColor(new Vector3( 1.0f, y * 0.05f - 1, 0), Color.YellowGreen);
                lineGrid[((y + 38) * 2) + 1] = new VertexPositionColor(new Vector3(-1.0f, y * 0.05f - 1, 0), Color.YellowGreen);
            }
        }

        public void initEmpty()
        {
            for (int x = 0; x < 40; x++)
            {
                for (int y = 0; y < 40; y++)
                {
                    _map[y * 40 + x] = new Field();
                    _map[y * 40 + x].value = 0;
                    _map[y * 40 + x].x = x;
                    _map[y * 40 + x].y = y;
                }
            }
        }


        public bool isWallAt(int xPixel, int yPixel)
        {
            int x = xPixel / 20;
            int y = yPixel / 20;
            Field field = (from f in _map where (f.x == x && f.y == y) select f).FirstOrDefault();
            if (field != null)
                return field.value == 1;
            return false;
        }


        internal void click(int p, int p_2)
        {
            int x = p / 20;
            int y = p_2 / 20;
            Field field = (from f in _map where (f.x == x && f.y == y) select f).FirstOrDefault();
            if(field != null)
                field.value = (field.value == 0) ? 1 : 0;

        }

        static public void SerializeToXML(ref Map map, String path)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(Map));
            TextWriter textWriter = new StreamWriter(path + "/mapData.xml");
            serializer.Serialize(textWriter, map);
            textWriter.Close();
        }

        static public bool DeserializeFromXML(out Map map, String path)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(Map));
            try
            {
                TextReader textReader = new StreamReader(path + "/mapData.xml");
                map = serializer.Deserialize(textReader) as Map;
                textReader.Close();
                return true;
            }
            catch (FileNotFoundException)
            {
                map = null;
                return false;
            }
            catch (Exception)
            {
                map = null;
                return false;
            }
        }

        internal void draw(ref SpriteBatch spriteBatch)
        {

            for (int i = 0; i < 40 * 40; i++)
            {
                Field f = _map[i];
                if(f.value != 0)
                    spriteBatch.Draw(_WallTexture, new Vector2(f.x * _WallTexture.Width, f.y * _WallTexture.Height), Color.RosyBrown);
            }

            if (drawGrid)
            {
                _Effects.CurrentTechnique = _Effects.Techniques["Pretransformed"];
                foreach (EffectPass pass in _Effects.CurrentTechnique.Passes)
                {
                    pass.Apply();
                    spriteBatch.GraphicsDevice.DrawUserPrimitives<VertexPositionColor>(PrimitiveType.LineList, lineGrid, 0, 78);
                }
            }

        }



        internal void LoadContent(ContentManager contentManager)
        {
            _WallTexture = contentManager.Load<Texture2D>("Wall");
            _Effects = contentManager.Load<Effect>("effects");
        }
    }

}
